package com.soa.canete.transaccional_allocation_soa_canete.web;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.DataTeenFuncionaryTransaccional;
import com.soa.canete.transaccional_allocation_soa_canete.repository.TransaccionalAllocationRepository;
import com.soa.canete.transaccional_allocation_soa_canete.service.TransaccionalAllocationService;
import lombok.RequiredArgsConstructor;
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

}
