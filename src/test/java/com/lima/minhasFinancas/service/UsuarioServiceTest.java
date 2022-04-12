package com.lima.minhasFinancas.service;

import com.lima.minhasFinancas.exception.ErroAutenticacao;
import com.lima.minhasFinancas.exception.RegraNegocioException;
import com.lima.minhasFinancas.model.entity.Usuario;
import com.lima.minhasFinancas.model.repository.UsuarioRepository;
import com.lima.minhasFinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class UsuarioServiceTest {

    String email = "email@email.com";
    String senha = "senha";

    @MockBean
    UsuarioRepository repository;

    @SpyBean
    UsuarioServiceImpl service;

    /*
    UsuarioService service;

    @BeforeEach
    public void setUp(){
        service = Mockito.spy(UsuarioServiceImpl.class);

//        service = new UsuarioServiceImpl(repository);
    }*/

    @Test
    public void deveAutenticarUmUsuarioComSucesso(){
        Usuario usuario = Usuario.builder().id(1l).nome("nome").email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario result = service.autenticar(email, senha);

        assertNotNull(result);
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComEmailInformado(){
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Executable executable = () -> service.autenticar(email, senha);
        Exception exception = assertThrows(ErroAutenticacao.class, executable);

        assertEquals(exception.getMessage(), "Usuário não encontrado para o email informado.");
    }

    @Test
    public void deveLancarErroQuandoASenhaNaoBater(){
        Usuario usuario = Usuario.builder().id(1l).nome("nome").email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        Executable executable = () -> service.autenticar(email, "123");
        Exception exception = assertThrows(ErroAutenticacao.class, executable);

        assertEquals(exception.getMessage(), "Senha iválida.");

    }

    @Test
    public void deveValidarEmailComSucesso(){
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        Executable executable = () -> service.validarEmail("teste@email.com.br");
        Assertions.assertDoesNotThrow(executable);
    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado(){
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Executable executable = () -> service.validarEmail("usuario@email.com");
        Exception exception = Assertions.assertThrows(RegraNegocioException.class, executable);

        assertEquals(exception.getMessage(), "Já existe um usuáiro cadastrado com este email.");
    }

    @Test
    public void deveSalvarUmUsuario(){
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder().id(1l).nome("nome").email(email).senha(senha).build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        assertNotNull(usuarioSalvo);
        assertEquals(usuarioSalvo.getId(), 1l);
        assertEquals(usuarioSalvo.getNome(), "nome");
        assertEquals(usuarioSalvo.getEmail(), email);
        assertEquals(usuarioSalvo.getSenha(), senha);
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado(){
        Usuario usuario = Usuario.builder().email(email).build();

        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        Executable executable = () -> service.salvarUsuario(usuario);
        Assertions.assertThrows(RegraNegocioException.class, executable);

        Mockito.verify(repository, Mockito.never()).save(usuario);

    }
}