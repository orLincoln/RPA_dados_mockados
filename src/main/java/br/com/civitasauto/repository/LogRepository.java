package br.com.civitasauto.repository;

import br.com.civitasauto.model.LogErro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LogRepository extends JpaRepository<LogErro, Long>, JpaSpecificationExecutor<LogErro> {
}
