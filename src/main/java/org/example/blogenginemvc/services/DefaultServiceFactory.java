package org.example.blogenginemvc.services;

import org.springframework.stereotype.Component;

@Component
public class DefaultServiceFactory implements ServiceFactory {
    private final PostService postService;
    private final AccountService accountService;
    private final CommentService commentService;

    public DefaultServiceFactory(PostService postService, AccountService accountService,CommentService commentService) {
        this.postService = postService;
        this.accountService = accountService;
        this.commentService = commentService;
    }


    public PostService createPostService() {
        return postService;
    }

    @Override
    public AccountService createAccountService() {
        return accountService;
    }

    public CommentService createCommentService() {
        return commentService;
    }


}
