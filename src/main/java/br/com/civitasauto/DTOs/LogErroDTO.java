package br.com.civitasauto.DTOs;

import br.com.civitasauto.enums.Automacoes;
import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.model.LogErro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record LogErroDTO(
        @Schema(description = "Id referente ao log") Long id,
        @Schema(description = "Módulo referente ao log") Modulos modulo,
        @Schema(description = "Município referente ao log") Municipios municipio,
        @Schema(description = "Status do log") StatusItem status,
        @Schema(description = "Data/hora inicial do filtro de período") LocalDateTime dataHoraLog,
        @Schema(description = "Data/hora final do filtro de período") LocalDateTime dataHoraLogFim,
        @Schema(description = "Automação") Automacoes automacoes,
        @Schema(description = "Observação referente ao log de erro") String observacao
        ) {

    public static LogErroDTO from (LogErro log){
        return new LogErroDTO(
                log.getId(),
                log.getModulo(),
                log.getMunicipio(),
                log.getStatus(),
                log.getDataHoraLog(),
                null,
                log.getAutomacoes(),
                log.getObservacao()
        );
    }
}
