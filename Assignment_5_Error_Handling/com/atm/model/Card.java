package com.atm.model;

import com.atm.exception.CardBlockedException;

import java.time.LocalDate;

public class Card {
    private final String pin;
    private final LocalDate expiryDate;
    private int invalidAttempts;
    private boolean blocked;

    public Card(String pin, LocalDate expiryDate) {
        this.pin = pin;
        this.expiryDate = expiryDate;
        this.invalidAttempts = 0;
        this.blocked = false;
    }

    public boolean validatePin(String inputPin) throws CardBlockedException {
        if (blocked) {
            throw new CardBlockedException("Card is blocked.");
        }

        if (pin.equals(inputPin)) {
            invalidAttempts = 0;
            return true;
        } else {
            invalidAttempts++;
            if (invalidAttempts >= 3) {
                blocked = true;
                throw new CardBlockedException("Card blocked after 3 invalid attempts.");
            }
            throw new CardBlockedException("Invalid PIN. Attempts left: " + (3 - invalidAttempts));
        }
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }
}
