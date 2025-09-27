package com.example.demo.controller;

import com.example.demo.dto.ConvidarMembroRequestDTO;
import com.example.demo.dto.ConviteResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.ConviteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/convite")
public class ConviteController {

    private final ConviteService conviteService;

    public ConviteController(ConviteService conviteService) {
        this.conviteService = conviteService;
    }

    @PostMapping("/{tarefaId}")
    @Transactional
    public ResponseEntity<Void> convidarMembro(
            @PathVariable UUID tarefaId,
            @RequestBody @Valid ConvidarMembroRequestDTO conviteDTO,
            Authentication authentication
    ) {
        Usuario donoDaTarefa = (Usuario) authentication.getPrincipal();
        this.conviteService.convidarMembro(tarefaId, conviteDTO, donoDaTarefa);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ConviteResponseDTO>> listarConvitesPendentes(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        List<ConviteResponseDTO> convites = this.conviteService.listarConvitesPendentes(usuarioLogado);

        return ResponseEntity.ok(convites);
    }
}
