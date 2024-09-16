package ru.storage.exception;

public class UsernameAlreadyExistException extends RuntimeException {
    public UsernameAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
