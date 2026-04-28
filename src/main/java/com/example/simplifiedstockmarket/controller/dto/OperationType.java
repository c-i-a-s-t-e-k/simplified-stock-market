package com.example.simplifiedstockmarket.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    BUY, SELL;
    @JsonCreator
    public static OperationType fromString(String value) {
        if(!value.equals("sell") && !value.equals("buy")) throw new IllegalArgumentException();
        return OperationType.valueOf(value.toUpperCase());
    }
}
