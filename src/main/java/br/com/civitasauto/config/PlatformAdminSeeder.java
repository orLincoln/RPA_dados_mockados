package br.com.civitasauto.config;

import br.com.civitasauto.enums.Role;
import br.com.civitasauto.enums.StatusDeferimento;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlatformAdminSeeder implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;

    @Value("${CPF_PLATFORM_CPF}")
    private String cpf;
    @Value("${APP_PLATFORM_ADMIN_NAME}")
    private String nome;
    @Value("${APP_PLATFORM_ADMIN_PASSWORD}")
    private String senha;

    @Override
    public void run(String... args) {
        if(this.repository.existsByNome(this.nome)){
            log.info("Platform admin '{}' already exists — skipping seed.", this.nome);
            return;
        }

        final Usuario admin = Usuario.builder()
                .cpf(this.cpf)
                .nome(this.nome)
                .senha(this.encoder.encode(this.senha))
                .role(Role.ADMIN)
                .statusDeferimento(StatusDeferimento.DEFERIDO)
                .build();

        this.repository.save(admin);
        log.info("Platform admin '{}' created.", this.nome);
    }
}
