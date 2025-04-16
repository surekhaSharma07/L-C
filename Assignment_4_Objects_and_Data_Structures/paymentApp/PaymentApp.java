package com.paymentApp;


import com.paymentApp.domain.Customer;
import com.paymentApp.service.PaymentCollector;

public class PaymentApp {
    public static void main(String[] args) {
        Customer customer = new Customer("Ravi", "Sharma", 10.0f);
        PaymentCollector collector = new PaymentCollector(2.0f);

        collector.collectFrom(customer);

        System.out.println("Remaining Wallet Balance: ₹" + customer.getWalletBalance());
    }
}

