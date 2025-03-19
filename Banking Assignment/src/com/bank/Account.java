package com.bank;

public abstract class Account {
    protected double balance;
    protected String accountNumber;
    private static int nextAccountNumber = 1000;

    public Account(double initialBalance) {
        this.balance = initialBalance;
        this.accountNumber = "AC" + nextAccountNumber++;
    }

    public String getAccountNumber() { return accountNumber; }

    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            System.out.println("Deposited: " + amount);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public boolean transfer(Account target, double amount) {
        if (this.withdraw(amount)) {
            target.deposit(amount);
            System.out.println("Transferred " + amount + " from " + accountNumber + " to " + target.accountNumber);
            return true;
        }
        System.out.println("Transfer failed.");
        return false;
    }

    public abstract boolean withdraw(double amount);

    public void displayDetails() {
        System.out.println("Account No: " + accountNumber + ", Balance: " + balance);
    }
}
