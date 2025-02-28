package com.bank;


class SavingsAccount extends Account {
    private final double withdrawalLimit = 500;

    public SavingsAccount(double initialBalance) {
        super(initialBalance);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > withdrawalLimit) {
            System.out.println("Savings Account: Withdrawal amount exceeds limit of " + withdrawalLimit);
            return false;
        }
        if (amount > balance) {
            System.out.println("Savings Account has insufficient funds");
            return false;
        }
        balance -= amount;
        System.out.println("Savings Account- Withdrawn" + amount + ",New Balance -" + balance);
        return true;
    }
}
