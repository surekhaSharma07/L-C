package com.paymentApp.domain;


public class Customer {
    private String firstName;
    private String lastName;
    private DigitalWallet wallet;

    public Customer(String firstName, String lastName, float initialBalance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.wallet = new DigitalWallet(initialBalance);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean makePayment(float amount) {
        if (wallet.hasSufficientBalance(amount)) {
            wallet.deduct(amount);
            return true;
        }
        return false;
    }

    public void receiveFunds(float amount) {
        wallet.add(amount);
    }

    public float getWalletBalance() {
        return wallet.getBalance();
    }
}
