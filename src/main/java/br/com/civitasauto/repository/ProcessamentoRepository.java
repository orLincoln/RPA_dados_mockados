package br.com.civitasauto.repository;

import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.model.Processamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessamentoRepository extends JpaRepository<Processamento, Long> {

    List<Processamento> findByAtribuicaoIdOrderByIdAsc(Long atribuicaoId);

    long countByAtribuicaoIdAndStatus(Long atribuicaoId, StatusItem status);

    long countByAtribuicaoId(Long atribuicaoId);
}
