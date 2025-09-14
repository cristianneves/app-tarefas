package com.example.demo.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO (@NotBlank String nome, String descricao){
}
