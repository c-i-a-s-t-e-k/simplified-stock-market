package com.example.simplifiedstockmarket.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LogType {
    BUY, SELL;

    @JsonValue
    public String toLowerCase() {
        return name().toLowerCase();
    }
}
