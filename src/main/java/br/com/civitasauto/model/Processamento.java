package br.com.civitasauto.model;

import br.com.civitasauto.DTOs.DadosProcessamento;
import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import br.com.civitasauto.enums.StatusItem;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"atribuicao_id", "fila_municipio", "fila_modulo"}))
public class Processamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "atribuicao_id")
    private Atribuicao atribuicao;

    @Column(name = "fila_municipio")
    @Enumerated(EnumType.STRING)
    private Municipios municipio;

    @Column(name = "fila_modulo")
    @Enumerated(EnumType.STRING)
    private Modulos modulo;

    @Column(name = "mensagem", length = 1024)
    private String mensagem;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusItem status;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public Processamento(DadosProcessamento dados){
        this.municipio = dados.municipio();
        this.modulo = dados.modulo();
        this.status = StatusItem.PENDENTE;
    }

    public Processamento(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atribuicao getAtribuicao() {
        return atribuicao;
    }

    public void setAtribuicao(Atribuicao atribuicao) {
        this.atribuicao = atribuicao;
    }

    public Municipios getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipios municipio) {
        this.municipio = municipio;
    }

    public Modulos getModulo() {
        return modulo;
    }

    public void setModulo(Modulos modulo) {
        this.modulo = modulo;
    }

    public StatusItem getStatus() {
        return status;
    }

    public void setStatus(StatusItem status) {
        this.status = status;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
