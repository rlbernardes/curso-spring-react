package com.lima.minhasFinancas.api.resource;

import com.lima.minhasFinancas.api.dto.AtualizaStatusDTO;
import com.lima.minhasFinancas.api.dto.LancamentoDTO;
import com.lima.minhasFinancas.exception.RegraNegocioException;
import com.lima.minhasFinancas.model.entity.Lancamento;
import com.lima.minhasFinancas.model.entity.Usuario;
import com.lima.minhasFinancas.model.enums.StatusLancamento;
import com.lima.minhasFinancas.model.enums.TipoLancamento;
import com.lima.minhasFinancas.service.LancamentoService;
import com.lima.minhasFinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService service;
    private final UsuarioService usuarioService;

    /*public LancamentoController(LancamentoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }*/

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto){
        try{
            Lancamento entidade = lancamentoFromDto(dto);
            entidade = service.salvar(entidade);
            return new ResponseEntity(entidade, HttpStatus.CREATED);

        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto){
       return service.obterPorId(id).map(entity -> {
           try{
               Lancamento lancamento = lancamentoFromDto(dto);
               lancamento.setId(entity.getId());
               service.atualizar(lancamento);
               return ResponseEntity.ok(lancamento);
           }catch (RegraNegocioException e){
               return ResponseEntity.badRequest().body(e.getMessage());
           }
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
    }

    @PutMapping("{id}/atualiza-status")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDTO dto){
        return service.obterPorId(id).map(entity -> {
            StatusLancamento statusLancamentoSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if(statusLancamentoSelecionado  == null){
                return ResponseEntity.badRequest().body("Não foi possível atualizar o status do lançamento, envie um status válido.");
            }
            try{
                service.atualizarStatus(entity, statusLancamentoSelecionado);
                return ResponseEntity.ok(entity);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return service.obterPorId(id).map(entity -> {
            service.deletar(entity);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lançamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
    }

    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
            ){
        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);

        if(!usuario.isPresent()) {
                return ResponseEntity.badRequest().body("Usuário não encontrado para o Id informado.");
        }

        Lancamento lancamentoFiltro = Lancamento.builder()
                .descricao(descricao)
                .mes(mes)
                .ano(ano)
                .usuario(usuario.get())
                .build();
        List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);

        return ResponseEntity.ok(lancamentos);

    }

    private Lancamento lancamentoFromDto(LancamentoDTO dto) {
        Usuario usuario = usuarioService
                .obterPorId(dto.getUsuario())
                .orElseThrow(() -> new RegraNegocioException("Usuário não encontrado para o Id informado."));
        return Lancamento.builder()
                .id(dto.getId())
                .ano(dto.getAno())
                .mes(dto.getMes())
                .tipo(dto.getTipo() != null ? TipoLancamento.valueOf(dto.getTipo()) : null)
                .status(dto.getStatus() != null ? StatusLancamento.valueOf(dto.getStatus()) : null)
                .descricao(dto.getDescricao())
                .valor(dto.getValor())
                .usuario(usuario)
                .build();
    }

}
