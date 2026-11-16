package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CheckoutRequest(
        @NotNull Integer idEndereco,
        String codigoCupom,
        @PositiveOrZero BigDecimal valorFrete,
        Boolean embalagemPresente,
        @Size(max = 255) String mensagemPresente
) {
}
