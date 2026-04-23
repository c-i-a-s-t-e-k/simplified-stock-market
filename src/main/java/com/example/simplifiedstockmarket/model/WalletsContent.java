package com.example.simplifiedstockmarket.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

@Entity
public class WalletsContent {

    @EmbeddedId
    private WalletsContentID id;
    @Min(0)
    private int quantity_in_wallet;

    @ManyToOne
    @MapsId("walletId")
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @MapsId("stockId")
    @JoinColumn(name = "stock_id")
    private Stock stock;
}
