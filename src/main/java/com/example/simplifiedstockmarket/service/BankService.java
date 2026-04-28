package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.controller.dto.LogDto;
import com.example.simplifiedstockmarket.controller.dto.OperationType;
import com.example.simplifiedstockmarket.controller.dto.StockDto;
import com.example.simplifiedstockmarket.exeptions.InsufficientStockException;
import com.example.simplifiedstockmarket.mapper.StockMapper;
import com.example.simplifiedstockmarket.model.Log;
import com.example.simplifiedstockmarket.model.Stock;
import com.example.simplifiedstockmarket.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
public class BankService {
    private final ApplicationContext context;
    private final StockRepository stockRepository;
    private final AuditService auditService;
    private final StockMapper stockMapper;

    public BankService(
            ApplicationContext context,
            StockRepository repository,
            AuditService auditService,
            StockMapper stockMapper
    ){
        this.stockRepository = repository;
        this.context = context;
        this.auditService = auditService;
        this.stockMapper = stockMapper;
    }

    @Transactional
    public List<StockDto> getStatus(){
        return stockRepository.findAll().stream()
                .map(stockMapper::toDto)
                .toList();
    }

    @Transactional
    public void setStatus(List<StockDto> status){
        stockRepository.deleteAll();
        stockRepository.flush();
        stockRepository.saveAll(
                status.stream()
                        .map(stockMapper::toEntity)
                        .collect(Collectors.toList())
        );
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
        if(operativeStock.getQuantity_in_bank() < 0) throw new InsufficientStockException("No stock in bank");
        stockRepository.save(operativeStock);
    }

    public List<LogDto> getLogs(){
        return auditService.getLogs();
    }

    @Async
    public void shutdownInstance(){
        ((ConfigurableApplicationContext) context).close();
    }
}
