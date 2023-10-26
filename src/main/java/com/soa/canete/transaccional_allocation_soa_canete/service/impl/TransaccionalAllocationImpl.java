package com.soa.canete.transaccional_allocation_soa_canete.service.impl;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Funcionary.FuncionaryResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.TeenResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalAllocationMapper;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.Teen;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocation;
import com.soa.canete.transaccional_allocation_soa_canete.exception.ResourceNotFoundException;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TeenAllocationRepository;
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

    final TeenAllocationRepository _teenAllocationRepository;
    final TransaccionalAllocationRepository _transaccionalAllocationRepository;

    @Override
    public Mono<DataTeenFuncionaryTransaccional> findById(Integer id_funcionaryteend) {
        Mono<TransaccionalAllocation> family = _transaccionalAllocationRepository.findById(id_funcionaryteend);
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/teenData/" + dataFamily.getId_teen())
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
        Flux<TransaccionalAllocation> family = _transaccionalAllocationRepository.findAll()
                .sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed());
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/teenData/" + dataFamily.getId_teen())
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
    public Flux<TransaccionalAllocationResponseDto> getDataIdFuncionaryTeen() {
        return this._transaccionalAllocationRepository.findAll()
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Flux<Teen> getDataTeenNoRegisterTransactional() {
        return _teenAllocationRepository.findAll()
                .collectList()
                .flatMapMany(datas -> _transaccionalAllocationRepository.findAll()
                        .map(TransaccionalAllocation::getId_teen)
                        .collectList()
                        .flatMapMany(ids ->
                                Flux.fromIterable(datas)
                                        .filter(data -> !ids.contains(data.getId_teen()))
                        )
                );
    }

    @Override
    public Flux<DataTeenFuncionaryTransaccional> findAllDataActive() {
        Flux<TransaccionalAllocation> family = _transaccionalAllocationRepository.findAll()
                .sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed())
                .filter((active) -> active.getStatus().equals("A"));
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/teenData/" + dataFamily.getId_teen())
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
    public Flux<DataTeenFuncionaryTransaccional> findAllDataInactive() {
        Flux<TransaccionalAllocation> family = _transaccionalAllocationRepository.findAll()
                .sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed())
                .filter((active) -> active.getStatus().equals("I"));
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8081/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/api/teenData/" + dataFamily.getId_teen())
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
        return this._transaccionalAllocationRepository.save(toModel(request))
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> updateDataTransaction(TransaccionalAllocationRequestDto request, Integer id_funcionaryteend) {
        return this._transaccionalAllocationRepository.findById(id_funcionaryteend)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("El identificador: " + id_funcionaryteend + " no fue encontrado")))
                .flatMap((dataAsignationOrTransaction) -> this._transaccionalAllocationRepository.save(toModel(request, dataAsignationOrTransaction.getId_funcionaryteend())))
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> deleteLogicalTransaction(Integer id_funcionaryteend) {
        return this._transaccionalAllocationRepository.findById(id_funcionaryteend)
                .map((deleteData) -> {
                    deleteData.setStatus("I");
                    return deleteData;
                })
                .flatMap(_transaccionalAllocationRepository::save)
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> reactiveLogicalTransaction(Integer id_funcionaryteend) {
        return this._transaccionalAllocationRepository.findById(id_funcionaryteend)
                .map((deleteData) -> {
                    deleteData.setStatus("A");
                    return deleteData;
                })
                .flatMap(_transaccionalAllocationRepository::save)
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Mono<Void> deleteDataCompleteTransaction(Integer id_funcionaryteend) {
        return this._transaccionalAllocationRepository.deleteById(id_funcionaryteend);
    }
}
