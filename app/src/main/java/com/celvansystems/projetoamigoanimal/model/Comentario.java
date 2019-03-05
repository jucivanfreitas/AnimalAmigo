package com.celvansystems.projetoamigoanimal.model;

import java.io.Serializable;

public class Comentario  implements Serializable {

    private Usuario usuario;
    private String texto;
    private String datahora;

    public Comentario() {
    }

    public Comentario(Usuario usuario, String texto, String datahora) {
        this.usuario = usuario;
        this.texto = texto;
        this.datahora = datahora;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

