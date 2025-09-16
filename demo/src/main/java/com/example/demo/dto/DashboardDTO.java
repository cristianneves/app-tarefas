package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardDTO(
        long totalTarefas,
        long tarefasPendentes,
        long tarefasConcluidas,
        long tarefasAtrasadas,
        TarefaResumidaDTO tarefaMaisProximaDoVencimento) {

    public record TarefaResumidaDTO(UUID id, String titulo, LocalDateTime dataVencimento){}

}
