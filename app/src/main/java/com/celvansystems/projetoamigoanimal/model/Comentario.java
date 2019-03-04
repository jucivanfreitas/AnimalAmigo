package com.celvansystems.projetoamigoanimal.model;

import java.io.Serializable;

public class Comentario  implements Serializable {

    private String usuarioId;
    private String texto;
    private String datahora;

    public Comentario(){}

    public Comentario(String usuarioId, String texto, String datahora) {
        this.usuarioId = usuarioId;
        this.texto = texto;
        this.datahora = datahora;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getDatahora() {
        return datahora;
    }

    public void setDatahora(String datahora) {
        this.datahora = datahora;
    }
}
