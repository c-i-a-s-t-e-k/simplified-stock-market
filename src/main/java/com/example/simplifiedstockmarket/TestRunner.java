package com.example.simplifiedstockmarket;

import com.example.simplifiedstockmarket.model.Wallet;
import com.example.simplifiedstockmarket.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public void run(String... args) {
        // stwórz wallet
        Wallet wallet = new Wallet();
        wallet.setId("test-wallet-1");
        walletRepository.save(wallet);
        System.out.println("Dodano: " + wallet.getId());

        // usuń wallet
        walletRepository.deleteById("test-wallet-1");
        System.out.println("Usunięto wallet");
    }
}