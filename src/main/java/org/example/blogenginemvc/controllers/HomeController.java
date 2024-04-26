package org.example.blogenginemvc.controllers;

import org.example.blogenginemvc.models.Account;
import org.example.blogenginemvc.models.Post;
import org.example.blogenginemvc.services.AccountService;
import org.example.blogenginemvc.services.PostService;
import org.example.blogenginemvc.services.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {
//    @Autowired
//    private PostService postService;
//    @Autowired
//    private AccountService accountService;

    private final ServiceFactory serviceFactory;

    public HomeController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GetMapping("/")
    public String Home(Model model, HttpSession session) {
//        List<Post> posts = postService.getAll();
        List<Post> posts =serviceFactory.createPostService().getAll();
        Collections.sort(posts, new PostComparator());
        if (session.getAttribute("isLoggedIn") == null) {
            session.setAttribute("isLoggedIn", false);
            session.setAttribute("account", null);
        }
        model.addAttribute("posts", posts);
        return "home";
    }

    @PostMapping("/search")
    public String searchPost(@RequestParam(name = "query") String query, Model model) {
        List<Post> searchResults = serviceFactory.createPostService().searchPostsByTitle(query);
        Collections.sort(searchResults, new PostComparator());
        model.addAttribute("searchResults", searchResults);
        return "search_post";
    }

    @PostMapping("/account")
    public String searchAccount(@RequestParam(name = "account") String account, Model model) {
        List<Account> searchedAccounts = serviceFactory.createAccountService().searchAccountsByEmail(account);
        if (searchedAccounts.size() != 0) {
            model.addAttribute("searchedAccounts", searchedAccounts);
            model.addAttribute("NoAccount", false);
        } else {
            model.addAttribute("searchedAccounts", searchedAccounts);
            model.addAttribute("NoAccount", true);
        }
        return "search_account";
    }

    class PostComparator implements Comparator<Post> {
        @Override
        public int compare(Post post1, Post post2) {
            return post2.getUpdatedAt().compareTo(post1.getUpdatedAt());
        }
    }
}