package com.example.demo.dto;

import java.util.UUID;

public record CategoriaResponseDTO(UUID id, String nome, String descricao, UUID idUsuario) {
}
