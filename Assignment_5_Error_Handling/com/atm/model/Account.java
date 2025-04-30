package com.atm.model;

import com.atm.exception.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String accountNumber;
    private final Card card;
    private double balance;
    private double dailyLimit;
    private double withdrawnToday;
    private LocalDate lastWithdrawDate;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountNumber, Card card, double balance, double dailyLimit) {
        this.accountNumber = accountNumber;
        this.card = card;
        this.balance = balance;
        this.dailyLimit = dailyLimit;
        this.lastWithdrawDate = LocalDate.now();
    }

    public static Account createTestAccount(String accNum, String pin) {
        Card card = new Card(pin, LocalDate.now().plusYears(2));
        return new Account(accNum, card, 10000.0, 5000.0);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Card getCard() {
        return card;
    }

    public double getBalance() {
        return balance;
    }


    public void resetLimitIfNewDay() {
        if (!lastWithdrawDate.equals(LocalDate.now())) {
            withdrawnToday = 0;
            lastWithdrawDate = LocalDate.now();
        }
    }

    public void withdraw(double amount) throws ATMException {
        resetLimitIfNewDay();

        if (withdrawnToday + amount > dailyLimit) {
            throw new DailyLimitExceededException("Daily limit exceeded.");
        }
        if (amount > balance) {
            throw new InsufficientFundsException("Insufficient account balance.");
        }

        withdrawnToday += amount;
        balance -= amount;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void printTransactions() {
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }
}
