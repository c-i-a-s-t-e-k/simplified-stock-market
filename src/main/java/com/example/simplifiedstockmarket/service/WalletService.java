package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.controller.dto.OperationType;
import com.example.simplifiedstockmarket.controller.dto.StockDto;
import com.example.simplifiedstockmarket.controller.dto.WalletStatusDto;
import com.example.simplifiedstockmarket.exeptions.InsufficientStockException;
import com.example.simplifiedstockmarket.exeptions.StockNotFoundException;
import com.example.simplifiedstockmarket.exeptions.WalletNotFoundException;
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

import java.util.ArrayList;
import java.util.List;

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

    private Wallet getOrCreateWallet(String walletId) {
        walletRepository.insertIfNotExists(walletId);
        return walletRepository.findById(walletId)
                .orElseThrow(); // nigdy nie poleci bo właśnie wstawiliśmy
    }

    private WalletsContent newWalletContent(Wallet wallet, Stock stock){
        return new WalletsContent(0, wallet, stock);
    }

    public void executeOperationWithLog(OperationType type, String walletId, String stockName){
        executeOperation(type, walletId, stockName);
        auditService.addLog(type, walletId, stockName);
    }

    @Transactional
    public void executeOperation(OperationType type, String walletId, String stockName){
        Wallet wallet = getOrCreateWallet(walletId);
        Stock stock = stockRepository.findByNameWithLock(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock not found " + stockName));

        WalletsContent content = walletContentRepository.findById(new WalletsContentID(wallet, stock))
                .orElse(newWalletContent(wallet, stock));
        content.setQuantity_in_wallet(content.getQuantity_in_wallet() + this.valueChangeByType(type));
        if(content.getQuantity_in_wallet() < 0) throw new InsufficientStockException("no stock in wallet");
        bankService.WalletOperation(stock, type);

        walletContentRepository.save(content);
    }

    private List<StockDto> getWalletStocks(String walletId){
        List<WalletsContent> walletsContent = walletContentRepository.findByWalletId(walletId);
        List<StockDto> stocks = new ArrayList<>();
        for(WalletsContent content: walletsContent){
            stocks.add(new StockDto(content.getStock().getName(), content.getQuantity_in_wallet()));
        }
        return stocks;
    }

    private boolean isWalletExist(String walletId){
        return walletRepository.existsById(walletId);
    }



    @Transactional
    public WalletStatusDto getWalletStatus(String walletId){
        if(!this.isWalletExist(walletId)) throw new WalletNotFoundException("Wallet of id " + walletId +" not found." );

        WalletStatusDto walletStatusDto = new WalletStatusDto();
        walletStatusDto.setId(walletId);
        walletStatusDto.setStocks(getWalletStocks(walletId));
        return walletStatusDto;
    }

    @Transactional
    public int getStockQuantityInWallet(String walletId, String stockName){
        Stock stock = stockRepository.findByName(stockName)
                .orElseThrow(() -> new StockNotFoundException("Stock of name " + stockName + " do not exist."));
        WalletsContent content = walletContentRepository.findById(new WalletsContentID(walletId, stock.getId()))
                .orElse(null);
        if(content == null) return 0;
        return content.getQuantity_in_wallet();
    }
}
