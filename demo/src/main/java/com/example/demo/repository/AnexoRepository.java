package com.example.demo.repository;

import com.example.demo.model.Anexo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnexoRepository extends JpaRepository<Anexo, UUID> {
}
