package com.soa.canete.transaccional_allocation_soa_canete.service.impl;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Funcionary.FuncionaryResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.MasivTeen;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalAllocationMapper.toModel;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionalAllocationImpl implements TransaccionalAllocationService {

    @Autowired
    public TransaccionalAllocationImpl(TransaccionalAllocationRepository transaccionalAllocationRepository,
                                       TeenAllocationRepository teenAllocationRepository,
                                       WebClient.Builder webClientBuilder,
                                       TeenAllocationRepository teenRepository) {
        this._transaccionalAllocationRepository = transaccionalAllocationRepository;
        this._teenAllocationRepository = teenAllocationRepository;
        this.webClientBuilder = webClientBuilder;
        this._teenRepository = teenRepository;
    }

    private WebClient.Builder webClientBuilder;

    final TeenAllocationRepository _teenRepository;
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
                    .uri("http://localhost:8082/api/teenData/listUnique/" + dataFamily.getIdTeen())
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
                    .uri("http://localhost:8082/api/teenData/listUnique/" + dataFamily.getIdTeen())
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
    public Mono<TransaccionalAllocationResponseDto> findByIdTeen(Integer id_teen) {
        return this._transaccionalAllocationRepository.findByIdTeen(id_teen)
                .map(TransaccionalAllocationMapper::toDto);
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
                        .map(TransaccionalAllocation::getIdTeen)
                        .collectList()
                        .flatMapMany(ids ->
                                Flux.fromIterable(datas)
                                        .filter(data -> !ids.contains(data.getId_teen()))
                                        .filter(data -> "A".equals(data.getStatus()))
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
                    .uri("http://localhost:8082/api/teenData/listUnique/" + dataFamily.getIdTeen())
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
                    .uri("http://localhost:8082/api/teenData/listUnique/" + dataFamily.getIdTeen())
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
    public Mono<TransaccionalAllocationResponseDto> deleteLogicalTransaction(Integer idTeen) {
        return this._teenRepository.findById(idTeen)
                .flatMap((teen) -> {
                    // Verifica si el estado del teen es "T"
                    System.out.println("Information Teen: " + teen);
                    if ("T".equals(teen.getStatus())) {
                        // Busca la asignación relacionada con el teen
                        return this._transaccionalAllocationRepository.findByIdTeen(idTeen)
                                .flatMap((deleteData) -> {
                                    deleteData.setStatus("I");
                                    // Guarda la asignación con el nuevo estado
                                    return _transaccionalAllocationRepository.save(deleteData)
                                            .map(TransaccionalAllocationMapper::toDto);
                                });
                    } else {
                        // Si el estado del teen no es "T", puedes manejarlo según tus requisitos
                        return Mono.error(new IllegalStateException("No se puede cambiar el estado de asignación porque el estado del teen no es 'T'."));
                    }
                });

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

    @Override
    public Mono<Void> updateTeenBulk(MasivTeen dto) {
        List<Mono<TransaccionalAllocationResponseDto>> masiv = dto.getTeens().stream()
                .map((res) -> {
                    TransaccionalAllocationRequestDto transac = TransaccionalAllocationRequestDto.builder()
                            .idTeen(res.getId_teen())
                            .description(dto.getDescription())
                            .id_funcionary(dto.getId_funcionary())
                            .function_start(dto.getFunction_start())
                            .status("A")
                            .build();
                    return saveNewDataTransaccional(transac);
                }).collect(Collectors.toList());
        return Flux.merge(masiv).then(Mono.empty());
    }
}
