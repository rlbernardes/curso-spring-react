package com.lima.minhasFinancas.api.dto;

import com.lima.minhasFinancas.model.entity.Usuario;
import com.lima.minhasFinancas.model.enums.StatusLancamento;
import com.lima.minhasFinancas.model.enums.TipoLancamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.math.BigDecimal;
import java.time.LocalDate;



@Data
@Builder
public class LancamentoDTO {

    private Long id;

    private String descricao;

    private Integer mes;

    private Integer ano;

    private Usuario usuario;

    private BigDecimal valor;

    private LocalDate dataCadastro;

    private TipoLancamento tipo;

    private StatusLancamento status;

}
