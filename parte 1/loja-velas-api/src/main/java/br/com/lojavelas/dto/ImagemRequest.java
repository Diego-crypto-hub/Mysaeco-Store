package br.com.lojavelas.dto;

import jakarta.validation.constraints.*;

public record ImagemRequest(
        @NotBlank @Size(max = 255) String url,
        Integer idVariacao,
        @Positive Integer ordem
) {
}
