package com.example.simplifiedstockmarket.controller;

import com.example.simplifiedstockmarket.controller.dto.StockStatusRequest;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BankController {

    @GetMapping("/stocks")
    public ResponseEntity<?> getBankStatus(){
        return null;
    }

    @PostMapping("/stocks")
    public ResponseEntity<?> setBankStatus(@RequestBody StockStatusRequest request){
        return null;
    }

    @GetMapping("/log")
    public ResponseEntity<?> getLog(){
        return null;
    }

    @PostMapping("/chaos")
    public ResponseEntity<?> shutdownInstance(){
        return null;
    }
}
