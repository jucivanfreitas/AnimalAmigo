package com.celvansystems.projetoamigoanimal.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterMeusAnuncios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.RecyclerItemClickListener;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private RecyclerView recyclerAnuncios;
    private List<Animal> anuncios = new ArrayList<>();
    private AdapterMeusAnuncios adapterMeusAnuncios;
    private DatabaseReference anuncioUsuarioRef;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        inicializarComponentes();

        // configuracoes iniciais
        try {
            anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(ConfiguracaoFirebase.getIdUsuario());
        } catch (Exception e){e.printStackTrace();}

        //configurar recyclerview
        try {
            recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
            recyclerAnuncios.setHasFixedSize(true);

            adapterMeusAnuncios = new AdapterMeusAnuncios(anuncios);
            recyclerAnuncios.setAdapter(adapterMeusAnuncios);
        } catch (Exception e){e.printStackTrace();}

        //recupera anuncios para o usuario
        recuperarAnuncios();

        //adiciona evento de clique
        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

                Animal anuncioSelecionado = anuncios.get(position);
                anuncioSelecionado.remover();

                adapterMeusAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));
    }

    private void recuperarAnuncios(){

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Procurando anúncios")
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e){e.printStackTrace();}

        try {
            anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    anuncios.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        anuncios.add(ds.getValue(Animal.class));
                    }
                    Collections.reverse(anuncios);
                    adapterMeusAnuncios.notifyDataSetChanged();

                    dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    private void inicializarComponentes(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fabCadastrar = findViewById(R.id.fabcadastrar);

        fabCadastrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));
            }
        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerAnuncios = findViewById(R.id.recycle_meus_anuncios);
    }
}
