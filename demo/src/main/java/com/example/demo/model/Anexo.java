package com.example.demo.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"tarefa", "usuario"})
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String nomeArquivo;
    String tipoArquivo;
    String caminhoArquivo;
    LocalDateTime dataUpload;

    @ManyToOne
    @JoinColumn(name = "tarefa_id", nullable = false)
    Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @PrePersist
    public void prePersist() {
        this.dataUpload = LocalDateTime.now();
    }
}
