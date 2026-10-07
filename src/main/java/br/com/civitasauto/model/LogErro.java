package br.com.civitasauto.model;

import br.com.civitasauto.DTOs.LogErroDTO;
import br.com.civitasauto.enums.Automacoes;
import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import br.com.civitasauto.enums.StatusItem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "log")
public class LogErro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Modulos modulo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Municipios municipio;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusItem status;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime dataHoraLog;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Automacoes automacoes;

    @Column(nullable = false, length = 800)
    private String observacao;

    public LogErro(LogErroDTO log){
        this.modulo = log.modulo();
        this.municipio = log.municipio();
        this.status = log.status();
        this.observacao = log.observacao();
    }
}
