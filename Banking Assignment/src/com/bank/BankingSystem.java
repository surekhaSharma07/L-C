package com.bank;

import java.util.Scanner;

public class BankingSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BankService bankService = new BankService();

        System.out.println("Creating demo accounts...");
        Account savingsAccount = bankService.createAccount("savings", 2000);
        Account currentAccount = bankService.createAccount("current", 10000);
        System.out.println("Created accounts: " + savingsAccount.getAccountNumber() + ", " + currentAccount.getAccountNumber());

        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Banking System Menu ---");
            System.out.println("1 - Deposit");
            System.out.println("2 - Withdraw");
            System.out.println("3 - Transfer");
            System.out.println("4 - View Account Details");
            System.out.println("5 - View All Accounts");
            System.out.println("6 - Exit");
            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();

            if (option == 6) {
                isRunning = false;
                break;
            }

            System.out.print("Enter your account number: ");
            String accountNumber = scanner.next();
            Account account = bankService.getAccount(accountNumber);

            if (account == null) {
                System.out.println("Account not found.");
                continue;
            }

            switch (option) {
                case 1 -> {
                    System.out.print("Enter amount to deposit: ");
                    double depositAmount = scanner.nextDouble();
                    account.deposit(depositAmount);
                }
                case 2 -> {
                    System.out.print("Enter amount to withdraw: ");
                    double withdrawalAmount = scanner.nextDouble();
                    account.withdraw(withdrawalAmount);
                }
                case 3 -> {
                    System.out.print("Enter target account number: ");
                    String targetAccountNumber = scanner.next();
                    Account targetAccount = bankService.getAccount(targetAccountNumber);

                    if (targetAccount == null) {
                        System.out.println("Target account not found.");
                        continue;
                    }

                    System.out.print("Enter transfer amount: ");
                    double transferAmount = scanner.nextDouble();
                    account.transfer(targetAccount, transferAmount);
                }
                case 4 -> account.displayDetails();
                case 5 -> bankService.displayAllAccounts();
                default -> System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
        System.out.println("Thank you for using the Banking System.");
    }
}
