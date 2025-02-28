package com.bank;

import java.util.Scanner;

public class BankingSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Account savingsAccount = new SavingsAccount(2000);
        Account currentAccount = new CurrentAccount(10000);

        boolean exit = false;
        while (!exit) {
            System.out.println("Welcome");
            System.out.println("1-Deposit into Savings Account");
            System.out.println("2-Deposit into Current Account");
            System.out.println("3-Withdraw from Savings Account");
            System.out.println("4-Withdraw from Current Account");
            System.out.println("5-View Account Balances");
            System.out.println("6-Exit");
            System.out.print("Please choose an option");

            int choice = scanner.nextInt();
            double amount;

            switch (choice) {
                case 1:
                    System.out.print("Enter deposit amount for Savings Account");
                    amount = scanner.nextDouble();
                    savingsAccount.deposit(amount);
                    break;
                case 2:
                    System.out.print("Enter deposit amount for Current Account");
                    amount = scanner.nextDouble();
                    currentAccount.deposit(amount);
                    break;
                case 3:
                    System.out.print("Enter withdrawal amount for Savings Account");
                    amount = scanner.nextDouble();
                    savingsAccount.withdraw(amount);
                    break;
                case 4:
                    System.out.print("Enter withdrawal amount for Current Account");
                    amount = scanner.nextDouble();
                    currentAccount.withdraw(amount);
                    break;
                case 5:
                    System.out.println("Savings Account Balance" + savingsAccount.getBalance());
                    System.out.println("Current Account Balance:" + currentAccount.getBalance());
                    break;
                case 6:
                    exit = true;
                    System.out.println("Thank you for using Bank.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
    }
}


