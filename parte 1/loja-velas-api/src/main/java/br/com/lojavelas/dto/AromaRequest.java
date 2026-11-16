package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record AromaRequest(
        @NotBlank @Size(max = 60) String nome,
        @Size(max = 40) String familiaOlfativa
) {
}
