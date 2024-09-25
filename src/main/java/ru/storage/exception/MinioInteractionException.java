package ru.storage.exception;

public class MinioInteractionException extends RuntimeException {
    public MinioInteractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
