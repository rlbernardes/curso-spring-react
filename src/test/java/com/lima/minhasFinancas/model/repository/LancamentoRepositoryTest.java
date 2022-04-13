package com.lima.minhasFinancas.model.repository;

import com.lima.minhasFinancas.helper.LancamentoTestHelper;
import com.lima.minhasFinancas.model.entity.Lancamento;
import com.lima.minhasFinancas.model.enums.StatusLancamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//Não sobreescre as configurações do arquivo application-test.properties
class LancamentoRepositoryTest {
    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    void deveSalvarUmLancamento(){
        Lancamento lancamento = LancamentoTestHelper.criarLancamento();

        lancamento = repository.save(lancamento);

        assertNotNull(lancamento.getId());
    }

    @Test
    void deveDeletarUmLancamento(){
        Lancamento lancamento = LancamentoTestHelper.criarLancamento();
        entityManager.persist(lancamento);

        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        repository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        assertNull(lancamentoInexistente);

    }

    @Test
    void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarEPercistirUmLancamento();

        lancamento.setAno(2018);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        repository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        assertEquals(2018, lancamentoAtualizado.getAno());
        assertEquals("Teste Atualizar", lancamentoAtualizado.getDescricao());
        assertEquals(StatusLancamento.CANCELADO, lancamentoAtualizado.getStatus());

    }

    @Test
    void deveBuscarUmLancamentoPorId(){
        Lancamento lancamento = criarEPercistirUmLancamento();
        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        assertTrue(lancamentoEncontrado.isPresent());
    }


    private Lancamento criarEPercistirUmLancamento(){
        Lancamento lancamento = LancamentoTestHelper.criarLancamento();
        return entityManager.persist(lancamento);
    }
}