package com.moneytransfer.exceptions;

import lombok.Getter;

/**
 * Thrown to indicate that a requested account doesn't have enough money for operation.
 */
@Getter
public class NotEnoughBalanceException extends RuntimeException {

    private final String accountId;

    public NotEnoughBalanceException(String accountId) {
        super(null, null, true, false);
        this.accountId = accountId;
    }
}
