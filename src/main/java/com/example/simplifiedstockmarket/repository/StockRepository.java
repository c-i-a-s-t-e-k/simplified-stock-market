package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, String> {
}
