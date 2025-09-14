package com.example.demo.dto;

import com.example.demo.model.Prioridade;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TarefaResponseDTO(UUID id,
                                String titulo,
                                CategoriaResponseDTO categoria,
                                Prioridade prioridade, String descricao,
                                String status, LocalDateTime dataDeCriacao,
                                LocalDateTime datDeVencimento,
                                Set<TarefaResponseDTO> subTarefas) {
}
