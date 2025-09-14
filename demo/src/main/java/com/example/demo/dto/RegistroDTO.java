package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record RegistroDTO(@NotBlank String login, @NotBlank String senha) {
}
