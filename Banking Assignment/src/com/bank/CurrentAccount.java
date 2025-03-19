package com.bank;

public class CurrentAccount extends Account {
    private final double withdrawalLimit = 2500;

    public CurrentAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > withdrawalLimit || amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }
}