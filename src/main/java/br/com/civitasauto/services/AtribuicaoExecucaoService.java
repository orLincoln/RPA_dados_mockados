package br.com.civitasauto.services;

import br.com.civitasauto.enums.*;
import br.com.civitasauto.model.*;
import br.com.civitasauto.repository.AtribuicaoRepository;
import br.com.civitasauto.repository.LogRepository;
import br.com.civitasauto.repository.ProcessamentoRepository;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class AtribuicaoExecucaoService {

    @Autowired
    private AtribuicaoRepository atribuicaoRepository;

    @Autowired
    private ProcessamentoRepository processamentoRepository;

    @Autowired
    private Acesso acesso;

    @Autowired
    private Sessao sessao;

    @Autowired
    private AtribuirPerfil atribuirPerfil;

    @Autowired
    private AtribuicaoStatusService atribuicaoStatusService;

    @Autowired
    private PageManager pageManager;

    @Autowired
    private LogRepository logRepository;

    private void calcularEAtualizarStatusLoteAgregado(Atribuicao atribuicao, List<Processamento> itens) {
        long sucessoCount = itens.stream()
                .filter(p -> p.getStatus() == StatusItem.SUCESSO || p.getStatus() == StatusItem.JA_ATRIBUIDO)
                .count();
        long erroCount = itens.stream()
                .filter(p -> p.getStatus() == StatusItem.ERRO)
                .count();

        StatusLote novoStatus;
        String mensagem;

        if (erroCount == 0) {
            novoStatus = StatusLote.CONCLUIDO;
            mensagem = "Atribuição concluída com sucesso!";
        } else if (sucessoCount > 0) {
            novoStatus = StatusLote.CONCLUIDO_COM_ERROS;
            mensagem = "Atribuição concluída com erros em alguns itens. Sucesso: " + sucessoCount + ", Erros: " + erroCount;
        } else {
            novoStatus = StatusLote.ERRO;
            mensagem = "Erro na Atribuição. Todos os itens falharam.";
        }

        atribuicaoStatusService.atualizarAtribuicao(atribuicao, novoStatus, mensagem);
    }

    private String observacaoDoProcessamento(Processamento processamento) {
        String mensagem = processamento.getMensagem();
        if (mensagem == null || mensagem.isBlank()) {
            mensagem = processamento.getStatus().name();
        }
        return mensagem.length() > 800 ? mensagem.substring(0, 800) : mensagem;
    }

    private String resumoErro(Exception e, String tela) {
        String mensagem = e.getMessage() == null ? e.toString() : e.getMessage();
        int inicioCallLog = mensagem.indexOf("Call log:");
        String detalhe = inicioCallLog >= 0
                ? mensagem.substring(inicioCallLog)
                : e.getClass().getSimpleName() + ": " + mensagem;
        String resumo = (tela + " ----------------------- " + detalhe).replaceAll("\\s+", " ").trim();
        return resumo.length() > 1024 ? resumo.substring(0, 1024) : resumo;
    }

    @Async("atribuicaoExecutor")
    public void executar(Long atribuicaoId) {
        Atribuicao atribuicao = atribuicaoRepository.findById(atribuicaoId)
                .orElseThrow(() -> new IllegalStateException("Atribuição não encontrada: " + atribuicaoId));

        String cpfDestino = atribuicao.getCpfDestino();

        try {
            try {
                List<Processamento> itens = processamentoRepository.findByAtribuicaoIdOrderByIdAsc(atribuicaoId);

                Municipios municipioAnterior = null;
                Modulos moduloAnterior = null;

                for (Processamento processamento : itens) {
                    processamento.setAtualizadoEm(LocalDateTime.now());
                    processamentoRepository.save(processamento);

                    LogErro logErro = new LogErro();
                    logErro.setMunicipio(processamento.getMunicipio());
                    logErro.setModulo(processamento.getModulo());
                    logErro.setDataHoraLog(LocalDateTime.now());
                    logErro.setAutomacoes(Automacoes.ATRIBUICAO_DE_PERFIL);

                    try {
                        boolean mudouMunicipio = municipioAnterior == null || !municipioAnterior.equals(processamento.getMunicipio());
                        boolean mudouModulo = moduloAnterior == null || !moduloAnterior.equals(processamento.getModulo());

                        if (mudouMunicipio || mudouModulo) {
                            System.out.println("Contexto mudou (município: " + municipioAnterior + " → " + processamento.getMunicipio()
                                    + ", módulo: " + moduloAnterior + " → " + processamento.getModulo() + "). Limpando sessão...");
                            sessao.limparSessaoCompleta();
                            municipioAnterior = processamento.getMunicipio();
                            moduloAnterior = processamento.getModulo();
                        }

                        System.out.println("Iniciando acesso ao módulo: " + processamento.getModulo());
                        acesso.fazerLogin(processamento.getMunicipio(), processamento.getModulo());
                        log.info("fazendo login uma vez");

                        if(pageManager.getPage().url().contains("login") || pageManager.getPage().url().contains("acesso")){
                            acesso.fazerLogin(processamento.getMunicipio(), processamento.getModulo());
                            log.info("fazendo login novamente");
                        }

                        System.out.println("Indo para atribuição...");
                        atribuirPerfil.telaAtribuicaoPerfil(processamento.getMunicipio(), processamento.getModulo(), cpfDestino, atribuicao, processamento);
                        System.out.println(processamento.getMensagem());
                        processamentoRepository.save(processamento);
                        atribuicaoRepository.save(atribuicao);
                    } catch (Exception e) {
                        String tela = pageManager.descreverTela();
                        log.error("Erro no processamento {} ({})", processamento.getId(), tela, e);
                        processamento.setStatus(StatusItem.ERRO);
                        processamento.setMensagem(resumoErro(e, tela));
                    }
                    processamento.setAtualizadoEm(LocalDateTime.now());
                    processamentoRepository.save(processamento);
                    System.out.println(atribuicaoId);

                    logErro.setStatus(processamento.getStatus());
                    logErro.setObservacao(observacaoDoProcessamento(processamento));
                    logRepository.save(logErro);
                }

                calcularEAtualizarStatusLoteAgregado(atribuicao, itens);

            } catch (Exception e) {
                log.error("Erro na atribuição {}", atribuicaoId, e);
                atribuicaoStatusService.atualizarAtribuicao(atribuicao, StatusLote.ERRO, "Erro na Atribuição, verificar log para mais informações.");
            }
        } finally {
            pageManager.closeBrowser();
        }
    }
}
