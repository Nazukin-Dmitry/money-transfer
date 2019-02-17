package com.moneytransfer;

import com.google.inject.servlet.GuiceFilter;
import com.moneytransfer.modules.AppModule;
import com.moneytransfer.modules.PersistenceModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import javax.servlet.DispatcherType;
import java.util.Arrays;
import java.util.EnumSet;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8000);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        GuiceContextListener contextListener = new GuiceContextListener(
                Arrays.asList(new AppModule(), new PersistenceModule("inMemory")));
        handler.addEventListener(contextListener);
        handler.addServlet(HttpServletDispatcher.class, "/*");
        server.start();
    }
}
