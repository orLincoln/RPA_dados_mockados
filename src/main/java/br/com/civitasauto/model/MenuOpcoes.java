package br.com.civitasauto.model;

import br.com.civitasauto.enums.Modulos;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MenuOpcoes {

    private static final double TIMEOUT_ITEM = 15000;
    private static final double TIMEOUT_SUBMENU_JA_ABERTO = 1500;

    @Autowired
    private PageManager pageManager;

    public void abrirPerfilUsuario(Modulos modulo) {
        abrirPerfilUsuario(modulo, null);
    }

    public void abrirPerfilUsuario(Modulos modulo, String textoConfirmacaoTela) {
        List<String> caminho = modulo.getCaminhoMenu();
        if (caminho == null || caminho.isEmpty()) {
            throw new IllegalStateException(
                    "Caminho de menu não configurado para o módulo: " + modulo.name());
        }
        navegarMenu(caminho, textoConfirmacaoTela);
    }

    public void navegarMenu(List<String> caminho) {
        navegarMenu(caminho, null);
    }

    public void navegarMenu(List<String> caminho, String textoConfirmacaoTela) {
        Page page = pageManager.getPage();

        for (int i = 0; i < caminho.size(); i++) {
            String rotulo = caminho.get(i);
            boolean folha = (i == caminho.size() - 1);

            Locator item = itemMenu(page, rotulo);
            item.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(TIMEOUT_ITEM));

            if (folha) {
                item.click();
                confirmarTela(page, textoConfirmacaoTela);
                return;
            }

            String proximoRotulo = caminho.get(i + 1);
            Locator filho = itemMenu(page, proximoRotulo);

            // Antes: dependia de filho.isVisible() (leitura instantânea, sujeita a
            // resíduo de tela/menu anterior ainda no DOM) para decidir se clicava
            // no item pai. Isso podia fazer o código concluir que o submenu já
            // estava aberto e pular o clique necessário, navegando errado.
            // Agora: sempre clica no pai e espera o filho ficar visível; se o
            // submenu já estava aberto, o clique é inofensivo (no máximo
            // reabre/fecha) e o waitFor abaixo confirma o estado real da tela.
            page.waitForTimeout(2000);
            page.keyboard().press("Escape");
            item.click();
            try {
                filho.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(TIMEOUT_SUBMENU_JA_ABERTO));
            } catch (com.microsoft.playwright.TimeoutError e) {

                item.click();
                filho.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(TIMEOUT_ITEM));
            }
        }
    }

    private void confirmarTela(Page page, String textoConfirmacaoTela) {
        page.waitForLoadState();
        if (textoConfirmacaoTela != null && !textoConfirmacaoTela.isBlank()) {
            itemMenu(page, textoConfirmacaoTela)
                    .waitFor(new Locator.WaitForOptions()
                            .setState(WaitForSelectorState.VISIBLE)
                            .setTimeout(TIMEOUT_ITEM));
        }
    }
    
    private Locator itemMenu(Page page, String texto) {
        return page.getByText(texto, new Page.GetByTextOptions().setExact(true)).first();
    }
}
