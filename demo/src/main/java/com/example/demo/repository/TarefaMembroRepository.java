package com.example.demo.repository;

import com.example.demo.model.TarefaMembro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TarefaMembroRepository extends JpaRepository<TarefaMembro, UUID> {
}