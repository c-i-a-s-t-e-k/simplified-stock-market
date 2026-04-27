package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.controller.dto.OperationType;
import com.example.simplifiedstockmarket.model.Log;
import com.example.simplifiedstockmarket.model.Stock;
import com.example.simplifiedstockmarket.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankService {
    private final ApplicationContext context;
    private final StockRepository stockRepository;
    private final AuditService auditService;

    public BankService(
            ApplicationContext context,
            StockRepository repository,
            AuditService auditService
    ){
        this.stockRepository = repository;
        this.context = context;
        this.auditService = auditService;
    }

    @Transactional
    public List<Stock> getStatus(){
        return stockRepository.findAll();
    }

    @Transactional
    public void setStatus(List<Stock> status){
        stockRepository.deleteAll();
        stockRepository.saveAll(status);
    }

    private int valueChangeByType(OperationType type){
        return switch (type){
            case SELL -> 1;
            case BUY -> -1;
            case null -> throw new IllegalArgumentException("Operation type cannot be null");
        };
    }

    @Transactional
    public void WalletOperation(String stockId, OperationType type){
        Stock operativeStock = stockRepository.findById(stockId)
                        .orElseThrow(() -> new EntityNotFoundException("Stock not found: " + stockId));
        operativeStock.setQuantity_in_bank(
                operativeStock.getQuantity_in_bank() + this.valueChangeByType(type)
        );
        stockRepository.save(operativeStock);
    }

    public List<Log> getLogs(){
        return auditService.getLogs();
    }

    public void shutdownInstance(){
        ((ConfigurableApplicationContext) context).close();
    }
}
