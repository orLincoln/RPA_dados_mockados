package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DadosAutenticacao(
        @Schema(description = "CPF do usuário, com ou sem máscara", example = "12345678900") @NotBlank String cpf,
        @Schema(description = "Senha do usuário") @NotBlank String senha
) {
}
