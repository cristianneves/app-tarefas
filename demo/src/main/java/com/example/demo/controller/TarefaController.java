package com.example.demo.controller;

import com.example.demo.dto.DashboardDTO;
import com.example.demo.dto.TarefaRequestDTO;
import com.example.demo.dto.TarefaResponseDTO;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Usuario;
import com.example.demo.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    public TarefaController(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
    }


    @GetMapping
    public ResponseEntity<List<TarefaResponseDTO>> listarTarefas(Authentication authentication,
                                                                 @RequestParam(required = false) Prioridade prioridade,
                                                                 @RequestParam(required = false) UUID categoriaId){
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<TarefaResponseDTO> tarefas = this.tarefaService.listarTarefas(usuarioLogado,prioridade,categoriaId);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> verDashboard(Authentication authentication){
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        DashboardDTO dashboard = this.tarefaService.gerarDashboard(usuarioLogado);
        return ResponseEntity.ok(dashboard);
    }

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criarTarefa(@Valid @RequestBody TarefaRequestDTO tarefaDTO, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        TarefaResponseDTO tarefaSalva = tarefaService.criarTarefa(tarefaDTO, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizarTarefa(@Valid @PathVariable UUID id, @RequestBody TarefaRequestDTO tarefaDTO, Authentication authentication){
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        TarefaResponseDTO tarefaAtualizada = tarefaService.atualizarTarefa(id, tarefaDTO, usuarioLogado);
        return ResponseEntity.ok(tarefaAtualizada);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarTarefa(@PathVariable UUID id, Authentication authentication){
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        tarefaService.deletarTarefa(id,usuarioLogado);
        return ResponseEntity.noContent().build();
    }

}
