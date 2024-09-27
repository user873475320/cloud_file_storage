package ru.storage.exception;

public class MissingQueryParameterException extends RuntimeException{
    public MissingQueryParameterException(String message) {
        super(message);
    }
}
