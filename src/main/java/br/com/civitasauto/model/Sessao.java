package br.com.civitasauto.model;

import com.microsoft.playwright.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class Sessao {

    @Autowired
    private PageManager pageManager;

    public void limparSessaoCompleta() {
        Page page = pageManager.getPage();

        page.context().clearCookies();
        page.context().clearPermissions();

        // page.evaluate("() => { localStorage.clear(); sessionStorage.clear(); }");
        page.setDefaultTimeout(3000);
        page.navigate("about:blank");

        System.out.println("Sessão completamente limpa para novo módulo");
    }
}
