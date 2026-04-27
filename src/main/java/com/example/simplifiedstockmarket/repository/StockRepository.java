package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findByName(String name);
}
