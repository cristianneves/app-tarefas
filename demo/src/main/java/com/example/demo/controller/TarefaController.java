package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Usuario;
import com.example.demo.service.AnexoService;
import com.example.demo.service.ConviteService;
import com.example.demo.service.TarefaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tarefas")
public class TarefaController {

    private final TarefaService tarefaService;
    private final AnexoService anexoService;

    public TarefaController(TarefaService tarefaService, AnexoService anexoService) {
        this.tarefaService = tarefaService;
        this.anexoService = anexoService;
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

    @PostMapping("/{idDaTarefa}/anexos")
    public ResponseEntity<?> uploadAnexo(
            @PathVariable UUID idDaTarefa,
            @RequestParam("arquivo") MultipartFile arquivo,
            Authentication authentication
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        AnexoResponseDTO anexoSalvo = anexoService.anexarArquivo(arquivo, idDaTarefa, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(anexoSalvo);
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

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Void> iniciarTarefa(@PathVariable UUID id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        this.tarefaService.iniciarTarefa(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/concluir")
    public ResponseEntity<Void> concluirTarefa(@PathVariable UUID id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        this.tarefaService.concluirTarefa(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }

}
