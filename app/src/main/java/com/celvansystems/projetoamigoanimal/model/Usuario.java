package com.celvansystems.projetoamigoanimal.model;

import android.support.annotation.NonNull;

import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class Usuario implements Serializable {

    private String id;
    private String nome;
    private String email;
    private String telefone;
    private String pais;
    private String uf;
    private String cidade;
    private String latitude;
    private String longitude;
    private String foto;
    private String genero;

    /**
     * salva usuario
     */
    public void salvar() {

        try {
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("usuarios");

            usuarioRef.child(getId())
                    .setValue(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * remove anuncio
     */
    public void remover() {

        try {
            DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("usuarios")
                    .child(getId());


            usuarioRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    apagarFotoStorage();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * metodo auxilicar que apaga as fotos de um animal
     */
    private void apagarFotoStorage() {

        try {
            StorageReference storage = ConfiguracaoFirebase.getFirebaseStorage();
            StorageReference imagemUsuario = storage
                    .child("imagens")
                    .child("usuarios")
                    .child(getId());

            imagemUsuario.child("imagem").delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public boolean isLoginCompleto() {

        boolean retorno = false;

        if (nome != null && telefone != null &&
                foto != null && cidade != null) {
            if (!nome.isEmpty() && !telefone.isEmpty() &&
                    !foto.isEmpty() && !cidade.isEmpty()) {
                retorno = true;
            }
        }
        return retorno;
    }
}
