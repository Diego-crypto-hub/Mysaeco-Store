package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record AvaliacaoRequest(
        @NotNull Integer idCliente,
        @NotNull @Min(1) @Max(5) Integer nota,
        @Size(max = 2000) String comentario
) {
}
