package com.lima.minhasFinancas.api.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.minhasFinancas.api.dto.UsuarioDTO;
import com.lima.minhasFinancas.exception.ErroAutenticacao;
import com.lima.minhasFinancas.exception.RegraNegocioException;
import com.lima.minhasFinancas.helper.UsuarioTestHelper;
import com.lima.minhasFinancas.model.entity.Usuario;
import com.lima.minhasFinancas.service.LancamentoService;
import com.lima.minhasFinancas.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
class UsuarioControllerTest {

    static final String API = "/api/usuarios";
    static final MediaType JSON_MEDIA_TYPE = MediaType.APPLICATION_JSON;
    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    void deveAutenticar() throws Exception {
        UsuarioDTO dto = UsuarioTestHelper.criarUsuarioDto();
        Usuario usuario = UsuarioTestHelper.criarUsuario();

        Mockito.when(service.autenticar(UsuarioTestHelper.EMAIL, UsuarioTestHelper.SENHA)).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON_MEDIA_TYPE)
                .contentType(JSON_MEDIA_TYPE)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
        UsuarioDTO dto = UsuarioTestHelper.criarUsuarioDto();

        Mockito.when(service.autenticar(UsuarioTestHelper.EMAIL, UsuarioTestHelper.SENHA)).thenThrow(ErroAutenticacao.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON_MEDIA_TYPE)
                .contentType(JSON_MEDIA_TYPE)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveCriarUmNovoUsuario() throws Exception {
        UsuarioDTO dto = UsuarioTestHelper.criarUsuarioDto();
        Usuario usuario = UsuarioTestHelper.criarUsuario();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON_MEDIA_TYPE)
                .contentType(JSON_MEDIA_TYPE)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
    }

    @Test
    void deveRetornarBadRequestAoTentarCriarUmNovoUsuarioInvalido() throws Exception {
        UsuarioDTO dto = UsuarioTestHelper.criarUsuarioDto();
        Usuario usuario = UsuarioTestHelper.criarUsuario();

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON_MEDIA_TYPE)
                .contentType(JSON_MEDIA_TYPE)
                .content(json);

        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deveObterSaldo() {
    }
}