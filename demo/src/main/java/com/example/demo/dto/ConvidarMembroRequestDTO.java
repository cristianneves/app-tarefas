// Em /dto/ConvidarMembroRequestDTO.java
package com.example.demo.dto;

import com.example.demo.model.Permissao;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConvidarMembroRequestDTO(
        @NotBlank @Email String email,
        @NotNull Permissao permissao
) {}