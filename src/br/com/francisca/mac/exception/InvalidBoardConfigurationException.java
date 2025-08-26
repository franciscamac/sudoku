package br.com.francisca.mac.exception;

public class InvalidBoardConfigurationException extends RuntimeException {
    public InvalidBoardConfigurationException(String message) {
        super(message);
    }
    public InvalidBoardConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
