package com.moneytransfer.modules;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JpaInitializer {

    @Inject
    public JpaInitializer(final PersistService service) {
        service.start();
    }
}
