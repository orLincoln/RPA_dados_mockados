package br.com.civitasauto.services;

import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.enums.StatusLote;
import br.com.civitasauto.model.Atribuicao;
import br.com.civitasauto.model.Processamento;
import br.com.civitasauto.repository.AtribuicaoRepository;
import br.com.civitasauto.repository.ProcessamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProcessamentoStatusService {
    @Autowired
    private ProcessamentoRepository processamentoRepository;

    public void atualizarProcessamento(Processamento processamento, StatusItem status, String mensagem) {
        processamento.setMensagem(mensagem);
        processamento.setStatus(status);
        processamento.setAtualizadoEm(LocalDateTime.now());
        processamentoRepository.save(processamento);
    }
}
