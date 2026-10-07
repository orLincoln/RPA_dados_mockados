package br.com.civitasauto.services;

import br.com.civitasauto.DTOs.LogErroDTO;
import br.com.civitasauto.repository.LogRepository;
import br.com.civitasauto.specifications.LogSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    LogRepository logRepository;

    public Page<LogErroDTO> listagem (LogErroDTO log, Pageable pageable) {
        return logRepository.findAll(LogSpecifications.comFiltros(log), pageable).map(LogErroDTO::from);
    }
}
