package br.com.civitasauto.config;

import com.microsoft.playwright.Playwright;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PlaywrightConfigurations {
    @Bean
    public Playwright playwright (){
        return Playwright.create();
    }

}
