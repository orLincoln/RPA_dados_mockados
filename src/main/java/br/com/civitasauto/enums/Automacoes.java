package br.com.civitasauto.enums;

public enum Automacoes {
    ATRIBUICAO_DE_PERFIL("Atribuição de Perfil"),
    GERAR_GUIA ("Gerar Guia"),
    RECALCULAR_GUIA("Recalcular Guia"),
    CADASTRO_PF("Cadastro Pessoa Física"),
    CADASTRO_PJ("Cadastro Pessoa Jurídica"),
    CADASTRO_AUTONOMO("Cadastro Autônomo"),
    EMISSAO_NOTA("Emissão de Nota Fiscal");

    private String automacoes;

    Automacoes (String automacoes){
        this.automacoes = automacoes;
    }
}
