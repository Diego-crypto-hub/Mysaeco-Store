package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record EnderecoRequest(
        @Size(max = 50) String apelido,
        @NotBlank @Pattern(regexp = "\\d{8}", message = "deve conter 8 dígitos") String cep,
        @NotBlank @Size(max = 150) String logradouro,
        @NotBlank @Size(max = 10) String numero,
        @Size(max = 100) String complemento,
        @NotBlank @Size(max = 80) String bairro,
        @NotBlank @Size(max = 80) String cidade,
        @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "deve ser a sigla do estado, ex.: RJ") String uf,
        Boolean principal
) {
}
