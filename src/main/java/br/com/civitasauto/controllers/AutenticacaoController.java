package br.com.civitasauto.controllers;

import br.com.civitasauto.DTOs.DadosAutenticacao;
import br.com.civitasauto.DTOs.DadosTokenJWT;
import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.exception.UsuarioAguardandoDeferimentoException;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.services.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Tag(name = "Autenticação", description = "Login e emissão de token JWT")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Operation(summary = "Autenticar usuário",
            description = "Valida CPF e senha e retorna um token JWT. Requer que o usuário já esteja deferido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida, token retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "403", description = "Usuário aguardando deferimento manual"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor. Tente novamente em breve.")
    })
    @SecurityRequirements
    @PostMapping
    @Transactional
    public ResponseEntity<DadosTokenJWT> efetuarLogin(@RequestBody @Valid DadosAutenticacao dados) {
        var cpf = dados.cpf().replaceAll("\\D", "");
        var authenticationToken = new UsernamePasswordAuthenticationToken(cpf, dados.senha());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var usuario = (Usuario) authentication.getPrincipal();

        if (usuario.getStatusDeferimento() != StatusDeferimento.DEFERIDO) {
            throw new UsuarioAguardandoDeferimentoException(
                    "Usuário aguardando deferimento manual.");
        }

        var token = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(new DadosTokenJWT(token));
    }
}
