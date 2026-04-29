package com.example.simplifiedstockmarket.controller.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class StockStatusRequest {
    @NotNull
    @NotEmpty
    private List<StockDto> stocks;

    public StockStatusRequest(List<StockDto> list){
        this.stocks = list;
    }
    public StockStatusRequest(){}
}
