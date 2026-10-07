package br.com.civitasauto.controllers;

import br.com.civitasauto.DTOs.AlterarRoleDTO;
import br.com.civitasauto.services.AlterarRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mudarole")
@Tag(name = "Alterar Role de Usuário", description = "Realiza a alteração da Role do usuário escolhido para ADMIN.")
public class AlterarRoleController {

    @Autowired
    AlterarRoleService service;

    @Operation(summary = "Dispara Service de Alteração de Role",
            description = "Realiza o mapeamento de status http, garante verificação se usuário já for ADMIN e realiza a mudança via repository.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ALteração de Role realizada com sucesso."),
            @ApiResponse(responseCode = "400", description = "Não foi possível realizar a alteração, tente novamente."),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN)"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Map<String, Object>> alterar(@RequestBody @Valid AlterarRoleDTO dados){
        return service.alterar(dados);
    }
}
