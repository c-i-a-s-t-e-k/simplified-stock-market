package com.example.simplifiedstockmarket.service;

import com.example.simplifiedstockmarket.controller.dto.LogDto;
import com.example.simplifiedstockmarket.controller.dto.OperationType;
import com.example.simplifiedstockmarket.mapper.LogMapper;
import com.example.simplifiedstockmarket.model.Log;
import com.example.simplifiedstockmarket.model.LogType;
import com.example.simplifiedstockmarket.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditService {
    private final LogRepository logRepository;
    private final LogMapper logMapper;

    public AuditService(LogRepository repository, LogMapper logMapper){
        this.logRepository = repository;
        this.logMapper = logMapper;
    }

    @Transactional
    public List<LogDto> getLogs(){
        return logRepository.findAll()
                .stream()
                .map(logMapper::toDto)
                .collect(Collectors.toList());
    }

    private LogType fromOperationType(OperationType type){
        return switch (type){
            case SELL -> LogType.SELL;
            case BUY -> LogType.BUY;
        };
    }

    @Transactional
    public void addLog(OperationType type, String wallerId, String stockName){
        logRepository.save(
                new Log(UUID.randomUUID(), wallerId, stockName, fromOperationType(type))
        );
    }
}
