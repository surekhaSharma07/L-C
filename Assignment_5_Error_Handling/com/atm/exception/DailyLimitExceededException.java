package com.atm.exception;

public class DailyLimitExceededException extends ATMException {
    public DailyLimitExceededException(String msg) {
        super(msg);
    }
}