package com.example.demo.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"tarefa", "membro"}) //evita loops
public class TarefaMembro {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "tarefa_id",  nullable = false)
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "usuario_id",  nullable = false)
    private Usuario membro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Permissao permissao;
}
