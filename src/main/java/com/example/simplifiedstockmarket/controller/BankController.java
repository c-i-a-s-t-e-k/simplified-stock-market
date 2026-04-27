package com.example.simplifiedstockmarket.controller;

import com.example.simplifiedstockmarket.controller.dto.LogListDto;
import com.example.simplifiedstockmarket.controller.dto.StockStatusRequest;
import com.example.simplifiedstockmarket.service.BankService;
import jakarta.validation.Valid;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
public class BankController {
    BankService bankService;

    public BankController(BankService service){
        this.bankService = service;
    }

    @GetMapping("/stocks")
    public ResponseEntity<?> getBankStatus(){
        return ResponseEntity.ok(new StockStatusRequest(bankService.getStatus()));
    }

    @PostMapping("/stocks")
    public ResponseEntity<?> setBankStatus(@Valid @RequestBody StockStatusRequest request){
        bankService.setStatus(request.getStocks());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/log")
    public ResponseEntity<?> getLog(){
        return ResponseEntity.ok(new LogListDto(bankService.getLogs()));
    }

    @PostMapping("/chaos")
    public ResponseEntity<?> shutdownInstance(){
        bankService.shutdownInstance();
        return ResponseEntity.ok().build();
    }
}
