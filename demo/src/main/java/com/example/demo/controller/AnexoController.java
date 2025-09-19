package com.example.demo.controller;

import com.example.demo.dto.AnexoDownloadDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.AnexoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/anexos")
public class AnexoController {

    private final AnexoService anexoService;

    public AnexoController(AnexoService anexoService) {
        this.anexoService = anexoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> baixarAnexo(@PathVariable UUID id, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();

        AnexoDownloadDTO anexoDTO = anexoService.baixarArquivo(id, usuarioLogado);
        Resource recurso = anexoDTO.recurso();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + anexoDTO.nomeOriginal() + "\"")
                .body(recurso);
    }
}