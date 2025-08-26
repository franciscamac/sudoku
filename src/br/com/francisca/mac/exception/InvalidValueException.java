package br.com.francisca.mac.exception;

public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String message) {
        super(message);
    }
    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
}
