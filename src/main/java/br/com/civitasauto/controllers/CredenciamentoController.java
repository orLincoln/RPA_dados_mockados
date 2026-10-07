package br.com.civitasauto.controllers;

import br.com.civitasauto.DTOs.DadosCredenciamento;
import br.com.civitasauto.DTOs.DadosUsuarioCredenciado;
import br.com.civitasauto.services.CredenciamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/credenciamento")
@Tag(name = "Credenciamento", description = "Cadastro de novos usuários, sujeitos a deferimento manual")
public class CredenciamentoController {

    @Autowired
    private CredenciamentoService credenciamentoService;

    @Operation(summary = "Credenciar novo usuário",
            description = "Cria um usuário com status pendente de deferimento. Não requer autenticação.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Credenciamento realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos (CPF, nome ou senha)"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<String> credenciar(@RequestBody @Valid DadosCredenciamento dados) {
        credenciamentoService.credenciar(dados);
        return ResponseEntity.ok("Sucesso no credenciamento");
    }
}
