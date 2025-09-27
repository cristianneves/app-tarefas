package com.example.demo.service;

import com.example.demo.dto.ConvidarMembroRequestDTO;
import com.example.demo.dto.ConviteResponseDTO;
import com.example.demo.model.Usuario;

import java.util.List;
import java.util.UUID;

public interface ConviteService {
    void convidarMembro(UUID tarefaId, ConvidarMembroRequestDTO conviteDTO, Usuario donoDaTarefa);

    List<ConviteResponseDTO> listarConvitesPendentes(Usuario usuarioLogado);
}
