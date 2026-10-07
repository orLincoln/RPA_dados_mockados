package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record RespostaStatusAtribuicao(
        @Schema(description = "Status agregado do lote (ex: PROCESSANDO, CONCLUIDO)") String status,
        @Schema(description = "Mensagem adicional sobre o lote, quando aplicável") String mensagem,
        @Schema(description = "Resultado individual de cada combinação de município x módulo processada") List<RespostaItemAtribuicao> resultado) {
}
