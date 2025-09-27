package com.example.demo.repository;

import com.example.demo.model.Convite;
import com.example.demo.model.StatusConvite;
import com.example.demo.model.Tarefa;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConviteRepository extends JpaRepository<Convite, UUID> {
    boolean existsByTarefaAndDestinatarioAndStatus(Tarefa tarefa, Usuario destinatario, StatusConvite status);
    List<Convite> findByDestinatarioAndStatus(Usuario destinatario, StatusConvite status);
}
