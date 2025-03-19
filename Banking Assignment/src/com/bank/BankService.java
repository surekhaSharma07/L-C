package com.bank;

import java.util.HashMap;
import java.util.Map;

public class BankService {
    private final Map<String, Account> accounts = new HashMap<>();

    public Account createAccount(String type, double balance) {
        Account account = switch (type.toLowerCase()) {
            case "savings" -> new SavingsAccount(balance);
            case "current" -> new CurrentAccount(balance);
            default -> throw new IllegalArgumentException("Invalid account type.");
        };
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public Account getAccount(String accNumber) {
        return accounts.get(accNumber);
    }

    public void displayAllAccounts() {
        accounts.values().forEach(Account::displayDetails);
    }
}
