package com.example.demo.repository;

import com.example.demo.model.Categoria;
import com.example.demo.model.Prioridade;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {
    @EntityGraph(attributePaths = {"subTarefas", "categoria"})
    List<Tarefa> findByUsuario(Usuario usuario);
    List<Tarefa> findByUsuarioAndCategoria(Usuario usuario,Categoria categoria);
    List<Tarefa> findByUsuarioAndPrioridade(Usuario usuario,Prioridade prioridade);

    long countByUsuario(Usuario usuario);

    // Usa @Query para contar tarefas de um usuário com um status específico
    @Query("SELECT COUNT(t) FROM Tarefa t WHERE t.usuario = :usuario AND t.status = :status")
    long countByUsuarioAndStatus(@Param("usuario") Usuario usuario, @Param("status") String status);

    // Conta tarefas de um usuário que estão com a data de vencimento no passado (atrasadas)
    @Query("SELECT COUNT(t) FROM Tarefa t WHERE t.usuario = :usuario AND t.dataDeVencimento < :agora AND t.status <> 'CONCLUIDA'")
    long countTarefasAtrasadas(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);

    // Busca a próxima tarefa a vencer que ainda não foi concluída
    @Query("SELECT t FROM Tarefa t WHERE t.usuario = :usuario AND t.dataDeVencimento > :agora AND t.status <> 'CONCLUIDA' ORDER BY t.dataDeVencimento ASC LIMIT 1")
    Optional<Tarefa> findProximaTarefaAVencer(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);
}
