package com.example.simplifiedstockmarket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;

@Entity
public class Log {
    @Id
    private String id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private LogType type;

    @NotNull
    private String wallet_id;

    @NotNull
    private String stock_name;
}
