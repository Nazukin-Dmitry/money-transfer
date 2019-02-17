package com.moneytransfer.service.impl;

import com.google.common.collect.Sets;
import com.google.inject.persist.Transactional;
import com.moneytransfer.dao.AccountDao;
import com.moneytransfer.dao.entity.Account;
import com.moneytransfer.exceptions.AccountNotFoundException;
import com.moneytransfer.exceptions.NotEnoughBalanceException;
import com.moneytransfer.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Singleton
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;

    @Inject
    public AccountServiceImpl(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void transferMoney(String fromAccountId, String toAccountId, BigDecimal amount) {
        if (fromAccountId.equals(toAccountId)) {
            return;
        }

        List<Account> accounts = accountDao.findByIdsWithLock(Sets.newHashSet(fromAccountId, toAccountId));
        Account fromAccount = filterAccountById(accounts, fromAccountId);
        Account toAccount = filterAccountById(accounts, toAccountId);
        if (!isEnoughBalanceForWithdraw(fromAccount, amount)) {
            throw new NotEnoughBalanceException(fromAccountId);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountDao.save(fromAccount);
        accountDao.save(toAccount);
    }

    private Account filterAccountById(List<Account> accounts, String id) {
        return accounts.stream()
                .filter(account -> account.getId().equals(id))
                .findAny().orElseThrow(() -> new AccountNotFoundException(id));
    }

    private boolean isEnoughBalanceForWithdraw(Account account, BigDecimal withdraw) {
        return account.getBalance().compareTo(withdraw) >= 0;
    }
}
