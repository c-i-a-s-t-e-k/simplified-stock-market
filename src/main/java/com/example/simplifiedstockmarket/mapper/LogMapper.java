package com.example.simplifiedstockmarket.mapper;

import com.example.simplifiedstockmarket.controller.dto.LogDto;
import com.example.simplifiedstockmarket.model.Log;
import com.example.simplifiedstockmarket.model.LogType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogMapper {
    LogDto toDto(Log log);

    default String logTypeToString(LogType type) {
        return type.name().toLowerCase();
    }
}
