package com.celvansystems.projetoamigoanimal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterComentarios;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.celvansystems.projetoamigoanimal.model.Comentario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComentariosActivity extends AppCompatActivity {

    private Animal anuncioSelecionado;
    private AdapterComentarios adapterComentarios;
    private List<Comentario> listaComentarios = new ArrayList<>();

    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //configurar toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.comentarios_titulo);

        layout = findViewById(R.id.constraint_comentarios);

        RecyclerView recyclercomentarios = findViewById(R.id.recyclerComentarios);
        recyclercomentarios.setItemAnimator(null);
        anuncioSelecionado = (Animal) getIntent().getSerializableExtra("anuncioSelecionado");

        if(anuncioSelecionado != null){

            listaComentarios = anuncioSelecionado.getListaComentarios();

        //configurar recyclerview
        try {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(this);

            recyclercomentarios.setLayoutManager(lm);

            recyclercomentarios.setHasFixedSize(true);

            adapterComentarios = new AdapterComentarios(listaComentarios);

            recyclercomentarios.setAdapter(adapterComentarios);

        } catch (Exception e){e.printStackTrace();}



        }
    }
}
