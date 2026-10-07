package br.com.civitasauto.model;

import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import br.com.civitasauto.enums.StatusItem;
import br.com.civitasauto.services.ProcessamentoStatusService;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Scope("prototype")
public class AtribuirPerfil {

    @Autowired
    private PageManager pageManager;

    @Autowired
    private MenuOpcoes menu;

    public Atribuicao atribuicao;
    public Processamento processamento;

    @Autowired
    private ProcessamentoStatusService processamentoStatusService;

    public void telaAtribuicaoPerfil(Municipios municipio, Modulos modulo, String cpf, Atribuicao atribuicao, Processamento processamento) {
            Page page = pageManager.getPage();
            page.setDefaultTimeout(10000);

            menu.abrirPerfilUsuario(modulo, "Incluir");
            System.out.println("URL: " + page.url());

            page.waitForLoadState();

            System.out.println("Tela de Atribuição");
            System.out.println("URL: " + page.url());

            page.waitForTimeout(5000);

            Locator btnIncluir = page.getByText("Incluir", new Page.GetByTextOptions().setExact(true));
            btnIncluir.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(2000));
            btnIncluir.click();

            if (page.url().contains("nfsd")) {
                var campoIncluir = page.locator("#cpf");
                campoIncluir.click();
                campoIncluir.fill("");
                page.waitForTimeout(2000);
                campoIncluir.pressSequentially(cpf, new Locator.PressSequentiallyOptions().setDelay(190));
                var botaoPesquisar = page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Pesquisar")
                );
                botaoPesquisar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
                botaoPesquisar.click();

                if (validacaoMensagemErroNfsdEd(cpf, atribuicao, processamento)) return;

                page.locator("#perfil_label").click();
                page.locator("#perfil_items").waitFor();
                page.waitForTimeout(5000);

