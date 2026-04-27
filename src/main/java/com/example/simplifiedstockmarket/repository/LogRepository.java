package com.example.simplifiedstockmarket.repository;

import com.example.simplifiedstockmarket.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log,String> {
}
