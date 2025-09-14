package com.example.demo.controller;

import com.example.demo.dto.CategoriaRequestDTO;
import com.example.demo.dto.CategoriaResponseDTO;
import com.example.demo.model.Usuario;
import com.example.demo.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController (CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> minhasCategorias(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<CategoriaResponseDTO> categorias = this.categoriaService.listarCategorias(usuarioLogado);
        return ResponseEntity.ok(categorias);
    }

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criarCategoria(@Valid @RequestBody CategoriaRequestDTO categoriaDTO, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        CategoriaResponseDTO categoriaSalva = categoriaService.criarCategoria(categoriaDTO, usuarioLogado);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaResponseDTO> atualizarCategoria(@Valid @PathVariable UUID id, @RequestBody CategoriaRequestDTO categoriaDTO, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        CategoriaResponseDTO categoriaAtualizada = this.categoriaService.atualizarCategoria(id,  categoriaDTO, usuarioLogado);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable UUID id, Authentication authentication){
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        this.categoriaService.deletarCategoria(id,usuarioLogado);
        return ResponseEntity.noContent().build();
    }

}
