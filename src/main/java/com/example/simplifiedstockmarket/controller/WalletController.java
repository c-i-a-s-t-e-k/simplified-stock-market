package com.example.simplifiedstockmarket.controller;


import com.example.simplifiedstockmarket.controller.dto.WalletOperationRequest;
import com.example.simplifiedstockmarket.service.WalletService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Null;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService service){
        this.walletService = service;
    }

    @PostMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<?> doOperation(
            @PathVariable("wallet_id") String walletId,
            @PathVariable("stock_name") String stockName,
            @RequestBody WalletOperationRequest request)
    {
        walletService.executeOperationWithLog(request.getType(), walletId, stockName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{wallet_id}")
    public ResponseEntity<?> getWalletStatus(@PathVariable("wallet_id") String walletId){
        return ResponseEntity.ok(walletService.getWalletStatus(walletId));
    }

    @GetMapping("/{wallet_id}/stocks/{stock_name}")
    public ResponseEntity<?> getWalletBalance(@PathVariable("wallet_id") String walletId, @PathVariable("stock_name") String stockName){
        return  ResponseEntity.ok(walletService.getStockQuantityInWallet(walletId, stockName));
    }
}
