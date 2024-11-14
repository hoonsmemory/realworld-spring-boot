package io.hoon.realworld.exception;

import lombok.Getter;

@Getter
public enum Error {
    USER_NOT_FOUND("No user exists with the provided data."),
    INVALID_EMAIL_OR_PASSWORD("The email or password does not match."),
    DUPLICATE_DATA("A unique constraint or primary key constraint has been violated due to duplicate data."),
    MULTIPLE_TOKENS_FOUND("Found multiple bearer tokens in the request."),
    CONTACT_ADMIN("Please contact the administrator."),
    EMAIL_ALREADY_EXIST("The email already exists."),
    ALREADY_FOLLOWING_USER("Already following the user"),
    ARTICLE_NOT_FOUND("No aricle exists with the provided slug."),
    FAILED_TO_DELETE("Failed to delete the article."),
    INVALID_TOKEN("Invalid token: You are not authorized to perform this action."),
    ALREADY_FAVORITED("Already favorited the article."),
    NOT_FAVORITED("Not favorited the article."),;


    private final String message;

    Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
