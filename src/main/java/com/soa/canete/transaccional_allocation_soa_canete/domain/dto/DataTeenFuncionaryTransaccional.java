package com.soa.canete.transaccional_allocation_soa_canete.domain.dto;

import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Funcionary.FuncionaryResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen.TeenResponseDto;
import com.soa.canete.transaccional_allocation_soa_canete.domain.model.TransaccionalAllocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataTeenFuncionaryTransaccional {

    private FuncionaryResponseDto funcionaryResponseDto;
    private TeenResponseDto teenResponseDto;
    private TransaccionalAllocation transaccionalAllocation;

}
