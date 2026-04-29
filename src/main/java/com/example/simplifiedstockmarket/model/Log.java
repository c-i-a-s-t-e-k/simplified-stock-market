package com.example.simplifiedstockmarket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
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

    public Log(UUID id, String walletId, String stockName, LogType type){
        this.id = id.toString();
        this.wallet_id = walletId;
        this.stock_name = stockName;
        this.type = type;
    }
    public Log(){}
}
