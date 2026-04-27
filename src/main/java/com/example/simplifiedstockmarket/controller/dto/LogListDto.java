package com.example.simplifiedstockmarket.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class LogListDto {
    private List<LogDto> log;

    public LogListDto(List<LogDto> log){
        this.log = log;
    }
}
