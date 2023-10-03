package com.soa.canete.transaccional_allocation_soa_canete.web;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationRequestDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional.TransaccionalAllocationResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TransaccionalAllocationRepository;
import com.soa.canete.transaccional_allocation_soa_canete.service.TransaccionalAllocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/transaccionalData")
@RequiredArgsConstructor
public class TransaccionalAllocationController {

    final TransaccionalAllocationRepository transaccionalAllocationRepository;

    final TransaccionalAllocationService transaccionalAllocationService;

    @GetMapping("/{idFamilyData}")
    public Mono<DataTeenFuncionaryTransaccional> getTransaccionDataById(@PathVariable Integer idFamilyData) {
        return this.transaccionalAllocationService.findById(idFamilyData);
    }

    @GetMapping("/listData")
    public Flux<DataTeenFuncionaryTransaccional> getDataCompleteTransaccional() {
        return this.transaccionalAllocationService.findAll();
    }

    @GetMapping("/listData/active")
    public Flux<DataTeenFuncionaryTransaccional> getDataActiveAsignation() {
        return this.transaccionalAllocationService.findAllDataActive();
    }

    @GetMapping("/listData/inactive")
    public Flux<DataTeenFuncionaryTransaccional> getDataInativeAsignation() {
        return this.transaccionalAllocationService.findAllDataInactive();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<TransaccionalAllocationResponseDto> saveNewDataTransaccional(@RequestBody TransaccionalAllocationRequestDto dto) {
        return this.transaccionalAllocationService.saveNewDataTransaccional(dto);
    }

    @PutMapping("/{id_funcionaryteend}")
    public Mono<TransaccionalAllocationResponseDto> updateDataTransaccional(@RequestBody TransaccionalAllocationRequestDto dto, @PathVariable Integer id_funcionaryteend) {
        return this.transaccionalAllocationService.updateDataTransaction(dto, id_funcionaryteend);
    }

    @PatchMapping("/deleteLogical/{id_funcionaryteend}")
    public Mono<TransaccionalAllocationResponseDto> deleteDataTransaccional(@PathVariable Integer id_funcionaryteend) {
        return this.transaccionalAllocationService.deleteLogicalTransaction(id_funcionaryteend);
    }

    @PatchMapping("/reactiveLogical/{id_funcionaryteend}")
    public Mono<TransaccionalAllocationResponseDto> reactiveDataTransaccional(@PathVariable Integer id_funcionaryteend) {
        return this.transaccionalAllocationService.reactiveLogicalTransaction(id_funcionaryteend);
    }

}
