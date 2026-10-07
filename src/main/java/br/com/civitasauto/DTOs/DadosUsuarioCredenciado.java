package br.com.civitasauto.DTOs;

import br.com.civitasauto.model.Usuario;

public record DadosUsuarioCredenciado(Long id, String nome, String cpf) {

    public DadosUsuarioCredenciado(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getCpf());
    }
}
