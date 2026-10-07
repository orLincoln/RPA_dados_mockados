package br.com.civitasauto.services;

import br.com.civitasauto.DTOs.DadosAtribuirPerfil;
import br.com.civitasauto.DTOs.DadosProcessamento;
import br.com.civitasauto.DTOs.RespostaItemAtribuicao;
import br.com.civitasauto.DTOs.RespostaStatusAtribuicao;
import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.enums.StatusLote;
import br.com.civitasauto.model.Atribuicao;
import br.com.civitasauto.model.Processamento;
import br.com.civitasauto.model.Usuario;
import br.com.civitasauto.repository.AtribuicaoRepository;
import br.com.civitasauto.repository.ProcessamentoRepository;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Scope("prototype")
public class AtribuicaoPerfilService {

    @Autowired
    private AtribuicaoRepository atribuicaoRepository;

    @Autowired
    private ProcessamentoRepository processamentoRepository;

    @Autowired
    private AtribuicaoStatusService atribuicaoStatusService;

    public Long atribuicaoPerfil(DadosAtribuirPerfil dados, Usuario usuarioExecutor) {
        if (dados.municipios() == null || dados.municipios().isEmpty()
                || dados.modulos() == null || dados.modulos().isEmpty()) {
            throw new ValidationException("Nenhum município ou módulo foi selecionado.");
        }

        Atribuicao atribuicao = new Atribuicao(dados.cpf(), usuarioExecutor);
        atribuicaoRepository.save(atribuicao);

        dados.municipios().forEach(mu ->
                dados.modulos().forEach(mo -> {
                    DadosProcessamento dadosProcessamento = new DadosProcessamento(mu, mo);
                    Processamento processamento = new Processamento(dadosProcessamento);
                    processamento.setAtribuicao(atribuicao);
                    processamento.setStatus(StatusItem.PROCESSANDO);
                    processamentoRepository.save(processamento);
                })
        );

        atribuicaoStatusService.atualizarAtribuicao(atribuicao, StatusLote.PROCESSANDO, "Atribuição em processamento.");

        return atribuicao.getId();
    }

    @Transactional(readOnly = true)
    public RespostaStatusAtribuicao consultarStatus(Long jobId) {
        Atribuicao atribuicao = atribuicaoRepository.findById(jobId)
                .orElseThrow(() -> new NoSuchElementException("Atribuição não encontrada: " + jobId));

        List<RespostaItemAtribuicao> resultado = processamentoRepository
                .findByAtribuicaoIdOrderByIdAsc(jobId).stream()
                .map(item -> new RespostaItemAtribuicao(
                        item.getMunicipio().name(),
                        item.getModulo().name(),
                        item.getStatus().name(), item.getMensagem()))
                .toList();

        return new RespostaStatusAtribuicao(atribuicao.getStatus().name(), null, resultado);
    }
}
