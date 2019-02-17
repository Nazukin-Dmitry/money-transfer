package com.moneytransfer.exceptions;

import lombok.Getter;

/**
 * Thrown to indicate that a requested account not found.
 */
@Getter
public final class AccountNotFoundException extends RuntimeException {

    private final String accountId;

    public AccountNotFoundException(String accountId) {
        super(null, null, true, false);
        this.accountId = accountId;
    }
}
