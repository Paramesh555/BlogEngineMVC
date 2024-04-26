package org.example.blogenginemvc.services;

public interface ServiceFactory {
    PostService createPostService();
    AccountService createAccountService();
    CommentService createCommentService();
}


