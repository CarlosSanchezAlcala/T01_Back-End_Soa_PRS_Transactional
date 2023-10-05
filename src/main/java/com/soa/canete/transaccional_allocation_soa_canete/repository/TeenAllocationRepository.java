package com.soa.canete.transaccional_allocation_soa_canete.repository;

import com.soa.canete.transaccional_allocation_soa_canete.domain.model.Teen;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TeenAllocationRepository extends ReactiveCrudRepository<Teen, Integer> {
}
