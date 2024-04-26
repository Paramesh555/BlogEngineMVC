package org.example.blogenginemvc.controllers;

import jakarta.servlet.http.HttpSession;
import org.example.blogenginemvc.models.Account;
import org.example.blogenginemvc.models.Comment;
import org.example.blogenginemvc.models.Post;
import org.example.blogenginemvc.services.ServiceFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class CommentController {
//    @Autowired CommentService commentService;
//    @Autowired AccountService accountService;
//    @Autowired
//    PostService postService;

    private final ServiceFactory serviceFactory;

    public CommentController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }


    @PostMapping("/posts/{id}/comment")
    public String saveNewComment(@PathVariable Long id, @ModelAttribute Comment comment, HttpSession session) {
        comment.setId(null);
        Optional<Account> optionalAccount = serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        Optional<Post> optionalPost = serviceFactory.createPostService().getById(id);
        if (optionalAccount.isPresent() && optionalPost.isPresent()) {
            comment.setAccount(optionalAccount.get());
            comment.setPost(optionalPost.get());
            serviceFactory.createCommentService().save(comment);
            return "redirect:/posts/" + optionalPost.get().getId();
        } else {
            return "404";
        }
    }

    @GetMapping("/posts/{pid}/comment/{cid}/delete")
    public String deletePost(@PathVariable Long pid, @PathVariable Long cid, HttpSession session) {
        Optional<Comment> optionalComment = serviceFactory.createCommentService().getById(cid);
        Optional<Account> optionalAccount = serviceFactory.createAccountService().findByEmail(((Account)session.getAttribute("account")).getEmail());
        if (optionalComment.isPresent() && (optionalAccount.get() == optionalComment.get().getAccount() || optionalAccount.get().getAuthority().equals("ADMIN"))) {
            Comment comment = optionalComment.get();
            serviceFactory.createCommentService().delete(comment);
        }
        return "redirect:/posts/" + pid;
    }
}
