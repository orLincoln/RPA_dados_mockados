package br.com.civitasauto.controllers;

import br.com.civitasauto.DTOs.DadosAtribuirPerfil;
import br.com.civitasauto.DTOs.RespostaStatusAtribuicao;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.services.AtribuicaoExecucaoService;
import br.com.civitasauto.services.AtribuicaoPerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/home")
@Tag(name = "Atribuição de Perfil", description = "Atribuição assíncrona de perfil por município/módulo e consulta de status do lote")
public class AtribuirPerfilController {

    @Autowired
    private AtribuicaoPerfilService service;

    @Autowired
    private AtribuicaoExecucaoService execucaoService;

    @Operation(summary = "Disparar atribuição de perfil",
            description = "Enfileira a automação de atribuição de perfil para as combinações de município x módulo " +
                    "informadas e retorna imediatamente o id do lote (jobId). O processamento roda em background; " +
                    "use o endpoint de status para acompanhar o resultado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lote aceito, jobId retornado"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN ou USER)"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/atribuirperfil")
    public ResponseEntity<Map<String, String>> post(@RequestBody DadosAtribuirPerfil dados, Authentication authentication) {
        Usuario usuarioExecutor = (Usuario) authentication.getPrincipal();

        Long jobId = service.atribuicaoPerfil(dados, usuarioExecutor);
        execucaoService.executar(jobId);

        return ResponseEntity.ok(Map.of("jobId", String.valueOf(jobId)));
    }

    @Operation(summary = "Consultar status do lote de atribuição",
            description = "Retorna o status agregado do lote e o resultado individual de cada combinação de município x módulo processada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status do lote retornado"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN ou USER)"),
            @ApiResponse(responseCode = "500", description = "Lote não encontrado para o jobId informado (exceção não mapeada)")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/atribuirperfil/{jobId}/status")
    public ResponseEntity<RespostaStatusAtribuicao> status(
            @Parameter(description = "Id do lote retornado pelo POST /home/atribuirperfil") @PathVariable Long jobId) {
        return ResponseEntity.ok(service.consultarStatus(jobId));
    }
}
