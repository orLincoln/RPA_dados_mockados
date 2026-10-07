package br.com.civitasauto.services;


import br.com.civitasauto.DTOs.AlterarRoleDTO;
import br.com.civitasauto.enums.ResultadoDeferimento;
import br.com.civitasauto.enums.Role;
import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;
import java.util.Optional;

@Service
public class AlterarRoleService {
    @Autowired
    UsuarioRepository repository;
    // Atualmente esse metodo recebe um DTO que tem ROLE e CPF mas somente o cpf
    //é usado de fato para que caso haja necessidade futura de mudar as roles dinamicamente será
    //mais facil
    public ResponseEntity<Map<String, Object>> alterar(@Valid @RequestBody AlterarRoleDTO dados) {
        Optional<Usuario> usuarioOpt = repository.findByCpf(dados.cpf());

        // usuário não existe
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", 404, "mensagem", "Usuário não encontrado"));
        }

        // usuário já admin
        if(usuarioOpt.get().getRole().equals(Role.ADMIN)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", 409, "mensagem", "Usuário já é ADMIN"));
        }

        // usuário indeferido não pode ser admin
        if(usuarioOpt.get().getStatusDeferimento().equals(StatusDeferimento.INDEFERIDO)){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status", 409, "mensagem", "Usuário não foi deferido"));
        }

        System.out.println("Solicitada alteração do usuário para a role " + dados.role());
        Usuario usuario = usuarioOpt.get();

        usuario.setRole(Role.ADMIN);
        repository.save(usuario);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("status", 200, "ok", "ok"));
    }

}
