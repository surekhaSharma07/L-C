package com.paymentApp.service;


import com.paymentApp.domain.Customer;

public class PaymentCollector {
    private final float paymentAmount;

    public PaymentCollector(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void collectFrom(Customer customer) {
        System.out.println("Attempting to collect payment of ₹" + paymentAmount + " from " + customer.getFirstName());
        if (customer.makePayment(paymentAmount)) {
            System.out.println("Payment collected successfully.");
        } else {
            System.out.println("Insufficient balance. Will try again later.");
        }
    }
}
