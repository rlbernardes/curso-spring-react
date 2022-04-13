package com.lima.minhasFinancas.service;

import com.lima.minhasFinancas.exception.ErroAutenticacao;
import com.lima.minhasFinancas.exception.RegraNegocioException;
import com.lima.minhasFinancas.helper.LancamentoTestHelper;
import com.lima.minhasFinancas.model.entity.Lancamento;
import com.lima.minhasFinancas.model.entity.Usuario;
import com.lima.minhasFinancas.model.enums.StatusLancamento;
import com.lima.minhasFinancas.model.enums.TipoLancamento;
import com.lima.minhasFinancas.model.repository.LancamentoRepository;
import com.lima.minhasFinancas.model.repository.UsuarioRepository;
import com.lima.minhasFinancas.service.impl.LancamentoServiceImpl;
import com.lima.minhasFinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class LancamentoServiceTest {

    @MockBean
    LancamentoRepository repository;

    @SpyBean
    LancamentoServiceImpl service;

    @Test
    void deveSalvarUmLancamento() {
        Lancamento lancamentoASalvar = LancamentoTestHelper.criarLancamento();
        Lancamento lancamentoSalvo = LancamentoTestHelper.criarLancamentoComId();

        Mockito.doNothing().when(service).validar(lancamentoASalvar);
        Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamento = service.salvar(lancamentoASalvar);

        assertEquals(lancamento.getId(), lancamentoSalvo.getId());
        assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
    }

    @Test
    void naoDeveSalvarUmLancamentoQuandoTiverErroDeValidacao() {
        Lancamento lancamentoASalvar = LancamentoTestHelper.criarLancamento();

        Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoASalvar);

        Executable executable = () -> service.salvar(lancamentoASalvar);

        assertThrows(RegraNegocioException.class, executable);

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
    }

    @Test
    void deveAtualizarUmLancamento() {
        Lancamento lancamentoSalvo = LancamentoTestHelper.criarLancamentoComId();

        Mockito.doNothing().when(service).validar(lancamentoSalvo);
        Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        service.atualizar(lancamentoSalvo);

        Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        Lancamento lancamentoASalvar = LancamentoTestHelper.criarLancamento();

        Executable executable = () -> service.atualizar(lancamentoASalvar);

        assertThrows(NullPointerException.class, executable);

        Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);

    }

    @Test
    void deveDeletarUmLancamento() {
        Lancamento lancamento = LancamentoTestHelper.criarLancamentoComId();

        service.deletar(lancamento);

        Mockito.verify(repository).delete(lancamento);

    }

    @Test
    void deveLancarErroAoTentarDeletarUmLancamentoSemId() {
        Lancamento lancamento = LancamentoTestHelper.criarLancamento();

        Executable executable = () -> service.deletar(lancamento);

        assertThrows(NullPointerException.class, executable);

        Mockito.verify(repository, Mockito.never()).delete(lancamento);
    }

    @Test
    void deveFiltrarLancamentos() {
        Lancamento lancamento = LancamentoTestHelper.criarLancamentoComId();

        List<Lancamento> lista = Arrays.asList(lancamento);

        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

        List<Lancamento> resultado = service.buscar(lancamento);

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.size() == 1);
        assertEquals(lista, resultado);

    }

    @Test
    void deveAtualizarStatusDeUmLancamento() {
        Lancamento lancamento = LancamentoTestHelper.criarLancamentoComId();
        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;

        Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

        service.atualizarStatus(lancamento, novoStatus);

        assertEquals(lancamento.getStatus(), novoStatus);

        Mockito.verify(service).atualizar(lancamento);
    }

    @Test
    void deveValidarDadosLancamentoELancarErrosDescricaoNullOuVazia() {
        Lancamento lancamento = new Lancamento();
        Executable executable = () -> service.validar(lancamento);
        Exception exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe uma Descrição válida.");

        lancamento.setDescricao("");
        executable = () -> service.validar(lancamento);
        exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe uma Descrição válida.");
    }
    @Test
    void deveValidarDadosLancamentoELancarErrosMesNullOuForaDePeriodoValido() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Descricao");
        Executable executable = () -> service.validar(lancamento);
        Exception exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Mês válido.");

        lancamento.setMes(0);
        executable = () -> service.validar(lancamento);
        exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Mês válido.");

        lancamento.setMes(13);
        executable = () -> service.validar(lancamento);
        exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Mês válido.");
    }

    @Test
    void deveValidarDadosLancamentoELancarErrosAnoNullOuComTamanhoInvalido() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Descricao");
        lancamento.setMes(11);
        Executable executable = () -> service.validar(lancamento);
        Exception exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Ano válido.");

        lancamento.setAno(89);
        executable = () -> service.validar(lancamento);
        exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Ano válido.");
    }

    @Test
    void deveValidarDadosLancamentoELancarErrosUsuarioNullOuIdUsuarioNull() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Descricao");
        lancamento.setMes(11);
        lancamento.setAno(1989);
        Executable executable = () -> service.validar(lancamento);
        Exception exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Usuário válido.");

        lancamento.setUsuario(new Usuario());

        executable = () -> service.validar(lancamento);
        exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um Usuário válido.");
    }

    @Test
    void deveValidarDadosLancamentoELancarErrosValorNullOuMenorDoQueZero() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Descricao");
        lancamento.setMes(11);
        lancamento.setAno(1989);
        lancamento.setUsuario(Usuario.builder().id(1l).build());
        Executable executable = () -> service.validar(lancamento);
        Exception exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um valor válido.");

        lancamento.setValor(BigDecimal.valueOf(-1));

        executable = () -> service.validar(lancamento);
        exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um valor válido.");
    }

    @Test
    void deveValidarDadosLancamentoELancarErrosTipoDeLancamentoNull() {
        Lancamento lancamento = new Lancamento();
        lancamento.setDescricao("Descricao");
        lancamento.setMes(11);
        lancamento.setAno(1989);
        lancamento.setUsuario(Usuario.builder().id(1l).build());
        lancamento.setValor(BigDecimal.valueOf(10));
        Executable executable = () -> service.validar(lancamento);
        Exception exception = assertThrows(RegraNegocioException.class, executable);
        assertEquals(exception.getMessage(), "Informe um tipo de Lançamento");
    }

    @Test
    void deveObterUmLancamentoPorId() {
        Long id = 1l;

        Lancamento lancamento = LancamentoTestHelper.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> resultado = service.obterPorId(id);

        assertTrue(resultado.isPresent());

    }

    @Test
    void deveRetornarVazioAoTentarObterUmLancamentoPorIdQueNaoExiste() {
        Long id = 1l;

        Lancamento lancamento = LancamentoTestHelper.criarLancamento();
        lancamento.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Lancamento> resultado = service.obterPorId(id);

        assertFalse(resultado.isPresent());

    }

    @Test
    void obterSaldoPorUsuario() {
        Lancamento lancamento = LancamentoTestHelper.criarLancamentoComIdEUsuario();

        Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(lancamento.getUsuario().getId(), TipoLancamento.RECEITA)).thenReturn(BigDecimal.valueOf(10));
        Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(lancamento.getUsuario().getId(), TipoLancamento.DESPESA)).thenReturn(BigDecimal.valueOf(5));

        BigDecimal saldo = service.obterSaldoPorUsuario(lancamento.getId());

        assertEquals(BigDecimal.valueOf(5), saldo);
    }
}