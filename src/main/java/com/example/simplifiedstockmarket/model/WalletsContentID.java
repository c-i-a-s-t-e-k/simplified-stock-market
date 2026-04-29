package com.example.simplifiedstockmarket.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WalletsContentID implements Serializable {
    private String walletId;
    private String stockId;

    public String getStockId() {
        return stockId;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public WalletsContentID(Wallet wallet, Stock stock){
        this.stockId = stock.getId();
        this.walletId = wallet.getId();
    }
    public WalletsContentID(String walletId, String stockId){
        this.stockId = stockId;
        this.walletId = walletId;
    }
    public WalletsContentID(){}

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof WalletsContentID){
            WalletsContentID other = (WalletsContentID) obj;
            return this.walletId.equals(other.walletId) && this.stockId.equals(other.stockId);
        }else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId, stockId);
    }
}
