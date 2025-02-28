package com.bank;


class CurrentAccount extends Account {
    private final double withdrawalLimit = 2500;

    public CurrentAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > withdrawalLimit) {
            System.out.println("CurrentAccount: Withdrawal amount exceeds limit of " + withdrawalLimit);
            return false;
        }
        if (amount > balance) {
            System.out.println("CurrentAccount: Insufficient funds.");
            return false;
        }
        balance -= amount;
        System.out.println("CurrentAccount: Withdrawn " + amount + ", New Balance: " + balance);
        return true;
    }
}
