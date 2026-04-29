package com.example.simplifiedstockmarket;

import com.example.simplifiedstockmarket.repository.LogRepository;
import com.example.simplifiedstockmarket.repository.StockRepository;
import com.example.simplifiedstockmarket.repository.WalletContentRepository;
import com.example.simplifiedstockmarket.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;


/**
 * This class Provides clear initialization so there will be no Wallets and Bank to be empty
 */
@Component
public class DataInitializer implements CommandLineRunner {
    private final List<JpaRepository> repositories;

    public DataInitializer(
        WalletRepository walletRepository,
        WalletContentRepository walletContentRepository,
        StockRepository stockRepository,
        LogRepository logRepository
    ){
        this.repositories = new LinkedList<>();
        repositories.add((JpaRepository) walletContentRepository);
        repositories.add((JpaRepository) walletRepository);
        repositories.add((JpaRepository) stockRepository);
        repositories.add((JpaRepository) logRepository);

    }
    @Override
    public void run(String... args) throws Exception {
        for(JpaRepository repository : this.repositories) repository.deleteAllInBatch();
    }
}
