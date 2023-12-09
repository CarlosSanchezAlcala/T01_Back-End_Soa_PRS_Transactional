package com.soa.canete.transaccional_allocation_soa_canete.service;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.MasivTeen;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.Teen;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public interface TransaccionalAllocationService {

    Mono<DataTeenFuncionaryTransaccional> findById(Integer id_funcionaryteend);

    Flux<DataTeenFuncionaryTransaccional> findAll();

    Mono<TransaccionalAllocationResponseDto> findByIdTeen(Integer id_teen);

    Flux<TransaccionalAllocationResponseDto> getDataIdFuncionaryTeen();

    Flux<Teen> getDataTeenNoRegisterTransactional();

    Flux<DataTeenFuncionaryTransaccional> findAllDataActive();

    Flux<DataTeenFuncionaryTransaccional> findAllDataInactive();

    Mono<TransaccionalAllocationResponseDto> saveNewDataTransaccional(TransaccionalAllocationRequestDto request);

    Mono<TransaccionalAllocationResponseDto> updateDataTransaction(TransaccionalAllocationRequestDto request, Integer id_funcionaryteend);

    Mono<TransaccionalAllocationResponseDto> deleteLogicalTransaction(Integer idTeen);

    Mono<TransaccionalAllocationResponseDto> reactiveLogicalTransaction(Integer id_funcionaryteend);

    Mono<Void> deleteDataCompleteTransaction(Integer id_funcionaryteend);

    Mono<Void> updateTeenBulk(MasivTeen dto);
}
