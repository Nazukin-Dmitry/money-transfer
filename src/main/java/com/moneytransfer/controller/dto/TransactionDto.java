package com.moneytransfer.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    @NotNull
    private String fromAccount;
    @NotNull
    private String toAccount;
    @Min(0)
    private BigDecimal amount;
}
