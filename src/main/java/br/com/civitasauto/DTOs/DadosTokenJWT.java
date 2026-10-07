package br.com.civitasauto.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;

public record DadosTokenJWT(@Schema(description = "Token JWT a ser enviado no header Authorization: Bearer <token>") String token) {
}
