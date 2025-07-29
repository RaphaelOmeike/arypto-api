package com.codewithmosh.arypto.exceptions;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("Insufficient balance in wallet");
    }
}
