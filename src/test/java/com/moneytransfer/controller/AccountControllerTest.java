package com.moneytransfer.controller;

import com.google.inject.AbstractModule;
import com.google.inject.servlet.GuiceFilter;
import com.moneytransfer.GuiceContextListener;
import com.moneytransfer.controller.dto.TransactionDto;
import com.moneytransfer.exceptions.AccountNotFoundException;
import com.moneytransfer.exceptions.NotEnoughBalanceException;
import com.moneytransfer.service.AccountService;
import io.restassured.http.ContentType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.DispatcherType;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.*;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class AccountControllerTest {
    private static final String NOT_FOUND_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String NOT_ENOUGH_MONEY_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String FIRST_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String SECOND_ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String BASE_URI = "http://localhost:8000/";

    private static Server server;

    @BeforeClass
    public static void setUp() throws Exception {
        AccountService mockAccountService = mock(AccountService.class);
        doThrow(new AccountNotFoundException(NOT_FOUND_ACCOUNT_ID))
                .when(mockAccountService).transferMoney(eq(NOT_FOUND_ACCOUNT_ID), any(), any());
        doThrow(new NotEnoughBalanceException(NOT_ENOUGH_MONEY_ACCOUNT_ID))
                .when(mockAccountService).transferMoney(eq(NOT_ENOUGH_MONEY_ACCOUNT_ID), any(), any());
        doNothing()
                .when(mockAccountService).transferMoney(eq(FIRST_ACCOUNT_ID), eq(SECOND_ACCOUNT_ID), any());

        server = new Server(8000);
        ServletContextHandler handler = new ServletContextHandler(server, "/");
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        GuiceContextListener contextListener = new GuiceContextListener(
                Collections.singletonList(new TestModule(mockAccountService)));
        handler.addEventListener(contextListener);
        handler.addServlet(HttpServletDispatcher.class, "/*");
        server.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void transferMoney_whenTransferIsSuccessful_thenOk() {
        TransactionDto dto = new TransactionDto(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, new BigDecimal(1));
        given().
                baseUri(BASE_URI).
                accept(ContentType.JSON).
                body(dto).
                header("Content-Type", "application/json").
                when().
                post("/rest/v1/accounts/transferMoney").
                then().
                statusCode(OK.getStatusCode());
    }

    @Test
    public void transferMoney_whenAccountNotFound_thenConflict() {
        TransactionDto dto = new TransactionDto(NOT_FOUND_ACCOUNT_ID, FIRST_ACCOUNT_ID, new BigDecimal(1));
        given().
                baseUri(BASE_URI).
                accept(ContentType.JSON).
                body(dto).
                header("Content-Type", "application/json").
                when().
                post("/rest/v1/accounts/transferMoney").
                then().
                statusCode(CONFLICT.getStatusCode()).
                body(equalTo(String.format(AccountController.ACCOUNT_S_NOT_FOUND, NOT_FOUND_ACCOUNT_ID)));
    }

    @Test
    public void transferMoney_whenMoneyNotEnough_thenConflict() {
        TransactionDto dto = new TransactionDto(NOT_ENOUGH_MONEY_ACCOUNT_ID, FIRST_ACCOUNT_ID, new BigDecimal(1));
        given().
                baseUri(BASE_URI).
                accept(ContentType.JSON).
                body(dto).
                header("Content-Type", "application/json").
                when().
                post("/rest/v1/accounts/transferMoney").
                then().
                statusCode(CONFLICT.getStatusCode()).
                body(equalTo(String.format(AccountController.NOT_ENOUGH_MONEY, NOT_ENOUGH_MONEY_ACCOUNT_ID)));
    }

    @Test
    public void transferMoney_whenTransferValueNegative_thenBadRequest() {
        TransactionDto dto = new TransactionDto(NOT_ENOUGH_MONEY_ACCOUNT_ID, FIRST_ACCOUNT_ID, new BigDecimal(-1));
        given().
                baseUri(BASE_URI).
                accept(ContentType.JSON).
                body(dto).
                header("Content-Type", "application/json").
                when().
                post("/rest/v1/accounts/transferMoney").
                then().
                statusCode(BAD_REQUEST.getStatusCode());
    }

    private static class TestModule extends AbstractModule {

        private final AccountService accountService;

        private TestModule(AccountService accountService) {
            this.accountService = accountService;
        }

        @Override
        protected void configure() {
            bind(AccountService.class).toInstance(accountService);
            bind(AccountController.class);
        }
    }
}