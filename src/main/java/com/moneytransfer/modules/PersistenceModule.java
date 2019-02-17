package com.moneytransfer.modules;

import com.google.inject.AbstractModule;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.moneytransfer.dao.AccountDao;
import com.moneytransfer.dao.impl.AccountDaoImpl;

public class PersistenceModule extends AbstractModule {

    private final String persistenceUnit;

    public PersistenceModule(String persistenceUnit) {
        this.persistenceUnit = persistenceUnit;
    }

    @Override
    protected void configure() {
        install(new JpaPersistModule(persistenceUnit));
        bind(JpaInitializer.class).asEagerSingleton();
        bind(AccountDao.class).to(AccountDaoImpl.class);
    }
}
