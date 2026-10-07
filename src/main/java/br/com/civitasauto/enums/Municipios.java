package br.com.civitasauto.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Municipios {
    ALTAMIRA("Altamira", "SEMAF"),
    BENEVIDES("Benevides", "SEFIN"),
    ANANINDEUA ("Ananindeua", "SEGEF"),
    ABAETETUBA("Benevides", "SEFIN"),
    ACARA("Acará", "SEFIN"),
    ALENQUER("Alenquer", "SEFIN"),
    BRAGANCA("Bragança", "SEFIN"),
    BRASILNOVO("Brasil Novo", "SEMAF"),
    CAMETA("Cametá", "SEFIN"),
    DOMELISEU("Dom Elisou", "SEFAZ"),
    ELDORADODOCARAJAS("Eldorado do Carajás", "SEMFAZ"),
    IGARAPEMIRI("Igarapé Miri", "SEFIN"),
    ITUPIRANGA("Itupiranga", "SEGAF"),
    MARITUBA("Marituba", "SEOF"),
    MAEDORIO("Mãe do Rio", "SEFIN"),
    PORTEL("Portel", "SEGAF"),
    REDENCAO("Redenção", "SEFAZ"),
    SANTAIZABEL("Santa Izabel", "SEMAPF"),
    SANTANADOARAGUAIA("Santana do Araguaia", "SMTT"),
    SAOFELIXDOXINGU("São Félix do Xingu", "SEMFAZ"),
    SAOSEBASTIAODABOAVISTA("São Sebastião da Boa Vista", "SEMAF"),
    ULIANOPOLIS("Ulianópolis", "SEMAF"),
    OBIDOS("Óbidos", "SEMPOF"),
    XINGUARA("Xinguara", "SEFIN"),
    PARAGOMINAS("Paragominas", "SEMAFI"),
    PARAUAPEBAS("Parauapebas", "SEFAZ"),
    VITORIADOXINGU("Vitória do Xingu", "SEPOF"),
    TERRAALTA("Terra Alta", "aaaaa");


    private String municipios;
    private String instituicaoPortalEmpresa;

    Municipios (String municipios, String instituicaoPortalEmpresa){
        this.municipios = municipios;
        this.instituicaoPortalEmpresa = instituicaoPortalEmpresa;
    }

    public String getInstituicaoPortalEmpresa() {
        return instituicaoPortalEmpresa;
    }

    @JsonCreator
    public static Municipios fromString (String texto){
        for (Municipios municipios : Municipios.values()){
            if (municipios.municipios.replaceAll("\\s", "").equalsIgnoreCase(texto.replaceAll("\\s", ""))
                    ||
                    municipios.name().replaceAll("\\s", "").equalsIgnoreCase(texto.replaceAll("\\s", ""))){
                return municipios;
            }
        }
        throw new IllegalArgumentException("Nenhum município com esse nome econtrado -> " + texto);
    }

}
