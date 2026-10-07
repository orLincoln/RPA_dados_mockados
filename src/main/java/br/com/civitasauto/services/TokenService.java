package br.com.civitasauto.services;

import br.com.civitasauto.model.Usuario;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiracao-minutos}")
    private long expiracaoMinutos;

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .issuer("civitas_auto")
                .subject(usuario.getNome())
                .claim("role", usuario.getRole().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(expiracaoMinutos, ChronoUnit.MINUTES)))
                .signWith(chave())
                .compact();
    }

    /**
     * Valida assinatura e expiração do token e devolve o subject (nome).
     *
     * @throws JwtException se o token for inválido ou estiver expirado
     */
    public String getSubject(String token) {
        return Jwts.parser()
                .verifyWith(chave())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    private SecretKey chave() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
