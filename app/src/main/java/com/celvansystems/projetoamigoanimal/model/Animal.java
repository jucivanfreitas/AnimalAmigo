package com.celvansystems.projetoamigoanimal.model;

import android.util.Log;

import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
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
    private List<String> fotos;

    public Animal (){

        DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_animais");
        setIdAnimal(animalRef.push().getKey());
        Log.d("INFO: ", getIdAnimal());

        //fins de teste. Falta implementar na classe CadastroAnuncioActivity
        this.setUf("AC");
        this.setCidade("Acrelândia");
    }

    public void salvar(){

        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_animais");

        animalRef.child(idUsuario)
                .child(getIdAnimal())
                .setValue(this);
        salvarAnimalPublico();
    }

    public void salvarAnimalPublico(){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");

        anuncioRef.child(getUf())
                .child(getCidade())
                .child(getIdAnimal())
                .setValue(this);
    }

    public void remover(){
        String idUsuario = ConfiguracaoFirebase.getIdUsuario();

        DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                .child("meus_animais")
                .child(idUsuario)
                .child(getIdAnimal());

        animalRef.removeValue();
        removerAnimalPublico();
    }
/////////////////////////////////////////////
    /////////////////////////////////////////
    public void removerAnimalPublico(){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios")
                .child(getUf())
                .child(getCidade())
                .child(getIdAnimal());

        anuncioRef.removeValue();
    }


    public String getIdAnimal() {
        return idAnimal;
    }

    public void setIdAnimal(String idAnimal) {
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

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}
