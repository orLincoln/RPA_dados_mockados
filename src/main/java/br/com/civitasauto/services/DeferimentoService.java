package br.com.civitasauto.services;


import br.com.civitasauto.DTOs.DeferimentoDTO;
import br.com.civitasauto.enums.ResultadoDeferimento;
import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeferimentoService {

    @Autowired
    UsuarioRepository repository;
    
    public Page<DeferimentoDTO> listar(StatusDeferimento status, Pageable pageable) {
        Page<Usuario> usuarios = (status == null)
                ? repository.findAll(pageable)
                : repository.findByStatusDeferimento(status, pageable);

        return usuarios.map(u -> new DeferimentoDTO(
                u.getNome(),
                u.getCpf(),
                u.getDataCadastro(),
                u.getStatusDeferimento()));
    }

    public ResultadoDeferimento deferir(String cpf) {
        Optional<Usuario> usuarioOpt = repository.findByCpf(cpf); // busca possivel usuário -> optional

        if (usuarioOpt.isEmpty()) {
            return ResultadoDeferimento.USUARIO_NAO_ENCONTRADO;
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getStatusDeferimento() == StatusDeferimento.DEFERIDO) {
            return ResultadoDeferimento.JA_DEFERIDO;
        }

        usuario.setStatusDeferimento(StatusDeferimento.DEFERIDO);
        repository.save(usuario);

        return ResultadoDeferimento.SUCESSO;
    }

}
