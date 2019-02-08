package com.celvansystems.projetoamigoanimal.model;

import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Animal {

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
            String idUsuario = ConfiguracaoFirebase.getIdUsuario();
            DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            animalRef.child(idUsuario)
                    .child(getIdAnimal())
                    .setValue(this);
        } catch (Exception e){e.printStackTrace();}

        salvarAnimalPublico();
    }

    /**
     * salva anuncio na tabela anuncios
     */
    private void salvarAnimalPublico(){

        try {
            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("anuncios");

            anuncioRef.child(getUf())
                    .child(getCidade())
                    .child(getIdAnimal())
                    .setValue(this);
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * remove anuncio
     */
    public void remover(){

        try {
            String idUsuario = ConfiguracaoFirebase.getIdUsuario();

            DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(idUsuario)
                    .child(getIdAnimal());

            animalRef.removeValue();
        } catch (Exception e){e.printStackTrace();}

        removerAnimalPublico();
    }

    /**
     * remove anuncio da tabela anuncios
     */
    private void removerAnimalPublico(){

        try {
            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("anuncios")
                    .child(getUf())
                    .child(getCidade())
                    .child(getIdAnimal());


            anuncioRef.removeValue();
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
}
