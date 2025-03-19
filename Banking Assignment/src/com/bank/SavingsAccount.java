package com.bank;

public class SavingsAccount extends Account {
    private final double withdrawalLimit = 500;

    public SavingsAccount(double initialBalance) {
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