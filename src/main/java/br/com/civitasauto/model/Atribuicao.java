package br.com.civitasauto.model;

import br.com.civitasauto.enums.StatusLote;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Atribuicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf_destino")
    private String cpfDestino;

    @ManyToOne
    @JoinColumn(name = "usuario_executor_id")
    private Usuario usuarioExecutor;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusLote status;

    @Column (name = "mensagem")
    private String mensagem;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "atribuicao")
    @OrderBy("id ASC")
    private List<Processamento> itens;

    public Atribuicao() {}

    public Atribuicao(String cpfDestino, Usuario usuarioExecutor) {
        this.cpfDestino = cpfDestino;
        this.usuarioExecutor = usuarioExecutor;
        this.status = StatusLote.PENDENTE;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCpfDestino() { return cpfDestino; }
    public void setCpfDestino(String cpfDestino) { this.cpfDestino = cpfDestino; }

    public Usuario getUsuarioExecutor() { return usuarioExecutor; }
    public void setUsuarioExecutor(Usuario usuarioExecutor) { this.usuarioExecutor = usuarioExecutor; }

    public StatusLote getStatus() { return status; }
    public void setStatus(StatusLote status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public List<Processamento> getItens() { return itens; }
    public void setItens(List<Processamento> itens) { this.itens = itens; }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
