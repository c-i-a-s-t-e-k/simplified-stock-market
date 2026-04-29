package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.WalletsContent;
import com.example.simplifiedstockmarket.model.WalletsContentID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletContentRepository extends JpaRepository<WalletsContent, WalletsContentID> {
    List<WalletsContent> findByWalletId(String walletId);
}
