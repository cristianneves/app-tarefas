package com.example.demo.service;

import com.example.demo.dto.AnexoDownloadDTO;
import com.example.demo.dto.AnexoResponseDTO;
import com.example.demo.model.Usuario;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AnexoService {
    AnexoResponseDTO anexarArquivo(MultipartFile arquivo, UUID tarefaId, Usuario usuario);
    AnexoDownloadDTO baixarArquivo(UUID idDoAnexo, Usuario usuario);
}
