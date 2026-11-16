package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record ItemCarrinhoRequest(
        @NotNull Integer idVariacao,
        @NotNull @Positive Integer quantidade
) {
}
