package com.example.simplifiedstockmarket.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class WalletStatusDto {
    private String id;
    private List<StockDto> stocks;
}
