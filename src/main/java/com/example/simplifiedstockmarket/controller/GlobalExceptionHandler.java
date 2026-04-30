package com.example.simplifiedstockmarket.controller;

import com.example.simplifiedstockmarket.exeptions.InsufficientStockException;
import com.example.simplifiedstockmarket.exeptions.StockInUseException;
import com.example.simplifiedstockmarket.exeptions.StockNotFoundException;
import com.example.simplifiedstockmarket.exeptions.WalletNotFoundException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<?> handleStockNotFound(StockNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<?> handleInsufficientStock(InsufficientStockException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<?> handleWalletNotFound(WalletNotFoundException e) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(StockInUseException.class)
    public ResponseEntity<?> handleStockInUse(StockInUseException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<?> handleLockingFailure(PessimisticLockingFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Resource temporarily locked, retry the request");
    }
}

