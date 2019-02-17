package com.moneytransfer.service;

import com.moneytransfer.exceptions.AccountNotFoundException;
import com.moneytransfer.exceptions.NotEnoughBalanceException;

import java.math.BigDecimal;

/**
 * Service for account data manipulation.
 */
public interface AccountService {
    /**
     * Transfers money from one account to another.
     *
     * @param fromAccountId account's id to withdraw
     * @param toAccountId   account's id to refill
     * @param amount        transfer amount
     * @throws NotEnoughBalanceException if withdraw account doesn't have enough money
     * @throws AccountNotFoundException  if any of accounts not found
     */
    void transferMoney(String fromAccountId, String toAccountId, BigDecimal amount);
}
