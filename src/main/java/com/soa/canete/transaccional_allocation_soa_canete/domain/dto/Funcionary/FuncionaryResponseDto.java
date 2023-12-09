package com.soa.canete.transaccional_allocation_soa_canete.domain.dto.Funcionary;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class FuncionaryResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 8222253670338491507L;

    @Id
    private Integer id_funcionary;
    @Column
    private String name;
    @Column("surnamefather")
    private String surnameFather;
    @Column("surnamemother")
    private String surnameMother;
    @Column
    private String dni;
    @Column("phonenumber")
    private String phoneNumber;
    @Column
    private String range;
    @Column
    private String confirmation;
    @Column
    private String address;
    @Column
    private String email;
    @Column
    private String codubi;
    @Column
    private Integer id_operativeunit;
    @Column
    private String status;
}
