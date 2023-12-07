package com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Teen;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class MasivTeen {
    @Column("id_funcionary")
    private Integer id_funcionary;
    @Column
    private String description;

    @Column
    private LocalDate function_start;
    @Column
    private List<TeenAssignDto> teens;
    @Getter
    @Setter
    public static class TeenAssignDto {
        private Integer id_teen;
    }

}
