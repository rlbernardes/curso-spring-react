package com.lima.minhasFinancas.helper;

import com.lima.minhasFinancas.api.dto.UsuarioDTO;
import com.lima.minhasFinancas.model.entity.Usuario;

public class UsuarioTestHelper {
    public static String EMAIL = "usuario@email.com";
    public static String SENHA = "123";
    public static String NOME = "usuario";

    public static Usuario criarUsuario(){
        return Usuario.builder()
                .nome(NOME)
                .email(EMAIL)
                .senha(SENHA)
                .build();
    }

    public static UsuarioDTO criarUsuarioDto(){
        return UsuarioDTO.builder()
                .nome(NOME)
                .email(EMAIL)
                .senha(SENHA)
                .build();
    }
}
