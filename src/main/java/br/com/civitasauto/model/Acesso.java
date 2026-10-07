package br.com.civitasauto.model;

import br.com.civitasauto.enums.Modulos;
import br.com.civitasauto.enums.Municipios;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Acesso {

    @Autowired
    private PageManager pageManager;

    @Value("${app.rpa.login}")
    private String loginRpa;

    @Value("${app.rpa.senha}")
    private String senhaRpa;

    @Value("${app.rpa.url-base}")
    private String urlBase;

    public void fazerLogin(Municipios municipio, Modulos modulo){
        String url = mapeamentoUrl(municipio, modulo);
        String loginFormatado = formatarCpf(loginRpa);

        Page page = pageManager.getPage();
        page.waitForLoadState();
        page.navigate(url);
        page.reload();
        page.waitForLoadState();

        if(!modulo.name().equalsIgnoreCase("civitas")){
            page.waitForLoadState();
            Locator linkCPF = page.getByRole(AriaRole.TAB,
                    new Page.GetByRoleOptions().setName("CPF"));

            try {
                linkCPF.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        );
                linkCPF.click();
                System.out.println("Link de CPF clicado");
            } catch (PlaywrightException e) {
                System.out.println("Link CPF não encontrado, seguindo sem clicar: " + e.getMessage());
            }
        }

        if(modulo.name().equalsIgnoreCase("nfsd")){
            var loginUsuario = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Usuário"));

            loginUsuario.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    );
            loginUsuario.click();
            loginUsuario.fill("");
            loginUsuario.pressSequentially(loginRpa, new Locator.PressSequentiallyOptions().setDelay(150));
            if(!(loginUsuario.inputValue().equalsIgnoreCase(loginFormatado))){
                loginUsuario.fill("");
                loginUsuario.pressSequentially(loginRpa, new Locator.PressSequentiallyOptions().setDelay(250));
                page.waitForTimeout(250);
            }

            page.getByRole(
                    AriaRole.TEXTBOX,
                    new Page.GetByRoleOptions().setName("Senha")
            ).pressSequentially(senhaRpa, new Locator.PressSequentiallyOptions().setDelay(50));
        } else{
            var login = page.getByRole(
                    AriaRole.TEXTBOX,
                    new Page.GetByRoleOptions().setName("Login"));

            login.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(1000));
            login.click();
            page.waitForLoadState();
            login.fill("");
            page.waitForTimeout(200);
            login.pressSequentially(loginRpa, new Locator.PressSequentiallyOptions().setDelay(150));
            if(!(login.inputValue().equalsIgnoreCase(loginFormatado))){
                login.fill("");
                login.pressSequentially(loginRpa, new Locator.PressSequentiallyOptions().setDelay(250));
                page.waitForTimeout(250);
            }
            page.getByRole(
                    AriaRole.TEXTBOX,
                    new Page.GetByRoleOptions().setName("Senha")
            ).pressSequentially(senhaRpa, new Locator.PressSequentiallyOptions().setDelay(50));
        }

        page.waitForTimeout(2000);
        if (!modulo.name().equalsIgnoreCase("civitas")){
            try{
                var botaoEntrar = page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Entrar")
                );
                botaoEntrar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        );
                botaoEntrar.click();
                page.waitForTimeout(5000);
            } catch (PlaywrightException e) {
                System.out.println("Botão 'Acessar' não encontrado, seguindo sem clicar: " + e.getMessage());

            }
            page.waitForLoadState();
        } else{
            try{
                var botaoAcessar = page.getByRole(
                        AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Acessar")
                );
                botaoAcessar.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(500)); //delay necessario para n bugar o click
                botaoAcessar.click();
            } catch (Exception e) {
                System.out.println("Botão 'Acessar' não encontrado, seguindo sem clicar: " + e.getMessage());
            }
            page.waitForTimeout(7000);
        }
    }

    public String mapeamentoUrl (Municipios municipio, Modulos modulo){
        String municipioSlug = municipio.name().toLowerCase().replaceAll("\\s", "");
        String moduloSlug = modulo.name().toLowerCase().replaceAll("\\s", "");

        boolean semSufixoPa = municipio == Municipios.ALTAMIRA
                || municipio == Municipios.BENEVIDES
                || municipio == Municipios.PARAGOMINAS
                || municipio == Municipios.PARAUAPEBAS
                || municipio == Municipios.VITORIADOXINGU;

        String dominio = semSufixoPa ? municipioSlug : municipioSlug + "-pa";


        String sufixo = (modulo == Modulos.CIVITAS
                || modulo == Modulos.REGULARIZE
                || modulo == Modulos.IPTU)
                ? "/#/login"
                : "/acessoSistema.jsf";

        return String.format(urlBase, dominio) + "/" + moduloSlug + sufixo;
    }

    private String formatarCpf(String cpf) {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
