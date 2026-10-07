package br.com.civitasauto.services;

import br.com.civitasauto.DTOs.DadosCredenciamento;
import br.com.civitasauto.DTOs.DadosUsuarioCredenciado;
import br.com.civitasauto.enums.Role;
import br.com.civitasauto.exception.ValidacaoException;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CredenciamentoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public DadosUsuarioCredenciado credenciar(DadosCredenciamento dados) {
        if (usuarioRepository.findByNome(dados.nome()).isPresent()) {
            throw new ValidacaoException("Já existe um usuário com este nome.");
        }

        // Remove máscara do CPF (aceita entrada formatada, persiste só os dígitos).
        var cpf = dados.cpf().replaceAll("\\D", "");

        if (usuarioRepository.findByCpf(cpf).isPresent()) {
            throw new ValidacaoException("Já existe um usuário com este CPF.");
        }

        // Role sempre USER: endpoint público, o solicitante não escolhe o perfil.
        Usuario usuario = new Usuario(
                dados.nome(),
                passwordEncoder.encode(dados.senha()),
                cpf,
                Role.USER
        );

        usuarioRepository.save(usuario);
        return new DadosUsuarioCredenciado(usuario);
    }
}
