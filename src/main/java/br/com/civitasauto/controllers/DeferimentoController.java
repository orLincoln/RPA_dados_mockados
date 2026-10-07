package br.com.civitasauto.controllers;

import br.com.civitasauto.DTOs.DadosDeferimento;
import br.com.civitasauto.DTOs.DeferimentoDTO;
import br.com.civitasauto.enums.ResultadoDeferimento;
import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.services.DeferimentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/deferimento")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Deferimento", description = "Listagem e deferimento manual de usuários credenciados (requer ADMIN)")
public class DeferimentoController {

    @Autowired
    DeferimentoService service;

//    @Operation(summary = "Listar usuários credenciados",
//            description = "Lista, de forma paginada, os usuários credenciados. Pode ser filtrada por status de deferimento.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Página de usuários retornada"),
//            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
//            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN)"),
//            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
//    })
//    @GetMapping
//    public ResponseEntity<Page<DeferimentoDTO>> listar(
//            @Parameter(description = "Filtra pelo status do deferimento (opcional)") @RequestParam(required = false) StatusDeferimento status,
//            Pageable pageable) {
//        return ResponseEntity.ok(service.listar(status, pageable));
//    }

    @Operation(summary = "Deferir usuário",
            description = "Defere manualmente o usuário com o CPF informado, liberando seu acesso.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deferimento realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado para o CPF informado"),
            @ApiResponse(responseCode = "409", description = "Usuário já está deferido"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> deferir(@RequestBody @Valid DadosDeferimento dados) {
        ResultadoDeferimento resultado = service.deferir(dados.cpf());
        return switch (resultado) {
            case SUCESSO -> ResponseEntity.ok(
                    Map.of("status", 200, "mensagem", "Deferimento realizado com sucesso."));

            case USUARIO_NAO_ENCONTRADO -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", 404, "mensagem", "Usuário não encontrado para o CPF informado."));

            case JA_DEFERIDO -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", 409, "mensagem", "Usuário já está deferido."));
        };
    }
}
