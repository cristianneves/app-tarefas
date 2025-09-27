package com.example.demo.dto;

import org.springframework.core.io.Resource;

public record AnexoDownloadDTO(String nomeOriginal, Resource recurso) {
}