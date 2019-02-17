package com.moneytransfer.modules;

import com.google.inject.AbstractModule;
import com.moneytransfer.controller.AccountController;
import com.moneytransfer.service.AccountService;
import com.moneytransfer.service.impl.AccountServiceImpl;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AccountService.class).to(AccountServiceImpl.class);
        bind(AccountController.class);
    }
}
