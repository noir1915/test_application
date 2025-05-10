package com.example.test_application.exception;

public class TransferException extends RuntimeException { // или Exception, если вы хотите сделать его проверяемым
    public TransferException(String message) {
        super(message);
    }
}
