package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteUpdateRequest(
        @NotBlank @Size(max = 100) String nome,
        @Size(max = 20) String telefone,
        @Past LocalDate dataNascimento
) {
}
