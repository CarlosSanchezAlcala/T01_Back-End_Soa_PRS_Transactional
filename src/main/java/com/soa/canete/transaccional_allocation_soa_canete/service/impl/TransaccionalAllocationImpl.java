package com.soa.canete.transaccional_allocation_soa_canete.service.impl;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Funcionary.FuncionaryResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.MasivTeen;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.TeenResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalAllocationMapper;
import com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalMapper;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.Teen;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocation;
import com.soa.canete.transaccional_allocation_soa_canete.domain.report.AsignationProgramsReportDto;
import com.soa.canete.transaccional_allocation_soa_canete.exception.ResourceNotFoundException;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TeenAllocationRepository;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TransaccionalAllocationRepository;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TransaccionalRepository;
import com.soa.canete.transaccional_allocation_soa_canete.service.TransaccionalAllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalAllocationMapper.toModel;
import static com.soa.canete.transaccional_allocation_soa_canete.domain.mapper.TransaccionalMapper.toModels;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransaccionalAllocationImpl implements TransaccionalAllocationService {

    @Autowired
    public TransaccionalAllocationImpl(TransaccionalAllocationRepository transaccionalAllocationRepository,
                                       TransaccionalRepository transaccionalRepository,
                                       TeenAllocationRepository teenAllocationRepository,
                                       WebClient.Builder webClientBuilder,
                                       TeenAllocationRepository teenRepository) {
        this._transaccionalAllocationRepository = transaccionalAllocationRepository;
        this._teenAllocationRepository = teenAllocationRepository;
        this.webClientBuilder = webClientBuilder;
        this._teenRepository = teenRepository;
        this._transaccionalRepository = transaccionalRepository;
    }

    private WebClient.Builder webClientBuilder;

    final TeenAllocationRepository _teenRepository;
    final TeenAllocationRepository _teenAllocationRepository;
    final TransaccionalAllocationRepository _transaccionalAllocationRepository;
    final TransaccionalRepository _transaccionalRepository;

    @Override
    public Mono<DataTeenFuncionaryTransaccional> findById(Integer id_funcionaryteend, String token) {
        Mono<TransaccionalAllocation> family = _transaccionalAllocationRepository.findById(id_funcionaryteend);
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .header(HttpHeaders.AUTHORIZATION,token)
                    //.header(HttpHeaders.AUTHORIZATION, "Bearer " + tuTokenDeAcceso)  // Agregar token de acceso
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/teenData/listUnique/" + dataFamily.getIdTeen())
                    .header(HttpHeaders.AUTHORIZATION,token)
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
    public Flux<DataTeenFuncionaryTransaccional> findAll(String token) {
        Flux<TransaccionalAllocation> family = _transaccionalAllocationRepository.findAll()
                .sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed());
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/teenData/listUnique/" + dataFamily.getIdTeen())
                    .header(HttpHeaders.AUTHORIZATION, token)
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
    public Flux<DataTeenFuncionaryTransaccional> findAllDataActive(String token) {
        System.out.println("Token: " + token);
        Flux<TransaccionalAllocation> family = _transaccionalAllocationRepository.findAll()
                .sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed())
                .filter((active) -> active.getStatus().equals("A"));
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/teenData/listUnique/" + dataFamily.getIdTeen())
                    .header(HttpHeaders.AUTHORIZATION, token)
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
    public Flux<DataTeenFuncionaryTransaccional> findAllDataInactive(String token) {
        Flux<TransaccionalAllocation> family = _transaccionalAllocationRepository.findAll()
                .sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed())
                .filter((active) -> active.getStatus().equals("I"));
        return family.flatMap(dataFamily -> {
            Mono<FuncionaryResponseDto> funcionaryResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/funcionaryData/" + dataFamily.getId_funcionary())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);
            Mono<TeenResponseDto> teenResponseDtoMono = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/teenData/listUnique/" + dataFamily.getIdTeen())
                    .header(HttpHeaders.AUTHORIZATION, token)
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
    public Mono<TransaccionalResponseDto> saveNewDataTransaccional(TransaccionalRequestDto request) {
        return this._transaccionalRepository.save(toModels(request))
                .map(TransaccionalMapper::toDtos);
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> saveNewDataTransaccionals(TransaccionalAllocationRequestDto request) {
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
                            .idTeen(Arrays.asList(res.getId_teen()))
                            .description(dto.getDescription())
                            .id_funcionary(dto.getId_funcionary())
                            .function_start(dto.getFunction_start())
                            .status("A")
                            .build();
                    return saveNewDataTransaccionals(transac);
                }).collect(Collectors.toList());
        return Flux.merge(masiv).then(Mono.empty());
    }

    @Override
    public Mono<TransaccionalAllocationResponseDto> getDataById(Integer id) {
        return this._transaccionalAllocationRepository.findById(id)
                .map(TransaccionalAllocationMapper::toDto);
    }

    @Override
    public Flux<AsignationProgramsReportDto> listAsignation(String token) {
        Flux<TransaccionalAllocation> asignation = _transaccionalAllocationRepository.findAll().sort(Comparator.comparing(TransaccionalAllocation::getId_funcionaryteend).reversed());
        return asignation.flatMap(dataasugnation -> {
            Mono<FuncionaryResponseDto> programs = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/funcionaryData/" + dataasugnation.getId_funcionary())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(FuncionaryResponseDto.class);

            Mono<TeenResponseDto> activities = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8080/api/teenData/listUnique/" + dataasugnation.getIdTeen())
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .bodyToMono(TeenResponseDto.class);
            return programs.zipWith(activities).map(data ->{
                FuncionaryResponseDto programsDto = data.getT1();
                TeenResponseDto activitiesDto = data.getT2();
                AsignationProgramsReportDto asignationProgramsReportDto = new AsignationProgramsReportDto();
                asignationProgramsReportDto.setId(dataasugnation.getIdTeen());
                asignationProgramsReportDto.setName_funcionary(programsDto.getName());
                asignationProgramsReportDto.setName_teen(activitiesDto.getName());
                asignationProgramsReportDto.setDate_funciont(dataasugnation.getFunction_start());
                asignationProgramsReportDto.setDescription(dataasugnation.getDescription());
                return asignationProgramsReportDto;
            });
        });

    }

    public Mono<ResponseEntity<Resource>> exportAsignationReport(String token) {
        Flux<AsignationProgramsReportDto> asignationReportDtoFlux = listAsignation(token);

        return asignationReportDtoFlux.collectList()
                .flatMap(asignationReportDtos -> {
                    try {
                        final HashMap<String, Object> parameters = new HashMap<>();
                        parameters.put("dataAsignation", new JRBeanCollectionDataSource(asignationReportDtos));
                        return Mono.just(jasperReport("transaccional.jasper", "Asignacion", parameters));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Mono.error(e);
                    }
                })
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    public ResponseEntity<Resource> jasperReport(String reportPath, String outputFileName, HashMap<String, Object> parameters) throws JRException {

        try {
            final File file = ResourceUtils.getFile("classpath:"+ reportPath);
            final File imgLogo = ResourceUtils.getFile("classpath:images/logo.png");
            final JasperReport report = (JasperReport) JRLoader.loadObject(file);
            parameters.put("logo", new FileInputStream(imgLogo));

            JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
            byte[] reporte = JasperExportManager.exportReportToPdf(jasperPrint);

            StringBuilder stringBuilder = new StringBuilder().append("InvoicePDF:");
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(stringBuilder.append(outputFileName).toString())
                    .build();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(contentDisposition);
            return ResponseEntity.ok().contentLength((long) reporte.length)
                    .contentType(MediaType.APPLICATION_PDF)
                    .headers(headers).body(new ByteArrayResource(reporte));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
