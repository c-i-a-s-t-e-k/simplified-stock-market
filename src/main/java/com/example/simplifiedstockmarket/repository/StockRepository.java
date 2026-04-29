package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findByName(String name);

    @Modifying
    @Query("DELETE FROM Stock s WHERE s NOT IN :items")
    void deleteAllNotIn(@Param("items") Iterable<Stock> items);

    @Modifying
    @Query(value = """
        INSERT INTO stock (id, name, quantity_in_bank)\s
        VALUES (gen_random_uuid(), :#{#stock.name}, :#{#stock.quantity_in_bank})
        ON CONFLICT (name) DO UPDATE\s
        SET quantity_in_bank = EXCLUDED.quantity_in_bank"""
            , nativeQuery = true)
    void upsert(@Param("stock") Stock stock);
}
