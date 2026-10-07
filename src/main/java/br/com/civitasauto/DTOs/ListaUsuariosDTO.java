package br.com.civitasauto.DTOs;

import br.com.civitasauto.enums.Role;
import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.model.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ListaUsuariosDTO(
        @Schema(description = "Id do usuário") Long id,
        @Schema(description = "Nome/login do usuário") String nome,
        @Schema(description = "CPF do usuário") String cpf,
        @Schema(description = "Role do usuário") Role role,
        @Schema(description = "Status atual do deferimento") StatusDeferimento statusDeferimento,
        @Schema(description = "Data e hora da solicitação de credenciamento") LocalDateTime dataSolicitacao
) {
    public static ListaUsuariosDTO from(Usuario usuario) {
        return new ListaUsuariosDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getRole(),
                usuario.getStatusDeferimento(),
                usuario.getDataCadastro()
        );
    }
}
