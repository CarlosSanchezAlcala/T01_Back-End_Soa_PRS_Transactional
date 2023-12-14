package com.soa.canete.transaccional_allocation_soa_canete.repository;

import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocation;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocations;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TransaccionalRepository extends ReactiveCrudRepository<TransaccionalAllocations, Integer> {

    Mono<TransaccionalAllocation> findByIdTeen(Integer idTeen);

}
