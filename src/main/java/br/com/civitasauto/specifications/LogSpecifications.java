package br.com.civitasauto.specifications;

import br.com.civitasauto.DTOs.LogErroDTO;
import br.com.civitasauto.enums.Automacoes;
import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.model.LogErro;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LogSpecifications {

    public static Specification<LogErro> comFiltros(LogErroDTO log){
        return Specification
                .where(porModulo(log.modulo()))
                .and(porMunicipio(log.municipio()))
                .and(porAutomacao(log.automacoes()))
                .and(porStatus(log.status()))
                .and(porPeriodo(log.dataHoraLog(), log.dataHoraLogFim()));
    }

    public static Specification<LogErro> porModulo(Modulos modulo){
        return (root, query, builder) -> modulo == null ? null : builder.equal(root.get("modulo"), modulo);
    }

    public static Specification<LogErro> porMunicipio(Municipios muncipio){
        return (root, query, builder) -> muncipio == null ? null : builder.equal(root.get("municipio"), muncipio);
    }

    public static Specification<LogErro> porStatus(StatusItem status){
        return (root, query, builder) -> status == null ? null : builder.equal(root.get("status"), status);
    }

    public static Specification<LogErro> porAutomacao(Automacoes automacoes){
        return (root, query, builder) -> automacoes == null ? null : builder.equal(root.get("automacoes"), automacoes);
    }

    public static Specification<LogErro> porPeriodo(LocalDateTime inicio, LocalDateTime fim){
        return (root, query, builder) -> {
            if(inicio == null && fim == null) return null;

            if(inicio == null) return builder.lessThanOrEqualTo(root.get("dataHoraLog"), fim);
            if(fim == null) return builder.greaterThanOrEqualTo(root.get("dataHoraLog"), inicio);
            return builder.between(root.get("dataHoraLog"), inicio, fim);
        };
    }
}
