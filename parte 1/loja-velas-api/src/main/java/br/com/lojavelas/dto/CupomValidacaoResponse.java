package br.com.lojavelas.dto;

import java.math.BigDecimal;

public record CupomValidacaoResponse(
        String codigo,
        BigDecimal subtotal,
        BigDecimal desconto,
        BigDecimal totalComDesconto
) {
}
