package com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Transaccional;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class TransaccionalAllocationRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 8222253670338491507L;

    @Column
    private String description;
    @Column
    private String status;
    @Column
    private LocalDateTime date_hour_register;
    @Column
    private LocalDate function_start;
    @JsonProperty("id_teen")
    private List<Integer> idTeen;
    @Column
    private Integer id_funcionary;

    public TransaccionalAllocationRequestDto() {
    }
}
