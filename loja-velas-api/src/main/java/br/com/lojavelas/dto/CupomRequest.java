package br.com.lojavelas.dto;

import br.com.lojavelas.model.enums.TipoDesconto;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CupomRequest(
        @NotBlank @Size(max = 30) String codigo,
        @NotNull TipoDesconto tipoDesconto,
        @NotNull @Positive BigDecimal valorDesconto,
        @PositiveOrZero BigDecimal valorMinimoPedido,
        @NotNull LocalDateTime dataInicio,
        @NotNull LocalDateTime dataFim,
        @Positive Integer limiteUso
) {
}
