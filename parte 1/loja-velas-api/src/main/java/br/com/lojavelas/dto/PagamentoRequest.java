package br.com.lojavelas.dto;

import br.com.lojavelas.model.enums.FormaPagamento;
import jakarta.validation.constraints.*;

public record PagamentoRequest(
        @NotNull FormaPagamento formaPagamento,
        @Min(1) @Max(12) Integer parcelas
) {
}
