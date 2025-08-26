package br.com.francisca.mac.exception;

public class FixedCellModificationException extends RuntimeException {
    public FixedCellModificationException(String message) {
        super(message);
    }
    public FixedCellModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}