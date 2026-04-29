package com.example.simplifiedstockmarket.exeptions;

public class StockInUseException extends RuntimeException {
    public StockInUseException(String message) {
        super(message);
    }
}
