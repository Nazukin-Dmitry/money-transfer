package com.moneytransfer.controller;

import com.moneytransfer.controller.dto.TransactionDto;
import com.moneytransfer.exceptions.AccountNotFoundException;
import com.moneytransfer.exceptions.NotEnoughBalanceException;
import com.moneytransfer.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Singleton
@Path("/rest/v1/accounts")
@Produces("application/json")
@Consumes("application/json")
public class AccountController {

    static final String ACCOUNT_S_NOT_FOUND = "Account %s not found.";
    static final String NOT_ENOUGH_MONEY = "Account %s doesn't have enough money for withdraw.";

    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Path("/transferMoney")
    public Response addTransaction(@Valid TransactionDto transactionDto) {
        try {
            accountService.transferMoney(transactionDto.getFromAccount(),
                    transactionDto.getToAccount(),
                    transactionDto.getAmount());
            return Response
                    .status(Response.Status.OK)
                    .build();
        } catch (AccountNotFoundException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(String.format(ACCOUNT_S_NOT_FOUND, e.getAccountId()))
                    .build();
        } catch (NotEnoughBalanceException e) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(String.format(NOT_ENOUGH_MONEY, e.getAccountId()))
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .build();
        }
    }
}
