package br.com.civitasauto.services;

import br.com.civitasauto.enums.StatusLote;
import br.com.civitasauto.model.Atribuicao;
import br.com.civitasauto.repository.AtribuicaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AtribuicaoStatusService {
    @Autowired
    private AtribuicaoRepository atribuicaoRepository;

    public void atualizarAtribuicao(Atribuicao atribuicao, StatusLote status, String mensagem) {
        atribuicao.setMensagem(mensagem);
        atribuicao.setStatus(status);
        atribuicao.setAtualizadoEm(LocalDateTime.now());
        atribuicaoRepository.save(atribuicao);
    }
}
