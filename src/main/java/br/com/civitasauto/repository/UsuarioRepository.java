package br.com.civitasauto.repository;

import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNome(String nome);

    Optional<Usuario> findByCpf(String cpf);

    Page<Usuario> findByStatusDeferimento(StatusDeferimento status, Pageable pageable);

    boolean existsByNome(String name);
}