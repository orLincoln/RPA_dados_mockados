package br.com.civitasauto.DTOs;

import br.com.civitasauto.enums.StatusDeferimento;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record DeferimentoDTO(
        @Schema(description = "Nome/login do usuário") String nome,
        @Schema(description = "CPF do usuário") String cpf,
        @Schema(description = "Data e hora da solicitação de credenciamento") LocalDateTime dataSolicitacao,
        @Schema(description = "Status atual do deferimento") StatusDeferimento status) {



}
