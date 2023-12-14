package com.soa.canete.transaccional_allocation_soa_canete.domain.mapper;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocations;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransaccionalMapper {

    public static TransaccionalAllocations toModels(TransaccionalRequestDto dto) {
        return new TransaccionalAllocations(
                dto.getDescription(),
                dto.getStatus(),
                dto.getDate_hour_register(),
                dto.getFunction_start(),
                dto.getIdTeen(),
                dto.getId_funcionary()
        );
    }

    public static TransaccionalAllocations toModels(TransaccionalRequestDto dto, Integer id_funcionaryteend) {
        return new TransaccionalAllocations(
                id_funcionaryteend,
                dto.getDescription(),
                dto.getStatus(),
                dto.getDate_hour_register(),
                dto.getFunction_start(),
                dto.getIdTeen(),
                dto.getId_funcionary()
        );
    }

    public static TransaccionalResponseDto toDtos(TransaccionalAllocations model) {
        return new TransaccionalResponseDto(
                model.getId_funcionaryteend(),
                model.getDescription(),
                model.getStatus(),
                model.getDate_hour_register(),
                model.getFunction_start(),
                model.getIdTeen(),
                model.getId_funcionary()
        );
    }

}