package br.com.civitasauto.DTOs;

import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;

public record DadosProcessamento(Municipios municipio, Modulos modulo) {
}
