package com.example.demo.dto;

import com.example.demo.model.Permissao;
import com.example.demo.model.StatusConvite;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConviteResponseDTO(
        UUID id,
        TarefaInfoDTO tarefa,
        UsuarioInfoDTO remetente,
        Permissao permissao,
        StatusConvite status,
        LocalDateTime dataEnvio
) {
    // DTO aninhado para fornecer apenas as informações essenciais da tarefa
    public record TarefaInfoDTO(
            UUID id,
            String titulo
    ) {}

    // DTO aninhado para fornecer apenas as informações públicas do remetente
    public record UsuarioInfoDTO(
            UUID id,
            String login
    ) {}
}