package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record AlterarRoleDTO(
        @Schema(description = "Recebe a role para qual o usuario vai ser mudado") String role,
        @Schema(description = "cpf do usuario afetado") String cpf
) {
}
