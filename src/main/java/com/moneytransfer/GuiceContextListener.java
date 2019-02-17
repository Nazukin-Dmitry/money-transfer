package com.moneytransfer;

import com.google.inject.Module;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;

import javax.servlet.ServletContext;
import java.util.List;

public class GuiceContextListener extends GuiceResteasyBootstrapServletContextListener {

    private final List<Module> modules;

    public GuiceContextListener(List<Module> modules) {
        this.modules = modules;
    }

    @Override
    protected List<? extends Module> getModules(final ServletContext context) {
        return modules;
    }
}

