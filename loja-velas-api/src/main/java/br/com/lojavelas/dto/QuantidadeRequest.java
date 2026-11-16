package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record QuantidadeRequest(
        @NotNull @Positive Integer quantidade
) {
}
