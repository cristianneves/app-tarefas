package com.example.demo.dto;

import com.example.demo.model.Prioridade;
import com.example.demo.model.StatusTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record TarefaRequestDTO(@NotBlank String titulo,
                               UUID categoriaId,
                               Prioridade prioridade,
                               @Size String descricao,
                               LocalDateTime dataDeVencimento,
                               UUID tarefaPaiId) {
}
