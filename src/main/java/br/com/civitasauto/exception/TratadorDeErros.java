package br.com.civitasauto.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, Object>> tratarErroRegraDeNegocio(ValidacaoException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "erro", e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> tratarErroAutenticacao(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", 401, "erro", "Credenciais inválidas."));
    }

    @ExceptionHandler(UsuarioAguardandoDeferimentoException.class)
    public ResponseEntity<Map<String, Object>> tratarUsuarioAguardandoDeferimento(UsuarioAguardandoDeferimentoException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", 403, "erro", e.getMessage(), "codigo", "AGUARDANDO_DEFERIMENTO"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> tratarErroValidacao(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "erro", "Dados inválidos na requisição."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> tratarCorpoRequisicaoInvalido(HttpMessageNotReadableException e) {
        Throwable causaRaiz = e.getMostSpecificCause();
        String mensagem = "Corpo da requisição inválido.";

        InvalidFormatException ife = buscarCausa(e, InvalidFormatException.class);
        ValueInstantiationException vie = buscarCausa(e, ValueInstantiationException.class);
        JsonParseException jpe = buscarCausa(e, JsonParseException.class);

        if (ife != null && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            mensagem = String.format(
                    "Valor '%s' inválido para o campo '%s'. Valores aceitos: [%s]",
                    ife.getValue(),
                    nomeCampo(ife.getPath()),
                    valoresValidos(ife.getTargetType()));
        } else if (vie != null && vie.getType() != null && vie.getType().isEnumType()) {
            String detalhe = causaRaiz.getMessage() == null
                    ? "Valor inválido para o campo '" + nomeCampo(vie.getPath()) + "'"
                    : causaRaiz.getMessage();
            mensagem = String.format("%s. Valores aceitos: [%s]",
                    detalhe, valoresValidos(vie.getType().getRawClass()));
        } else if (jpe != null) {
            mensagem = "JSON malformado: " + jpe.getOriginalMessage();
        } else if (causaRaiz.getMessage() != null) {
            mensagem = causaRaiz.getMessage();
        }

        return ResponseEntity.badRequest()
                .body(Map.of("status", 400, "erro", mensagem));
    }

    private <T extends Throwable> T buscarCausa(Throwable e, Class<T> tipo) {
        Throwable atual = e;
        while (atual != null) {
            if (tipo.isInstance(atual)) {
                return tipo.cast(atual);
            }
            atual = atual.getCause();
        }
        return null;
    }

    private String nomeCampo(List<JsonMappingException.Reference> path) {
        if (path == null || path.isEmpty()) {
            return "?";
        }
        return path.get(path.size() - 1).getFieldName();
    }

    private String valoresValidos(Class<?> tipoEnum) {
        return Arrays.stream(tipoEnum.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.joining(", "));
    }
}
