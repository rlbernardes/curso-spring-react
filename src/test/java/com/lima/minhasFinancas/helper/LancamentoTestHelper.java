package com.lima.minhasFinancas.helper;

import com.lima.minhasFinancas.model.entity.Lancamento;
import com.lima.minhasFinancas.model.entity.Usuario;
import com.lima.minhasFinancas.model.enums.StatusLancamento;
import com.lima.minhasFinancas.model.enums.TipoLancamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoTestHelper {

    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

    public static Lancamento criarLancamentoComId(){
        return Lancamento.builder()
                .id(1l)
                .ano(2019)
                .mes(1)
                .descricao("lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }

    public static Lancamento criarLancamentoComIdEUsuario(){
        return Lancamento.builder()
                .id(1l)
                .ano(2019)
                .mes(1)
                .descricao("lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .usuario(Usuario.builder().id(1l).build())
                .build();
    }

}
