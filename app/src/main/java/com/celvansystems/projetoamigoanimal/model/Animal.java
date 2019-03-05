package com.celvansystems.projetoamigoanimal.model;

import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Animal implements Serializable {

    private String donoAnuncio;
    private String idAnimal;
    private String nome;
    private String uf;
    private String cidade;
    private String especie;
    private String raca;
    private String descricao;
    private String idade;
    private String porte;
    private String sexo;
    private String dataCadastro;
    private List<String> fotos;
    private List<String> curtidas;
    private List<Comentario> listaComentarios;

    public Animal (){

        try {
            DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
            setIdAnimal(animalRef.push().getKey());

            // configuraçao da data atual do Brasil
            setDataCadastro(Util.getDataAtualBrasil());
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * salva anuncio
     */
    public void salvar(){

        try {
            DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            animalRef.child(getIdAnimal())
                    .setValue(this);
        } catch (Exception e){e.printStackTrace();}

    }

    /**
     * remove anuncio
     */
    public void remover(){

        try {
            DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(getIdAnimal());

            animalRef.removeValue();
        } catch (Exception e){e.printStackTrace();}
    }

    public String getIdAnimal() {
        return idAnimal;
    }

    private void setIdAnimal(String idAnimal) {
        this.idAnimal = idAnimal;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    private void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public String getDonoAnuncio() {
        return donoAnuncio;
    }

    public void setDonoAnuncio(String donoAnuncio) {
        this.donoAnuncio = donoAnuncio;
    }

    public List<String> getCurtidas() {
        return curtidas;
    }

    public void setCurtidas(List<String> curtidas) {
        this.curtidas = curtidas;
    }

    public List<Comentario> getListaComentarios() {
        return listaComentarios;
    }

    public void setListaComentarios(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }
}
