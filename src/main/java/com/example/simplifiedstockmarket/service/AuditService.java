package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.model.Log;
import com.example.simplifiedstockmarket.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditService {
    private final LogRepository logRepository;

    public AuditService(LogRepository repository){
        this.logRepository = repository;
    }

    @Transactional
    public List<Log> getLogs(){
        return logRepository.findAll();
    }
}
