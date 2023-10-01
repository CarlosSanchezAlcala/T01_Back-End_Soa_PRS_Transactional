package com.soa.canete.transaccional_allocation_soa_canete.service.impl;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Funcionary.FuncionaryResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.TeenResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalAllocationMapper;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocation;
import com.soa.canete.transaccional_allocation_soa_canete.exception.ResourceNotFoundException;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TransaccionalAllocationRepository;
import com.soa.canete.transaccional_allocation_soa_canete.service.TransaccionalAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

import static com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalAllocationMapper.toModel;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionalAllocationImpl implements TransaccionalAllocationService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    final TransaccionalAllocationRepository transaccionalAllocationRepository;

    @Override
    public Mono<DataTeenFuncionaryTransaccional> findById(Integer id_funcionaryteend) {
        Mono<TransaccionalAllocation> family = transaccionalAllocationRepository.findById(id_funcionaryteend);
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/teenData/" + dataFamily.getId_adolescente())
                    .retrieve()
                    .bodyToMono(TeenResponseDto.class);
            return funcionaryResponseDtoMono.zipWith(teenResponseDtoMono).map(dataGeneral -> {
                FuncionaryResponseDto legalGuardianResponseDtoData = dataGeneral.getT1();
                TeenResponseDto adolescentResponseDtoData = dataGeneral.getT2();
                DataTeenFuncionaryTransaccional dataTeenFuncionaryTransaccional = new DataTeenFuncionaryTransaccional();
                dataTeenFuncionaryTransaccional.setTransaccionalAllocation(dataFamily);
                dataTeenFuncionaryTransaccional.setTeenResponseDto(adolescentResponseDtoData);
                dataTeenFuncionaryTransaccional.setFuncionaryResponseDto(legalGuardianResponseDtoData);
                return dataTeenFuncionaryTransaccional;
            });
        });
    }

    @Override
    public Flux<DataTeenFuncionaryTransaccional> findAll() {
        Flux<TransaccionalAllocation> family = transaccionalAllocationRepository.findAll().sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed());
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/teenData/" + dataFamily.getId_adolescente())
                    .retrieve()
                    .bodyToMono(TeenResponseDto.class);
            return funcionaryResponseDtoMono.zipWith(teenResponseDtoMono).map(dataGeneral -> {
                FuncionaryResponseDto legalGuardianResponseDtoData = dataGeneral.getT1();
                TeenResponseDto adolescentResponseDtoData = dataGeneral.getT2();
                DataTeenFuncionaryTransaccional dataTeenFuncionaryTransaccional = new DataTeenFuncionaryTransaccional();
                dataTeenFuncionaryTransaccional.setTransaccionalAllocation(dataFamily);
                dataTeenFuncionaryTransaccional.setTeenResponseDto(adolescentResponseDtoData);
                dataTeenFuncionaryTransaccional.setFuncionaryResponseDto(legalGuardianResponseDtoData);
                return dataTeenFuncionaryTransaccional;
            });
        });
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> saveNewDataTransaccional(TransaccionalAllocationRequestDto request) {
        return this.transaccionalAllocationRepository.save(toModel(request))
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> updateDataTransaction(TransaccionalAllocationRequestDto request, Integer id_transaction) {
        return this.transaccionalAllocationRepository.findById(id_transaction)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("El identificador: " + id_transaction + " no fue encontrado")))
                .flatMap((dataAsignationOrTransaction) -> this.transaccionalAllocationRepository.save(toModel(request, dataAsignationOrTransaction.getId_funcionaryteend())))
                .map(TransaccionalAllocationMapper::toDto);
    }
}
