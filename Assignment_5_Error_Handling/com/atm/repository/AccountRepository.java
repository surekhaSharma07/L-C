package com.atm.repository;

import com.atm.model.Account;

import java.util.HashMap;
import java.util.Map;

public class AccountRepository {
    private final Map<String, Account> accountMap = new HashMap<>();

    public AccountRepository() {
        createAccount("123456", "4321");
        createAccount("789012", "5678");
        createAccount("111222", "0000");
    }

    public boolean createAccount(String accountNumber, String pin) {
        if (accountMap.containsKey(accountNumber)) return false;
        accountMap.put(accountNumber, Account.createTestAccount(accountNumber, pin));
        return true;
    }

    public boolean exists(String accountNumber) {
        return accountMap.containsKey(accountNumber);
    }

    public Account findByAccountNumber(String accountNumber) {
        return accountMap.get(accountNumber);
    }
}
