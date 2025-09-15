package com.example.demo.repository;

import com.example.demo.model.Categoria;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {
    @EntityGraph(attributePaths = {"subTarefas", "categoria"})
    List<Tarefa> findByUsuario(Usuario usuario);
    List<Tarefa> findByUsuarioAndCategoria(Usuario usuario,Categoria categoria);
    List<Tarefa> findByUsuarioAndPrioridade(Usuario usuario,Prioridade prioridade);
}
