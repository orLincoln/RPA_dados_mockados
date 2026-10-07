package br.com.civitasauto.model;

import br.com.civitasauto.enums.Role;
import br.com.civitasauto.enums.StatusDeferimento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@SuperBuilder
@EntityListeners(AuditingEntityListener.class)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", unique = true, nullable = false)
    private String nome;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "statusDeferimento", nullable = false, length = 64)
    @Enumerated(EnumType.STRING)
    private StatusDeferimento statusDeferimento;

    @CreatedDate
    @Column(name = "dataCadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public Usuario() {}

    public Usuario(String nome, String senha, String cpf, Role role) {
        this.nome = nome;
        this.senha = senha;
        this.cpf = cpf;
        this.role = role;
        this.statusDeferimento = StatusDeferimento.INDEFERIDO;
    }

    public LocalDateTime getDataCadastro() {return dataCadastro;}

    public StatusDeferimento getStatusDeferimento(){return this.statusDeferimento;}

    public void setStatusDeferimento(StatusDeferimento status){this.statusDeferimento = status;}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return nome;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
