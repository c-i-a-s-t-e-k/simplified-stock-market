package com.example.simplifiedstockmarket.mapper;

import com.example.simplifiedstockmarket.controller.dto.StockDto;
import com.example.simplifiedstockmarket.model.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockMapper {
    @Mapping(source = "quantity_in_bank", target = "quantity")
    StockDto toDto(Stock stock);
    @Mapping(source = "quantity", target = "quantity_in_bank")
    Stock toEntity(StockDto dto);
}
