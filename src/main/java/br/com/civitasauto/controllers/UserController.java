package br.com.civitasauto.controllers;


import br.com.civitasauto.DTOs.ListaUsuariosDTO;
import br.com.civitasauto.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Consulta de Usuários", description = "Responsável por realizar diferentes consultas referentes aos usuários do Hub de Automações")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Listagem de usuários",
            description = "Responsável por listar todos os usuários do Hub de Automações sejam ADMIN ou USER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem carregada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida, tente novamente."),
            @ApiResponse(responseCode = "401", description = "Token ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão (requer ADMIN ou USER)"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ListaUsuariosDTO>> listar(
            @Parameter(description = "Lista todos os usuários")
            @PageableDefault(size = 30, sort = {"dataCadastro"}) Pageable pageable) {
        return ResponseEntity.ok(userService.listaTodos(pageable));
    }
}
