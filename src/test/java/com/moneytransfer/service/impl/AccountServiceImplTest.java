package com.moneytransfer.service.impl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.moneytransfer.dao.AccountDao;
import com.moneytransfer.dao.entity.Account;
import com.moneytransfer.exceptions.AccountNotFoundException;
import com.moneytransfer.exceptions.NotEnoughBalanceException;
import com.moneytransfer.modules.AppModule;
import com.moneytransfer.modules.PersistenceModule;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AccountServiceImplTest {

    private static Injector injector = Guice.createInjector(
            new PersistenceModule("inMemory"), new AppModule());

    @Inject
    private AccountServiceImpl accountService = injector.getInstance(AccountServiceImpl.class);
    @Inject
    private AccountDao accountDao = injector.getInstance(AccountDao.class);

    @Test
    public void testTransferMoney_whenOneAccountTransfersToOther() {
        String firstAccountId = saveNewAccount(new BigDecimal(10));
        String secondAccountId = saveNewAccount(new BigDecimal(1000));

        accountService.transferMoney(firstAccountId, secondAccountId, new BigDecimal(10));

        Account firstAccount = accountDao.findById(firstAccountId);
        Account secondAccount = accountDao.findById(secondAccountId);

        Assert.assertEquals(firstAccount.getBalance().compareTo(new BigDecimal(0)), 0);
        Assert.assertEquals(secondAccount.getBalance().compareTo(new BigDecimal(1010)), 0);
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferMoney_whenSourceAccountNotExists_thenAccountNotFoundException() {
        String secondAccountId = saveNewAccount(new BigDecimal(1000));

        accountService.transferMoney(UUID.randomUUID().toString(), secondAccountId, new BigDecimal(10));
    }

    @Test(expected = AccountNotFoundException.class)
    public void testTransferMoney_whenTargetAccountNotExists_thenAccountNotFoundException() {
        String firstAccountId = saveNewAccount(new BigDecimal(1000));

        accountService.transferMoney(firstAccountId, UUID.randomUUID().toString(), new BigDecimal(10));
    }

    @Test
    public void testTransferMoney_whenBalanceNotEnough_thenNotEnoughBalanceExceptionAndBalanceNotChanged() {
        String firstAccountId = saveNewAccount(new BigDecimal(9));
        String secondAccountId = saveNewAccount(new BigDecimal(0));
        try {
            accountService.transferMoney(firstAccountId, secondAccountId, new BigDecimal(10));
        } catch (Exception e) {
            Assert.assertEquals(e instanceof NotEnoughBalanceException, true);
        }

        Account firstAccount = accountDao.findById(firstAccountId);
        Account secondAccount = accountDao.findById(secondAccountId);

        Assert.assertEquals(firstAccount.getBalance().compareTo(new BigDecimal(9)), 0);
        Assert.assertEquals(secondAccount.getBalance().compareTo(new BigDecimal(0)), 0);
    }

    @Test
    public void testTransferMoney_whenTwoAccountsTransferToEachOther() throws InterruptedException {
        String firstAccountId = saveNewAccount(new BigDecimal(1000));
        String secondAccountId = saveNewAccount(new BigDecimal(1000));

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.addAll(IntStream.range(0, 100)
                .boxed()
                .map(v -> createTransferTask(firstAccountId, secondAccountId, new BigDecimal(10)))
                .collect(Collectors.toList()));
        tasks.addAll(IntStream.range(0, 100)
                .boxed()
                .map(v -> createTransferTask(secondAccountId, firstAccountId, new BigDecimal(10)))
                .collect(Collectors.toList()));
        executorService.invokeAll(tasks);

        Account firstAccount = accountDao.findById(firstAccountId);
        Account secondAccount = accountDao.findById(secondAccountId);

        Assert.assertEquals(firstAccount.getBalance().compareTo(new BigDecimal(1000)), 0);
        Assert.assertEquals(secondAccount.getBalance().compareTo(new BigDecimal(1000)), 0);
    }

    @Test
    public void testTransferMoney_whenTheeAccountsTransferToEachOther() throws InterruptedException {
        String firstAccountId = saveNewAccount(new BigDecimal(1000));
        String secondAccountId = saveNewAccount(new BigDecimal(1000));
        String thirdAccountId = saveNewAccount(new BigDecimal(1000));

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.addAll(IntStream.range(0, 100)
                .boxed()
                .map(v -> createTransferTask(firstAccountId, secondAccountId, new BigDecimal(10)))
                .collect(Collectors.toList()));
        tasks.addAll(IntStream.range(0, 100)
                .boxed()
                .map(v -> createTransferTask(secondAccountId, thirdAccountId, new BigDecimal(10)))
                .collect(Collectors.toList()));
        tasks.addAll(IntStream.range(0, 100)
                .boxed()
                .map(v -> createTransferTask(thirdAccountId, firstAccountId, new BigDecimal(10)))
                .collect(Collectors.toList()));
        executorService.invokeAll(tasks);

        Account firstAccount = accountDao.findById(firstAccountId);
        Account secondAccount = accountDao.findById(secondAccountId);
        Account thirdAccount = accountDao.findById(thirdAccountId);

        Assert.assertEquals(firstAccount.getBalance().compareTo(new BigDecimal(1000)), 0);
        Assert.assertEquals(secondAccount.getBalance().compareTo(new BigDecimal(1000)), 0);
        Assert.assertEquals(thirdAccount.getBalance().compareTo(new BigDecimal(1000)), 0);
    }

    private String saveNewAccount(BigDecimal amount) {
        Account account = new Account();
        account.setBalance(amount);
        String id = UUID.randomUUID().toString();
        account.setId(id);
        accountDao.save(account);
        return id;
    }

    private Callable<Void> createTransferTask(String fromAccountId, String toAccountId, BigDecimal amount) {
        return () -> {
            accountService.transferMoney(fromAccountId, toAccountId, amount);
            return null;
        };
    }
}