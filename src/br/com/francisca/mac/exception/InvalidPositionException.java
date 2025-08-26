package br.com.francisca.mac.exception;

public class InvalidPositionException extends RuntimeException {
    public InvalidPositionException(String message) {
        super(message);
    }
    public InvalidPositionException(String message, Throwable cause) {
        super(message, cause);
    }
}