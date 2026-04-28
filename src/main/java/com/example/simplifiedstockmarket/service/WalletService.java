package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.controller.dto.OperationType;
import com.example.simplifiedstockmarket.exeptions.InsufficientStockException;
import com.example.simplifiedstockmarket.exeptions.StockNotFoundException;
import com.example.simplifiedstockmarket.model.Stock;
import com.example.simplifiedstockmarket.model.Wallet;
import com.example.simplifiedstockmarket.model.WalletsContent;
import com.example.simplifiedstockmarket.model.WalletsContentID;
import com.example.simplifiedstockmarket.repository.StockRepository;
import com.example.simplifiedstockmarket.repository.WalletContentRepository;
import com.example.simplifiedstockmarket.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WalletService {
    private final AuditService auditService;
    private final BankService bankService;
    private final WalletRepository walletRepository;
    private final WalletContentRepository walletContentRepository;
    private final StockRepository stockRepository;

    public WalletService(
            AuditService service,
            WalletRepository walletRepository,
            WalletContentRepository walletContentRepository,
            StockRepository stockRepository,
            BankService bankService
    ){
        this.auditService = service;
        this.bankService = bankService;
        this.walletRepository = walletRepository;
        this.walletContentRepository = walletContentRepository;
        this.stockRepository = stockRepository;
    }

    private int valueChangeByType(OperationType type){
        return switch (type){
            case SELL -> -1;
            case BUY -> 1;
        };
    }

    @Transactional
    private Wallet createWallet(String walletId){
        Wallet newWallet = new Wallet();
        newWallet.setId(walletId);
        this.walletRepository.save(newWallet);
        return newWallet;
    }

    private WalletsContent newWalletContent(Wallet wallet, Stock stock){
        return new WalletsContent(0, wallet.getId(), stock.getId());
    }

    public void executeOperationWithLog(OperationType type, String walletId, String stockName){
        executeOperation(type, walletId, stockName);
        auditService.addLog(type, walletId, stockName);
    }

    @Transactional
    private void executeOperation(OperationType type, String walletId, String stockName){
        Wallet wallet = walletRepository.findById(walletId)
                .orElse(this.createWallet(walletId));
        Stock stock = stockRepository.findByName(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock not found " + stockName));

        WalletsContent content = walletContentRepository.findById(new WalletsContentID(wallet, stock))
                .orElse(newWalletContent(wallet, stock));
        content.setQuantity_in_wallet(content.getQuantity_in_wallet() + this.valueChangeByType(type));
        if(content.getQuantity_in_wallet() < 0) throw new InsufficientStockException("no stock in wallet");
        bankService.WalletOperation(stock.getId(), type);

        walletContentRepository.save(content);
    }
}
