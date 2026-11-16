package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record CategoriaRequest(
        @NotBlank @Size(max = 60) String nome,
        String descricao,
        Integer idCategoriaPai
) {
}
