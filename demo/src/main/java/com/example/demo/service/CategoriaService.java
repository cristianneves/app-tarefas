package com.example.demo.service;

import com.example.demo.dto.CategoriaRequestDTO;
import com.example.demo.dto.CategoriaResponseDTO;
import com.example.demo.model.Usuario;

import java.util.List;
import java.util.UUID;

public interface CategoriaService {
    CategoriaResponseDTO criarCategoria(CategoriaRequestDTO categoriaDTO, Usuario usuario);
    CategoriaResponseDTO atualizarCategoria(UUID ID, CategoriaRequestDTO categoriaDTO, Usuario usuario);
    List<CategoriaResponseDTO> listarCategorias(Usuario usuario);
    void deletarCategoria(UUID id, Usuario usuario);
}
