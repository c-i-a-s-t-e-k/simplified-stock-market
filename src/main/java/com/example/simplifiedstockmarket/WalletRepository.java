package com.example.simplifiedstockmarket;

import com.example.simplifiedstockmarket.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, String> {
}
