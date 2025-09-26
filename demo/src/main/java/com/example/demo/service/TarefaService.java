package com.example.demo.service;

import com.example.demo.dto.ConvidarMembroRequestDTO;
import com.example.demo.dto.DashboardDTO;
import com.example.demo.dto.TarefaRequestDTO;
import com.example.demo.dto.TarefaResponseDTO;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Usuario;

import java.util.List;
import java.util.UUID;

public interface TarefaService {
    TarefaResponseDTO criarTarefa(TarefaRequestDTO tarefaDTO, Usuario usuario);
    TarefaResponseDTO atualizarTarefa(UUID ID,TarefaRequestDTO tarefaDTO, Usuario usuario);
    List<TarefaResponseDTO> listarTarefas(Usuario usuario, Prioridade prioridade, UUID categoriaId);
    void deletarTarefa(UUID ID, Usuario usuario);

    DashboardDTO gerarDashboard(Usuario usuarioLogado);

    void convidarMembro(UUID tarefaId, ConvidarMembroRequestDTO conviteDTO, Usuario donoDaTarefa);
}
