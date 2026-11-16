package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record EstoqueRequest(
        @NotNull @PositiveOrZero Integer qtdEstoque
) {
}
