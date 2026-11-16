package br.com.lojavelas.dto;

import br.com.lojavelas.model.enums.StatusPagamento;
import jakarta.validation.constraints.*;

public record StatusPagamentoRequest(
        @NotNull StatusPagamento status,
        @Size(max = 100) String idTransacaoGateway
) {
}
