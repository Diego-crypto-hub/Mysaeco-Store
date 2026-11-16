package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ClienteRequest(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Size(min = 6, max = 72) String senha,
        @Pattern(regexp = "\\d{11}", message = "deve conter 11 dígitos, sem pontos ou traço") String cpf,
        @Size(max = 20) String telefone,
        @Past LocalDate dataNascimento
) {
}
