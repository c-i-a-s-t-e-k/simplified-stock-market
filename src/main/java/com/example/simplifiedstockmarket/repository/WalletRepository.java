package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, String> {
    @Modifying
    @Query(value = """
    INSERT INTO wallet (id) 
    VALUES (:id) 
    ON CONFLICT (id) DO NOTHING
    """, nativeQuery = true)
    void insertIfNotExists(@Param("id") String id);
}
