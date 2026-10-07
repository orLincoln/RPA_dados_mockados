package br.com.civitasauto.model;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class PageManager {
  private final Playwright playwright;
  private Browser browser;
  private Page page;
  private final String instanceId;

  public PageManager(Playwright playwright) {
    this.playwright = playwright;
    this.instanceId = UUID.randomUUID().toString().substring(0, 8);
    System.out.println("🟡 NOVO PageManager CRIADO! ID: " + instanceId);
  }

  private Browser getBrowser() {
    if (browser == null || !browser.isConnected()) {
      browser = playwright.chromium().launch(
              new BrowserType.LaunchOptions().setHeadless(false)
      );
      System.out.println("🟢 Novo browser lançado.");
    }
    return browser;
  }

  public Page getPage() {
    if (page == null || page.isClosed()) {
      page = getBrowser().newPage();
      System.out.println("✅ Nova página criada (ID: " + page.hashCode() + ")");
    }
    return page;
  }

  public String descreverTela() {
    try {
      if (page == null || page.isClosed()) {
        return "Tela: indisponível";
      }
      return "Tela: " + page.title() + " | URL: " + page.url();
    } catch (Exception e) {
      return "Tela: indisponível";
    }
  }

  public void resetPage() {
    page.close();
    page = getBrowser().newPage();
  }

  public void closePage() {
    if (page != null) {
      page.close();
    }
  }

  public void closeBrowser() {
    closePage();
    try {
      if (browser != null) {
        browser.close();
      }
      System.out.println("🔴 Browser fechado.");
    } catch (Exception e) {
      System.out.println("⚠️ Falha ao fechar o browser: " + e.getMessage());
    }
  }
}
