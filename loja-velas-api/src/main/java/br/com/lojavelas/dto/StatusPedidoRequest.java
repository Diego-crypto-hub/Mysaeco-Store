package br.com.lojavelas.dto;

import br.com.lojavelas.model.enums.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record StatusPedidoRequest(
        @NotNull StatusPedido status
) {
}
