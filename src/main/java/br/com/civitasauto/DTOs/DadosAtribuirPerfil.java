package br.com.civitasauto.DTOs;

import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DadosAtribuirPerfil(
        @Schema(description = "CPF do usuário que terá o perfil atribuído", example = "12345678900") @NotBlank String cpf,
        @Schema(description = "Municípios para os quais o perfil será atribuído") @NotNull List<Municipios> municipios,
        @Schema(description = "Módulos do sistema para os quais o perfil será atribuído") @NotNull List<Modulos> modulos
) {
}
