package br.com.civitasauto.config;

import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.repository.UsuarioRepository;
import br.com.civitasauto.services.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            try {
                String nome = tokenService.getSubject(token);

                usuarioRepository.findByNome(nome)
                        .filter(usuario -> usuario.getStatusDeferimento() == StatusDeferimento.DEFERIDO)
                        .ifPresent(usuario -> {
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    usuario, null, usuario.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring("Bearer ".length()).trim();
        }
        return null;
    }
}
