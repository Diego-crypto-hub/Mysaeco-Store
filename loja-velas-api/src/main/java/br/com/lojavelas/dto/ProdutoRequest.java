package br.com.lojavelas.dto;

import br.com.lojavelas.model.enums.TipoCera;
import br.com.lojavelas.model.enums.TipoPavio;
import jakarta.validation.constraints.*;

public record ProdutoRequest(
        @NotNull Integer idCategoria,
        @NotBlank @Size(max = 120) String nome,
        String descricao,
        TipoCera tipoCera,
        TipoPavio tipoPavio
) {
}
