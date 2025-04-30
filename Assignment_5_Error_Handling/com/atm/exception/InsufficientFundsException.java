package com.atm.exception;

public class InsufficientFundsException extends ATMException {
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}