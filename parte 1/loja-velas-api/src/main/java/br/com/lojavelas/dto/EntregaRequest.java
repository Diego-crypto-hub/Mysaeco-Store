package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record EntregaRequest(
        @NotBlank @Size(max = 60) String transportadora,
        @Size(max = 30) String modalidade,
        @Size(max = 50) String codigoRastreio,
        @Positive Integer prazoDias
) {
}
