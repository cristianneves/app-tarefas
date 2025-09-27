package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"tarefaPai", "subTarefas"})
@ToString(exclude = {"tarefaPai", "subTarefas"})
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "O título é obrigatório.")
    private String titulo;

    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    private LocalDateTime dataDeCriacao;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private LocalDateTime dataDeVencimento;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "tarefa_pai_id")
    @JsonBackReference
    private Tarefa tarefaPai;

    @OneToMany(mappedBy = "tarefaPai", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Tarefa> subTarefas = new HashSet<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL)
    private Set<Anexo> anexos = new HashSet<>();

    @OneToMany(mappedBy = "tarefa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TarefaMembro> membros = new HashSet<>();

    @PrePersist
    public void definirDataDeCriacao(){
        this.dataDeCriacao = LocalDateTime.now();
    }

}
