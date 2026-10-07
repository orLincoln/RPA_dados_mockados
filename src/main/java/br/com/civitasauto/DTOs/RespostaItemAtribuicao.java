package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record RespostaItemAtribuicao(
        @Schema(description = "Município processado") String municipio,
        @Schema(description = "Módulo processado") String modulo,
        @Schema(description = "Status do item (ex: SUCESSO, ERRO)") String status,
        @Schema(description = "Mensagem detalhando o resultado do item") String mensagem) {

}
