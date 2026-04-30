package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Stock;
import com.example.simplifiedstockmarket.model.WalletsContent;
import com.example.simplifiedstockmarket.model.WalletsContentID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface WalletContentRepository extends JpaRepository<WalletsContent, WalletsContentID> {
    List<WalletsContent> findByWalletId(String walletId);

    @Modifying
    @Query("DELETE FROM WalletsContent wc WHERE wc.quantity_in_wallet = 0")
    void clearWalletsContent();

    @Modifying
    @Query(value = "LOCK TABLE wallet_content IN SHARE ROW EXCLUSIVE MODE", nativeQuery = true)
    void lockTable();

    @Query("SELECT DISTINCT wc.stock FROM WalletsContent wc WHERE wc.quantity_in_wallet > 0")
    Set<Stock> findAllUsedStocks();
}
