package com.example.demo.dto;

import com.example.demo.model.Prioridade;
import com.example.demo.model.StatusTarefa;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record TarefaResponseDTO(UUID id,
                                String titulo,
                                CategoriaResponseDTO categoria,
                                Prioridade prioridade, String descricao,
                                StatusTarefa status, LocalDateTime dataDeCriacao,
                                LocalDateTime datDeVencimento,
                                Set<TarefaResponseDTO> subTarefas) {
}
