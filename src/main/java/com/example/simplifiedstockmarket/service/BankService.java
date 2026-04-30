package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.controller.dto.LogDto;
import com.example.simplifiedstockmarket.controller.dto.OperationType;
import com.example.simplifiedstockmarket.controller.dto.StockDto;
import com.example.simplifiedstockmarket.exeptions.InsufficientStockException;
import com.example.simplifiedstockmarket.exeptions.StockInUseException;
import com.example.simplifiedstockmarket.mapper.StockMapper;
import com.example.simplifiedstockmarket.model.Log;
import com.example.simplifiedstockmarket.model.Stock;
import com.example.simplifiedstockmarket.repository.StockRepository;
import com.example.simplifiedstockmarket.repository.WalletContentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
public class BankService {
    private final ApplicationContext context;
    private final StockRepository stockRepository;
    private final AuditService auditService;
    private final StockMapper stockMapper;
    private final WalletContentRepository walletContentRepository;

    public BankService(
            ApplicationContext context,
            StockRepository repository,
            AuditService auditService,
            StockMapper stockMapper,
            WalletContentRepository walletContentRepository
    ){
        this.stockRepository = repository;
        this.walletContentRepository = walletContentRepository;
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
        stockRepository.lockTable();
        walletContentRepository.lockTable();
        walletContentRepository.clearWalletsContent();
        Set<Stock> newStatus = status
                        .stream()
                        .map(stockMapper::toEntity)
                        .collect(Collectors.toSet());
        Set<Stock> stocksInUse = walletContentRepository.findAllUsedStocks();
        if (!newStatus.containsAll(stocksInUse))
            throw new StockInUseException("Invalid bank status, some wallets using non-existing stock");

        stockRepository.deleteAllNotIn(stocksInUse);
        stockRepository.flush();
        newStatus.forEach(stockRepository::upsert);
    }

    private int valueChangeByType(OperationType type){
        return switch (type){
            case SELL -> 1;
            case BUY -> -1;
            case null -> throw new IllegalArgumentException("Operation type cannot be null");
        };
    }

    @Transactional
    public void WalletOperation(Stock stock, OperationType type){
        stock.setQuantity_in_bank(
                stock.getQuantity_in_bank() + this.valueChangeByType(type)
        );
        if(stock.getQuantity_in_bank() < 0) throw new InsufficientStockException("No stock in bank");
        stockRepository.save(stock);
    }

    public List<LogDto> getLogs(){
        return auditService.getLogs();
    }

    @Async
    public void shutdownInstance(){
        ((ConfigurableApplicationContext) context).close();
    }
}
