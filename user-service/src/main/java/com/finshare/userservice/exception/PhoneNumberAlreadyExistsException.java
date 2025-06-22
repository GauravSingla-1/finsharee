package com.finshare.userservice.exception;

/**
 * Exception thrown when attempting to create a user with a phone number that already exists.
 */
public class PhoneNumberAlreadyExistsException extends RuntimeException {

    public PhoneNumberAlreadyExistsException(String message) {
        super(message);
    }

    public PhoneNumberAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}