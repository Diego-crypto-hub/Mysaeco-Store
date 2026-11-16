package br.com.lojavelas.dto;

import br.com.lojavelas.model.enums.Tamanho;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record VariacaoRequest(
        Integer idAroma,
        @NotBlank @Size(max = 30) String sku,
        Tamanho tamanho,
        @Positive Integer pesoGramas,
        @Size(max = 30) String cor,
        @Positive Integer tempoQueimaHoras,
        @NotNull @Positive BigDecimal preco,
        @Positive BigDecimal precoPromocional,
        @NotNull @PositiveOrZero Integer qtdEstoque
) {
}
