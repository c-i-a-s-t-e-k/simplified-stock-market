package com.example.simplifiedstockmarket.controller;

import com.example.simplifiedstockmarket.exeptions.InsufficientStockException;
import com.example.simplifiedstockmarket.exeptions.StockNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
