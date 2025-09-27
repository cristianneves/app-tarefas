package com.example.demo.repository;

import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarefaRepository extends JpaRepository<Tarefa, UUID> {

    @Query("SELECT t FROM Tarefa t LEFT JOIN t.membros m WHERE t.usuario = :usuario OR m.membro = :usuario")
    List<Tarefa> findTarefasByUsuarioOrMembro(@Param("usuario") Usuario usuario);

    @Query("SELECT COUNT(t) FROM Tarefa t LEFT JOIN t.membros m WHERE t.usuario = :usuario OR m.membro = :usuario")
    long countTotalTarefasByUsuarioOrMembro(@Param("usuario") Usuario usuario);

    @Query("SELECT COUNT(t) FROM Tarefa t LEFT JOIN t.membros m WHERE (t.usuario = :usuario OR m.membro = :usuario) AND t.status = :status")
    long countTarefasByStatusForUsuarioOrMembro(@Param("usuario") Usuario usuario, @Param("status") String status);

    @Query("SELECT COUNT(t) FROM Tarefa t LEFT JOIN t.membros m WHERE (t.usuario = :usuario OR m.membro = :usuario) AND t.dataDeVencimento < :agora AND t.status <> 'CONCLUIDA'")
    long countTarefasAtrasadasForUsuarioOrMembro(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);

    @Query("SELECT t FROM Tarefa t LEFT JOIN t.membros m WHERE (t.usuario = :usuario OR m.membro = :usuario) AND t.dataDeVencimento > :agora AND t.status <> 'CONCLUIDA' ORDER BY t.dataDeVencimento ASC LIMIT 1")
    Optional<Tarefa> findProximaTarefaAVencerForUsuarioOrMembro(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);
}
