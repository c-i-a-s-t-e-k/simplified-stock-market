package com.example.simplifiedstockmarket.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockDto {
    @NotNull
    @NotBlank
    private String name;
    @Min(0)
    private int quantity;

    public StockDto(){}

    public StockDto(String name, int quantity){
        this.name = name;
        this.quantity = quantity;
    }
}
