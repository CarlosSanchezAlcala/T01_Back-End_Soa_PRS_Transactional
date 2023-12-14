package com.soa.canete.transaccional_allocation_soa_canete.domain.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignationProgramsReportDto {
    private Integer id;
    private String name_teen;
    private String name_funcionary;
    private LocalDate date_funciont;
    private String description;
}
