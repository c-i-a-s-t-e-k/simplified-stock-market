package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, String> {
    Optional<Stock> findByName(String name);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Stock s WHERE s.name = :name")
    Optional<Stock> findByNameWithLock(@Param("name") String name);

    @Modifying
    @Query("DELETE FROM Stock s WHERE s NOT IN :items")
    void deleteAllNotIn(@Param("items") Iterable<Stock> items);

    @Modifying
    @Query(value = """
        INSERT INTO Stock (id, name, quantity_in_bank)\s
        VALUES (gen_random_uuid(), :#{#stock.name}, :#{#stock.quantity_in_bank})
        ON CONFLICT (name) DO UPDATE\s
        SET quantity_in_bank = EXCLUDED.quantity_in_bank"""
            , nativeQuery = true)
    void upsert(@Param("stock") Stock stock);

    @Modifying
    @Query(value = "LOCK TABLE Stock IN SHARE ROW EXCLUSIVE MODE", nativeQuery = true)
    void lockTable();
}
