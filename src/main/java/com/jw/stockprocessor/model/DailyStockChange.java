package com.jw.stockprocessor.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
public class DailyStockChange {

    @Id
    @GeneratedValue
    @NotNull
    @Column(name = "ID", unique = true)
    private Integer id;

    private Date date;

    private String name;

    private String isin;

    private String currency;

    private BigDecimal openingRate;

    private BigDecimal maxRate;

    private BigDecimal minRate;

    private BigDecimal closeRate;

    private BigDecimal delta;

    private BigDecimal volume;

    private BigDecimal transactions;

    private BigDecimal turnover;

    public DailyStockChange(Date date, String name, String isin, String currency, BigDecimal openingRate,
                            BigDecimal maxRate, BigDecimal minRate, BigDecimal closeRate, BigDecimal delta, BigDecimal volume,
                            BigDecimal transactions, BigDecimal turnover) {
        this.date = date;
        this.name = name;
        this.isin = isin;
        this.currency = currency;
        this.openingRate = openingRate;
        this.maxRate = maxRate;
        this.minRate = minRate;
        this.closeRate = closeRate;
        this.delta = delta;
        this.volume = volume;
        this.transactions = transactions;
        this.turnover = turnover;
    }

    public DailyStockChange() {
    }
}
