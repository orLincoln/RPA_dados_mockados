package br.com.civitasauto.services;

import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.enums.StatusLote;
import br.com.civitasauto.model.Acesso;
import br.com.civitasauto.model.Atribuicao;
import br.com.civitasauto.model.AtribuirPerfil;
import br.com.civitasauto.model.Processamento;
import br.com.civitasauto.model.Sessao;
import br.com.civitasauto.repository.AtribuicaoRepository;
import br.com.civitasauto.repository.ProcessamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AtribuicaoExecucaoServiceTest {

    @Mock
    private AtribuicaoRepository atribuicaoRepository;

    @Mock
    private ProcessamentoRepository processamentoRepository;

    @Mock
    private Acesso acesso;

    @Mock
    private Sessao sessao;

    @Mock
    private AtribuirPerfil atribuirPerfil;

    @Mock
    private AtribuicaoStatusService atribuicaoStatusService;

    @Mock
    private ProcessamentoStatusService processamentoStatusService;

    @InjectMocks
    private AtribuicaoExecucaoService atribuicaoExecucaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStatusLoteAgregadoTodosSucesso() {
        // Cenário: todos os itens com sucesso → deve marcar lote como CONCLUIDO
        Long atribuicaoId = 1L;
        Atribuicao atribuicao = new Atribuicao();
        atribuicao.setId(atribuicaoId);
        atribuicao.setCpfDestino("12345678900");

        Processamento item1 = new Processamento();
        item1.setStatus(StatusItem.SUCESSO);
        item1.setMunicipio(Municipios.ALTAMIRA);
        item1.setModulo(Modulos.PORTALEMPRESA);

        Processamento item2 = new Processamento();
        item2.setStatus(StatusItem.SUCESSO);
        item2.setMunicipio(Municipios.ALTAMIRA);
        item2.setModulo(Modulos.PORTALEMPRESA);

        List<Processamento> itens = Arrays.asList(item1, item2);

        when(atribuicaoRepository.findById(atribuicaoId)).thenReturn(Optional.of(atribuicao));
        when(processamentoRepository.findByAtribuicaoIdOrderByIdAsc(atribuicaoId)).thenReturn(itens);

        atribuicaoExecucaoService.executar(atribuicaoId);

        ArgumentCaptor<StatusLote> statusCaptor = ArgumentCaptor.forClass(StatusLote.class);
        verify(atribuicaoStatusService).atualizarAtribuicao(any(Atribuicao.class), statusCaptor.capture(), any(String.class));

        assertEquals(StatusLote.CONCLUIDO, statusCaptor.getValue());
    }

    @Test
    void testStatusLoteAgregadoMix() {
        // Cenário: mix de sucesso e erro → deve marcar lote como CONCLUIDO_COM_ERROS
        Long atribuicaoId = 1L;
        Atribuicao atribuicao = new Atribuicao();
        atribuicao.setId(atribuicaoId);
        atribuicao.setCpfDestino("12345678900");

        Processamento item1 = new Processamento();
        item1.setStatus(StatusItem.SUCESSO);
        item1.setMunicipio(Municipios.ALTAMIRA);
        item1.setModulo(Modulos.PORTALEMPRESA);

        Processamento item2 = new Processamento();
        item2.setStatus(StatusItem.ERRO);
        item2.setMunicipio(Municipios.ALTAMIRA);
        item2.setModulo(Modulos.PORTALEMPRESA);

        List<Processamento> itens = Arrays.asList(item1, item2);

        when(atribuicaoRepository.findById(atribuicaoId)).thenReturn(Optional.of(atribuicao));
        when(processamentoRepository.findByAtribuicaoIdOrderByIdAsc(atribuicaoId)).thenReturn(itens);

        atribuicaoExecucaoService.executar(atribuicaoId);

        ArgumentCaptor<StatusLote> statusCaptor = ArgumentCaptor.forClass(StatusLote.class);
        verify(atribuicaoStatusService).atualizarAtribuicao(any(Atribuicao.class), statusCaptor.capture(), any(String.class));

        assertEquals(StatusLote.CONCLUIDO_COM_ERROS, statusCaptor.getValue());
    }

    @Test
    void testStatusLoteAgregadoTodosErro() {
        // Cenário: todos os itens com erro → deve marcar lote como ERRO
        Long atribuicaoId = 1L;
        Atribuicao atribuicao = new Atribuicao();
        atribuicao.setId(atribuicaoId);
        atribuicao.setCpfDestino("12345678900");

        Processamento item1 = new Processamento();
        item1.setStatus(StatusItem.ERRO);
        item1.setMunicipio(Municipios.ALTAMIRA);
        item1.setModulo(Modulos.PORTALEMPRESA);

        Processamento item2 = new Processamento();
        item2.setStatus(StatusItem.ERRO);
        item2.setMunicipio(Municipios.ALTAMIRA);
        item2.setModulo(Modulos.PORTALEMPRESA);

        List<Processamento> itens = Arrays.asList(item1, item2);

        when(atribuicaoRepository.findById(atribuicaoId)).thenReturn(Optional.of(atribuicao));
        when(processamentoRepository.findByAtribuicaoIdOrderByIdAsc(atribuicaoId)).thenReturn(itens);

        atribuicaoExecucaoService.executar(atribuicaoId);

        ArgumentCaptor<StatusLote> statusCaptor = ArgumentCaptor.forClass(StatusLote.class);
        verify(atribuicaoStatusService).atualizarAtribuicao(any(Atribuicao.class), statusCaptor.capture(), any(String.class));

        assertEquals(StatusLote.ERRO, statusCaptor.getValue());
    }

    @Test
    void testStatusLoteAgregadoComJaAtribuido() {
        // Cenário: mix de SUCESSO, JA_ATRIBUIDO e ERRO → deve marcar lote como CONCLUIDO_COM_ERROS
        Long atribuicaoId = 1L;
        Atribuicao atribuicao = new Atribuicao();
        atribuicao.setId(atribuicaoId);
        atribuicao.setCpfDestino("12345678900");

        Processamento item1 = new Processamento();
        item1.setStatus(StatusItem.SUCESSO);
        item1.setMunicipio(Municipios.ALTAMIRA);
        item1.setModulo(Modulos.PORTALEMPRESA);

        Processamento item2 = new Processamento();
        item2.setStatus(StatusItem.JA_ATRIBUIDO);
        item2.setMunicipio(Municipios.ALTAMIRA);
        item2.setModulo(Modulos.PORTALEMPRESA);

        Processamento item3 = new Processamento();
        item3.setStatus(StatusItem.ERRO);
        item3.setMunicipio(Municipios.ALTAMIRA);
        item3.setModulo(Modulos.PORTALEMPRESA);

        List<Processamento> itens = Arrays.asList(item1, item2, item3);

        when(atribuicaoRepository.findById(atribuicaoId)).thenReturn(Optional.of(atribuicao));
        when(processamentoRepository.findByAtribuicaoIdOrderByIdAsc(atribuicaoId)).thenReturn(itens);

        atribuicaoExecucaoService.executar(atribuicaoId);

        ArgumentCaptor<StatusLote> statusCaptor = ArgumentCaptor.forClass(StatusLote.class);
        verify(atribuicaoStatusService).atualizarAtribuicao(any(Atribuicao.class), statusCaptor.capture(), any(String.class));

        assertEquals(StatusLote.CONCLUIDO_COM_ERROS, statusCaptor.getValue());
    }
}
