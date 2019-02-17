package com.moneytransfer.dao;

import com.moneytransfer.dao.entity.Account;

import java.util.List;
import java.util.Set;

public interface AccountDao {
    List<Account> findByIdsWithLock(Set<String> ids);

    Account save(Account account);

    Account findById(String id);
}
