package com.moneytransfer.dao.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "ACCOUNT")
public class Account {
    /**
     * Account's unique id.
     */
    @Id
    @Column(name = "ID", unique = true, nullable = false)
    private String id;

    /**
     * Account's balance.
     */
    @Min(0)
    @Column(name = "BALANCE", nullable = false)
    private BigDecimal balance;
}
