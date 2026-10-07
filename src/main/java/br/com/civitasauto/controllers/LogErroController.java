package br.com.civitasauto.controllers;

import br.com.civitasauto.DTOs.LogErroDTO;
import br.com.civitasauto.model.LogErro;
import br.com.civitasauto.services.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/log")
@Tag(name = "Relatório de Erros e Acertos", description = "Responsável por listar todos os logs de resultados das automações.")
public class LogErroController {

    @Autowired
    private LogService logService;

    @Operation(summary = "Listagem de Logs",
            description = "Responsável por listar os logs de erros e acertos do Hub de Automações.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem carregada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, tente novamente."),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN ou USER)"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping
    public ResponseEntity<Page<LogErroDTO>> listaTodos(
            @Parameter(description = "Lista todos os logs")
            LogErroDTO log,
            @PageableDefault(size = 30, sort = {"dataHoraLog"}, direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(logService.listagem(log, pageable));
    }

}
