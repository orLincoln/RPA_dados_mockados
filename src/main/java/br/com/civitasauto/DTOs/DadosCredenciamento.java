package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record DadosCredenciamento(
        @Schema(description = "CPF válido do usuário", example = "12345678900") @NotBlank @CPF(message = "CPF inválido") String cpf,
        @Schema(description = "Nome/login do usuário") @NotBlank String nome,
        @Schema(description = "Senha do usuário, mínimo 6 caracteres") @NotBlank @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String senha
) {

    public String getLogin() {
        return this.nome();
    }

}
