package com.soa.canete.transaccional_allocation_soa_canete.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "funcionary_teen")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionalAllocation {

    @Id
    private Integer id_funcionaryteend;
    @Column
    private String description;
    @Column
    private String status;
    @Column("id_teen")
    private Integer idTeen;
    @Column
    private Integer id_funcionary;

    public TransaccionalAllocation(String description, String status, Integer idTeen, Integer id_funcionary) {
        this.description = description;
        this.status = status;
        this.idTeen = idTeen;
        this.id_funcionary = id_funcionary;
    }
}
