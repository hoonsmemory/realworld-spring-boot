package io.hoon.realworld.exception;

import lombok.Getter;

@Getter
public enum Error {
    USER_NOT_FOUND("No user exists with the provided email."),
    INVALID_EMAIL_OR_PASSWORD("The email or password does not match."),
    DUPLICATE_DATA("A unique constraint or primary key constraint has been violated due to duplicate data."),
    MULTIPLE_TOKENS_FOUND("Found multiple bearer tokens in the request."),
    CONTACT_ADMIN("Please contact the administrator.");

    private final String message;

    Error(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
