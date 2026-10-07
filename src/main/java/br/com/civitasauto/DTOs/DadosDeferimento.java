package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DadosDeferimento(
        @Schema(description = "CPF do usuário a ser deferido", example = "12345678900") @NotBlank String cpf
) {
}
