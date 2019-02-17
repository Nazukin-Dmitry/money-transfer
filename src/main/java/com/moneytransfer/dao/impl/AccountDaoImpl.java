package com.moneytransfer.dao.impl;

import com.google.inject.persist.Transactional;
import com.moneytransfer.dao.AccountDao;
import com.moneytransfer.dao.entity.Account;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Set;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

@Singleton
public class AccountDaoImpl implements AccountDao {
    private static final String SELECT_BY_IDS = "SELECT a FROM Account a WHERE a.id in :ids";

    @Getter
    private final Provider<EntityManager> em;

    @Inject
    public AccountDaoImpl(Provider<EntityManager> em) {
        this.em = em;
    }

    @Override
    @Transactional
    public List<Account> findByIdsWithLock(Set<String> ids) {
        TypedQuery<Account> query = em.get().createQuery(SELECT_BY_IDS, Account.class);
        query.setParameter("ids", ids);
        query.setLockMode(PESSIMISTIC_WRITE);
        return query.getResultList();
    }

    @Override
    @Transactional
    public Account save(Account account) {
        return em.get().merge(account);
    }

    @Override
    @Transactional
    public Account findById(String id) {
        return em.get().find(Account.class, id);
    }
}
