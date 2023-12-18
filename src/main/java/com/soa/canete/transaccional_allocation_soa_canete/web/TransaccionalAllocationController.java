package com.soa.canete.transaccional_allocation_soa_canete.web;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.MasivTeen;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.Teen;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TransaccionalAllocationRepository;
import com.soa.canete.transaccional_allocation_soa_canete.service.TransaccionalAllocationService;
import com.soa.canete.transaccional_allocation_soa_canete.service.impl.TransaccionalAllocationImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/transaccionalData")
@RequiredArgsConstructor
public class TransaccionalAllocationController {

    final TransaccionalAllocationRepository transaccionalAllocationRepository;
    final TransaccionalAllocationService transaccionalAllocationService;
    final TransaccionalAllocationImpl transaccionalAllocation;

    @GetMapping("/{idFamilyData}")
    public Mono<DataTeenFuncionaryTransaccional> getTransaccionDataById(@PathVariable Integer idFamilyData, @RequestHeader("Authorization") String token) {
        return this.transaccionalAllocationService.findById(idFamilyData, token);
    }

    @GetMapping("/listData")
    public Flux<DataTeenFuncionaryTransaccional> getDataCompleteTransaccional(@RequestHeader("Authorization") String token) {
        return this.transaccionalAllocationService.findAll(token);
    }

    @GetMapping("/listDataIdRegister")
    public Flux<TransaccionalAllocationResponseDto> getDataIdExistent() {
        return this.transaccionalAllocationService.getDataIdFuncionaryTeen();
    }

    @GetMapping("listData/idTeen/{id_teen}")
    public Mono<TransaccionalAllocationResponseDto> getDataForIdTeen(@PathVariable Integer id_teen) {
        return this.transaccionalAllocationService.findByIdTeen(id_teen);
    }

    @PostMapping("/bulk")
    public Mono<Void> updateTeenBulk(@RequestBody MasivTeen dto) {
        return this.transaccionalAllocationService.updateTeenBulk(dto);
    }

    @GetMapping("/listData/noRegisteredTeen")
    public Flux<Teen> getDataIdNoRegistered() {
        return this.transaccionalAllocationService.getDataTeenNoRegisterTransactional();
    }

    @GetMapping("/listData/active")
    public Flux<DataTeenFuncionaryTransaccional> getDataActiveAsignation(@RequestHeader("Authorization") String token) {
        return this.transaccionalAllocationService.findAllDataActive(token);
    }

    @GetMapping("/listData/inactive")
    public Flux<DataTeenFuncionaryTransaccional> getDataInactiveAsignation(@RequestHeader("Authorization") String token) {
        return this.transaccionalAllocationService.findAllDataInactive(token);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<TransaccionalResponseDto> saveNewDataTransaccional(@RequestBody TransaccionalRequestDto dto) {
        return this.transaccionalAllocationService.saveNewDataTransaccional(dto);
    }

    private TransaccionalAllocationRequestDto createCopyWithStatusI(TransaccionalAllocationResponseDto originalDto) {
        TransaccionalAllocationRequestDto copyDto = new TransaccionalAllocationRequestDto();
        // Copiar todos los campos del original a la copia
        copyDto.setDescription(originalDto.getDescription());
        copyDto.setStatus("I"); // Cambiar "status" a "I"
        copyDto.setDate_hour_register(originalDto.getDate_hour_register());
        copyDto.setFunction_start(originalDto.getFunction_start());
        copyDto.setIdTeen(originalDto.getIdTeen ());
        copyDto.setId_funcionary(originalDto.getId_funcionary());

        return copyDto;
    }

    @PutMapping("/{id_funcionaryteend}")
    public Mono<TransaccionalAllocationResponseDto> updateDataTransaccional(@RequestBody TransaccionalAllocationRequestDto dto, @PathVariable Integer id_funcionaryteend) {
        // Obtener los datos originales antes de la actualización
        return this.transaccionalAllocationService.getDataById(id_funcionaryteend)  // Reemplaza getDataById con el método correcto que obtiene los datos por ID
                .flatMap(originalData -> {
                    // Realizar copia con "status" cambiado a "I" antes de la actualización
                    TransaccionalAllocationRequestDto copyDto = createCopyWithStatusI(originalData);
                    // Guardar la copia mediante POST
                    return this.transaccionalAllocationService.saveNewDataTransaccionals(copyDto)
                            .then(this.transaccionalAllocationService.updateDataTransaction(dto, id_funcionaryteend));
                });
    }

    @PatchMapping("/deleteLogical/{idTeen}")
    public Mono<TransaccionalAllocationResponseDto> deleteDataTransaccional(@PathVariable Integer idTeen) {
        return this.transaccionalAllocationService.deleteLogicalTransaction(idTeen);
    }

    @PatchMapping("/reactiveLogical/{id_funcionaryteend}")
    public Mono<TransaccionalAllocationResponseDto> reactiveDataTransaccional(@PathVariable Integer id_funcionaryteend) {
        return this.transaccionalAllocationService.reactiveLogicalTransaction(id_funcionaryteend);
    }

    @DeleteMapping("/{id_funcionaryteend}")
    public Mono<Void> deleteDataCompleteTransactional(@PathVariable Integer id_funcionaryteend) {
        return this.transaccionalAllocationService.deleteDataCompleteTransaction(id_funcionaryteend);
    }

    @GetMapping("/report")
    public Mono<ResponseEntity<Resource>> exportAsignationPrograms(@RequestHeader("Authorization") String token){return transaccionalAllocation.exportAsignationReport(token);}

}
