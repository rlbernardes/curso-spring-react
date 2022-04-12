package com.lima.minhasFinancas.model.repository;

import com.lima.minhasFinancas.model.entity.Usuario;
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

@ExtendWith (SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//Não sobreescre as configurações do arquivo application-test.properties
class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveVerificarAExistenciaDeUmEmail(){
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        boolean result = repository.existsByEmail("usuario@email.com");
        assertTrue(result);
    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail(){
        boolean result = repository.existsByEmail("usuario@email.com");
        assertFalse(result);
    }

    @Test
    public void devePersistirUsuarioNaBaseDeDados(){
        Usuario usuario = criarUsuario();

        Usuario usuarioSalvo = repository.save(usuario);

        assertNotNull(usuarioSalvo.getId());
    }

    @Test
    public void deveBuscarOUsuarioPorEmail(){
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        assertTrue(result.isPresent());
    }

    @Test
    public void deveRetornarVazioAoBuscarOUsuarioPorEmailQuandoNaoExistir(){
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        Optional<Usuario> result = repository.findByEmail("usuari@email.com");

        assertFalse(result.isPresent());
    }

    public static Usuario criarUsuario(){
        return Usuario
                .builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();
    }

}