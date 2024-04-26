package org.example.blogenginemvc.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.blogenginemvc.models.Account;
import org.example.blogenginemvc.models.Comment;
import org.example.blogenginemvc.models.Post;
import org.example.blogenginemvc.services.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class PostController {
//    @Autowired
//    private PostService postService;
//    @Autowired AccountService accountService;
//    @Autowired CommentService commentService;

    private final ServiceFactory serviceFactory;

    public PostController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        List<Comment> comments = serviceFactory.createCommentService().getAllByPost(id);
        Optional<Post> optionalPost = serviceFactory.createPostService().getById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Comment comment = new Comment();
            model.addAttribute("comment", comment);
            model.addAttribute("comments", comments);
            model.addAttribute("post", post);
            return "post";
        } else {
            return "404";
        }
    }

    @GetMapping("/myfeed")
    public String getMyFeed(Model model, HttpSession session) {
        List<Post> posts = serviceFactory.createPostService().getAll();
        Set<Account> followingList =  serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail()).get().getFollowings();
        List<Post> myFeed = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            if (followingList.contains(posts.get(i).getAccount())) {
                myFeed.add(posts.get(i));
            }
        }
        Collections.sort(myFeed, new PostComparator());
        model.addAttribute("myfeed", myFeed);
        return "myfeed";
    }

    @GetMapping("/posts/new")
    public String createNewPost(Model model, HttpSession session) {
        Optional<Account> optionalAccount;
        if (session.getAttribute("account") != null) {
            optionalAccount = serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        } else {
            return "redirect:/login";
        }
        if (optionalAccount.isPresent()) {
            Post post = Post.builder().build();
            model.addAttribute("post", post);
            return "post_new";
        } else {
            return "redirect:/login";
        }
    }
    
    @PostMapping("/posts/new")
    public String saveNewPost(@ModelAttribute Post post, HttpSession session) {
        Optional<Account> optionalAccount =  serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        if (optionalAccount.isPresent()) {
            post.setAccount(optionalAccount.get());
            serviceFactory.createPostService().save(post);
            return "redirect:/posts/" + post.getId();
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/{id}/edit")
    public String editPost(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Post> optionalPost = serviceFactory.createPostService().getById(id);
        Optional<Account> optionalAccount =  serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        if (optionalPost.isPresent() && optionalAccount.get() == optionalPost.get().getAccount()) {
            Post post = optionalPost.get();
            model.addAttribute("post", post);
            return "post_edit";
        } else {
            return "404";
        }
    }
    
    @PostMapping("/posts/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute Post post, HttpSession session) {
        Optional<Post> optionalPost = serviceFactory.createPostService().getById(id);
        Optional<Account> optionalAccount =  serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        Post originalPost = optionalPost.get();
        if (originalPost.getTitle() == post.getTitle() && originalPost.getBody() == post.getBody()) {
            return "404";
        } else {
            post.setAccount(optionalAccount.get());
            serviceFactory.createPostService().save(post);
            return "redirect:/posts/" + post.getId();
        }
    }

    @GetMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, HttpSession session) {
        Optional<Post> optionalPost = serviceFactory.createPostService().getById(id);
        Optional<Account> optionalAccount = serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        if (optionalPost.isPresent() && (optionalAccount.get() == optionalPost.get().getAccount() || optionalAccount.get().getAuthority().equals("ADMIN"))) {
            Post post = optionalPost.get();
            serviceFactory.createPostService().delete(post);
        }
        return "redirect:/";
    }

    class PostComparator implements Comparator<Post> {
        @Override
        public int compare(Post post1, Post post2) {
            return post2.getUpdatedAt().compareTo(post1.getUpdatedAt());
        }
    }
}