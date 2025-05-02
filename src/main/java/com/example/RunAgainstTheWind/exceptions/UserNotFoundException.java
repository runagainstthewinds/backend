package com.example.RunAgainstTheWind.exceptions;

public final class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
