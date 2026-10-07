package br.com.civitasauto.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Login e emissão de token JWT")
public class AuthValidateController {

    @Operation(summary = "Validar token JWT",
            description = "Responsável por validar navegação do usuário no front-end: Retorna 200 se o Bearer token informado for válido e o usuário estiver deferido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @GetMapping("/validate")
    public ResponseEntity<Void> validate() {
        return ResponseEntity.ok().build();
    }
}
