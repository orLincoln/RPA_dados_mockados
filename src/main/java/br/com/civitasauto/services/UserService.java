package br.com.civitasauto.services;


import br.com.civitasauto.DTOs.ListaUsuariosDTO;
import br.com.civitasauto.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UsuarioRepository repository;

    public Page<ListaUsuariosDTO> listaTodos(Pageable paginacao) {
        return repository.findAll(paginacao).map(ListaUsuariosDTO::from);
    }
}
