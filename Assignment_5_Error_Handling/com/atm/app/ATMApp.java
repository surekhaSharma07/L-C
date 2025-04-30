package com.atm.app;

import com.atm.exception.ATMException;
import com.atm.model.Account;
import com.atm.repository.AccountRepository;
import com.atm.service.ATMService;

import java.util.Scanner;

public class ATMApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        AccountRepository accountRepository = new AccountRepository();
        ATMService atmService = new ATMService(20000.0);

        while (true) {
            System.out.println("\n==============================");
            System.out.println("🏦 Welcome to ATM");
            System.out.println("==============================");
            System.out.println("1. 🔐 Login to Existing Account");
            System.out.println("2. 🆕 Create New Account");
            System.out.println("3. 🚪 Exit");
            System.out.print("Choose an option: ");
            String mainChoice = scanner.nextLine().trim();

            if (mainChoice.equals("3")) {
                System.out.println("👋 Thank you for using ATM. Goodbye!");
                break;
            }

            if (mainChoice.equals("2")) {
                System.out.print("📄 Enter New Account Number: ");
                String newAccNum = scanner.nextLine().trim();
                if (accountRepository.exists(newAccNum)) {
                    System.out.println("⚠️ Account already exists.");
                    continue;
                }

                System.out.print("🔐 Set 4-digit PIN: ");
                String newPin = scanner.nextLine().trim();
                if (newPin.length() != 4 || !newPin.matches("\\d+")) {
                    System.out.println("❌ PIN must be exactly 4 digits.");
                    continue;
                }

                accountRepository.createAccount(newAccNum, newPin);
                System.out.println("✅ Account created successfully.");
                continue;
            }

            System.out.print("👉 Enter Account Number (or type 'exit' to quit): ");
            String accountNumber = scanner.nextLine().trim();

            if (accountNumber.equalsIgnoreCase("exit")) {
                System.out.println("👋 Thank you for using ATM. Goodbye!");
                break;
            }

            if (!accountRepository.exists(accountNumber)) {
                System.out.println("❌ Account not found.");
                continue;
            }

            Account account = accountRepository.findByAccountNumber(accountNumber);

            try {
                System.out.print("🔐 Enter PIN: ");
                String pin = scanner.nextLine().trim();

                boolean sessionActive = true;
                while (sessionActive) {
                    System.out.println("\n📋 Menu:");
                    System.out.println("1. 💸 Withdraw Cash");
                    System.out.println("2. 🧾 View Mini Statement");
                    System.out.println("3. 🔄 Switch Account");
                    System.out.println("4. 💼 View Balance");
                    System.out.println("5. 🚪 Exit");

                    System.out.print("Choose an option: ");
                    String choice = scanner.nextLine();

                    switch (choice) {
                        case "1":
                            System.out.print("💰 Enter amount to withdraw: ");
                            double amount = scanner.nextDouble();
                            scanner.nextLine();
                            atmService.withdraw(account, pin, amount);
                            break;

                        case "2":
                            System.out.println("\n🧾 Mini Statement:");
                            account.printTransactions();
                            break;

                        case "3":
                            sessionActive = false;
                            break;

                        case "4":
                            System.out.printf("💼 Current Balance: ₹%.2f%n", account.getBalance());
                            break;

                        case "5":
                            System.out.println("👋 Thank you. Session ended.");
                            return;

                        default:
                            System.out.println("❌ Invalid choice.");
                    }

                }

            } catch (ATMException e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        }

        scanner.close();
    }
}
