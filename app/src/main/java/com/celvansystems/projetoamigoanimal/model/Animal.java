package com.celvansystems.projetoamigoanimal.model;

public class Animal {

    private String nome;
    private String uf;
    private String cidade;
    private String espécie;
    private String raca;
    private String descricao;
    private String idade;
    private String porte;
    private String sexo;

    public Animal (){}

    public Animal(String nome, String uf, String cidade, String espécie, String raca, String descricao, String idade, String porte, String sexo) {
        this.nome = nome;
        this.uf = uf;
        this.cidade = cidade;
        this.espécie = espécie;
        this.raca = raca;
        this.descricao = descricao;
        this.idade = idade;
        this.porte = porte;
        this.sexo = sexo;
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

    public String getEspécie() {
        return espécie;
    }

    public void setEspécie(String espécie) {
        this.espécie = espécie;
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
}
