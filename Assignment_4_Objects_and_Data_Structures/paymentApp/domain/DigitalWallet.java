package com.paymentApp.domain;

public class DigitalWallet {
    private float balance;

    public DigitalWallet(float initialBalance) {
        this.balance = initialBalance;
    }

    public float getBalance() {
        return balance;
    }

    public void add(float amount) {
        balance += amount;
    }

    public void deduct(float amount) {
        balance -= amount;
    }

    public boolean hasSufficientBalance(float amount) {
        return balance >= amount;
    }
}

