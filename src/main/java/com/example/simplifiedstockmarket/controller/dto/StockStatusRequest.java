package com.example.simplifiedstockmarket.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class StockStatusRequest {
    private List<StockDto> stocks;
}
