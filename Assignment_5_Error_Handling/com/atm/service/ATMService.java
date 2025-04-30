package com.atm.service;

import com.atm.exception.*;
import com.atm.model.Account;
import com.atm.model.Transaction;

import java.time.LocalDateTime;
import java.util.Random;

public class ATMService {
    private double atmCash;

    public ATMService(double initialCash) {
        this.atmCash = initialCash;
    }

    public void withdraw(Account account, String pin, double amount) throws ATMException {
        simulateServer();

        if (amount > atmCash) {
            throw new InsufficientFundsException("ATM has insufficient cash.");
        }

        if (!account.getCard().validatePin(pin)) {
            return;
        }

        if (account.getCard().isExpired()) {
            throw new ATMException("Card expired.");
        }

        account.withdraw(amount);
        atmCash -= amount;

        account.addTransaction(new Transaction("Withdraw", amount, LocalDateTime.now()));
        System.out.println("✅ Withdrawal of ₹" + amount + " successful.");
    }

    private void simulateServer() throws ServerUnavailableException {
        if (new Random().nextInt(10) == 0) {
            throw new ServerUnavailableException("Server is currently unreachable.");
        }
    }
}
