package br.com.civitasauto.enums;

public enum StatusDeferimento {
    DEFERIDO("Deferido"),
    INDEFERIDO("Indeferido");

    private String status;

    StatusDeferimento (String status){
        this.status = status;
    }

}
