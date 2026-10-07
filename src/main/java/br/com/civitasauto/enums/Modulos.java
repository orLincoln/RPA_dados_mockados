package br.com.civitasauto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public enum Modulos {
    CIVITAS("Civitas",
            List.of("Sistema", "Administrativo", "Perfil do Usuário")),
    PORTALEMPRESA("Portalempresa",
            List.of("Sistema", "Configurações do Sistema", "Configurações de Perfil", "Perfil do Usuário")),
    NFSD("Nfsd",
            List.of("Administração", "Perfil do Usuário")),
    REGULARIZE("Regularize",
            List.of("Administração do Sistema", "Perfil do Usuário")),
    IPTU("Iptu",
            List.of("Sistema", "Administrativo", "Perfil do Usuário"));

    private final String modulos;
    private final List<String> caminhoMenu;
    Modulos(String modulos, List<String> caminhoMenu) {
        this.modulos = modulos;
        this.caminhoMenu = caminhoMenu;
    }

    public List<String> getCaminhoMenu() {
        return caminhoMenu;
    }

    @JsonCreator
    public static Modulos fromString (String texto){
        for (Modulos modulos : Modulos.values()){
            if(modulos.modulos.replaceAll("\\s", "").equalsIgnoreCase(texto.replaceAll("\\s", ""))
                    ||
                    modulos.name().replaceAll("\\s", "").equalsIgnoreCase(texto.replaceAll("\\s", ""))){
                return modulos;
            }
        }
        throw new IllegalArgumentException("Nenhum modulo com esse nome encontrado -> " + texto);
    }
}
