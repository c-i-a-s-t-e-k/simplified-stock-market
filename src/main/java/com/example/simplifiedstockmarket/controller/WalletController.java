package com.example.simplifiedstockmarket.controller;


import com.example.simplifiedstockmarket.controller.dto.WalletOperationRequest;
import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    @PostMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<?> doOperation(
            @PathVariable("wallet_id") String walletId,
            @PathVariable("stock_name") String stockName,
            @RequestBody WalletOperationRequest request)
    {
        return null;
    }

    @GetMapping("/{wallet_id}")
    public ResponseEntity<?> getWalletStatus(@PathVariable("wallet_id") String walletId){
        return null;
    }

    @GetMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<?> getWalletBalance(@PathVariable("wallet_id") String walletId, @PathVariable("stock_name") String stockName){
        return  null;
    }
}