                if (verificarExistenciaRPANfsdEd(atribuicao, processamento)) return;

                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("RPA")).click();
                Locator btnIncluir2 = page.getByText("Incluir", new Page.GetByTextOptions().setExact(true));
                btnIncluir2.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(1000));
                btnIncluir2.click();

                Locator btnSalvar = page.getByText("Salvar", new Page.GetByTextOptions().setExact(true));
                btnSalvar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(1000));
                btnSalvar.click();

                processamentoStatusService.atualizarProcessamento(processamento, StatusItem.SUCESSO, "Atribuição concluída!");

            } else if (page.url().contains("portalempresa")) {
                var campoIncluir = page.locator("#cpf");
                campoIncluir.click();
                campoIncluir.fill("");
                campoIncluir.pressSequentially(cpf, new Locator.PressSequentiallyOptions().setDelay(150));

                var botaoPesquisar = page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Pesquisar")
                );
                botaoPesquisar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(10000));
                botaoPesquisar.click();

                if (validacaoMensagemErroNfsdEd(cpf, atribuicao, processamento)) return;

                page.locator("#permissao_label").click();
                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("SIM")).click();

                page.locator("#perfil_label").click();
                page.locator("#perfil_items").waitFor();
                page.waitForTimeout(5000);

                if (verificarExistenciaRPANfsdEd(atribuicao, processamento)) return;

                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("RPA")).click();
                page.locator("#incluirUsuario").click();

                if (!verificarInstituicaoJaVinculada(atribuicao, processamento)) {
                    page.locator("#instituicao_label").click();
                    page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName(municipio.getInstituicaoPortalEmpresa())).click();
                    page.locator("#incluirInstituicao").click();
                }

                Locator btnSalvar = page.getByText("Salvar", new Page.GetByTextOptions().setExact(true));
                btnSalvar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(1000));
                btnSalvar.click();

                processamentoStatusService.atualizarProcessamento(processamento, StatusItem.SUCESSO, "Atribuição concluída!");
            } else {
                var campoIncluir = page.locator("#documentoFiscal");
                campoIncluir.click();
                campoIncluir.fill("");
                campoIncluir.pressSequentially(cpf, new Locator.PressSequentiallyOptions().setDelay(100));

                page.locator(".pi-search").click();

                if (validacaoMensagemErro(cpf, atribuicao, processamento)){
                    return;
                }

                page.locator(".p-dropdown-label").click();
                page.waitForTimeout(5000);

                if (validarPerfilNaoRPA(cpf, atribuicao, processamento)) return;

                page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("RPA - ADMINISTRAÇÃO DA PREFEITURA")).click();

                Locator btnIncluir2 = page.getByText("Incluir", new Page.GetByTextOptions().setExact(true));
                btnIncluir2.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(1000));
                btnIncluir2.click();

                Locator btnSalvar = page.getByText("Salvar", new Page.GetByTextOptions().setExact(true));
                btnSalvar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(1000));
                btnSalvar.click();

                processamentoStatusService.atualizarProcessamento(processamento, StatusItem.SUCESSO, "Atribuição concluída!");
            }

            page.waitForTimeout(5000);
            page.context().clearCookies();
            page.reload();
    }

    public boolean validacaoMensagemErro(String cpf, Atribuicao atribuicao, Processamento processamento) {
        Page page = pageManager.getPage();
        try {
            Locator mensagem = page.locator(".toast-message:has-text('Não há pessoa cadastrada')");
            mensagem.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));

            String texto = mensagem.textContent();
            log.error("❌ " + texto);
            processamentoStatusService.atualizarProcessamento(processamento, StatusItem.ERRO, texto);
            return true;

        } catch (TimeoutError e) {
            log.info("✅ CPF " + cpf + " encontrado com sucesso!");
            return false;
        }
    }

    public boolean validacaoMensagemErroNfsdEd(String cpf, Atribuicao atribuicao, Processamento processamento) {
        Page page = pageManager.getPage();
        try {
            Locator mensagem = page.getByText("Informe um usuário válido para vincular um perfil.");
            mensagem.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(3000));

            String texto = mensagem.textContent();
            log.error("❌ " + texto);
            processamentoStatusService.atualizarProcessamento(processamento, StatusItem.ERRO, texto);
            return true;
        } catch (TimeoutError e) {
            log.info("✅ CPF " + cpf + " encontrado com sucesso!");
            return false;
        }
    }

    public boolean validarPerfilNaoRPA(String cpf, Atribuicao atribuicao, Processamento processamento) {
        return verificarExistenciaRPA(cpf, atribuicao, processamento, "tr.ng-star-inserted");
    }

    public boolean verificarExistenciaRPANfsdEd(Atribuicao atribuicao, Processamento processamento) {
        return verificarExistenciaRPA(null, atribuicao, processamento, "table tr");
    }
    private boolean verificarExistenciaRPA(String cpf, Atribuicao atribuicao, Processamento processamento, String seletorLinhas) {
        Page page = pageManager.getPage();
        Locator linhas = page.locator(seletorLinhas);
        Locator linhaRPA = linhas.filter(new Locator.FilterOptions()
                .setHasText("RPA"));

        if (linhaRPA.count() > 0) {
            String cpfLog = cpf != null ? "CPF " + cpf : "usuário pesquisado";
            log.error("❌ ERRO: " + cpfLog + " já possui o perfil RPA!");
            processamentoStatusService.atualizarProcessamento(processamento, StatusItem.JA_ATRIBUIDO, "ℹ️ Usuário já foi atribuído.");
            return true;
        }
        log.info("✅ Perfil RPA não encontrado, continuando automação...");
        return false;
    }

    private boolean verificarInstituicaoJaVinculada(Atribuicao atribuicao, Processamento processamento) {
        Page page = pageManager.getPage();

        Locator linhas = page.locator("div, fieldset, section")
                .filter(new Locator.FilterOptions().setHasText("Instituições do Usuário"))
                .locator("tr");

        if (linhas.count() > 0) {
            log.info("ℹ️ Usuário já possui instituição vinculada, pulando etapa de seleção de instituição...");
            return true;
        }

        log.info("✅ Nenhuma instituição vinculada, prosseguindo com a seleção...");
        return false;
    }
}
