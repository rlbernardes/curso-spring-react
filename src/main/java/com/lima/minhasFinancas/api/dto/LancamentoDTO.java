package com.lima.minhasFinancas.api.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;



@Data
@Builder
public class LancamentoDTO {

    private Long id;

    private String descricao;

    private Integer mes;

    private Integer ano;

    private Long usuario;

    private BigDecimal valor;

    private LocalDate dataCadastro;

    private String tipo;

    private String status;

}
