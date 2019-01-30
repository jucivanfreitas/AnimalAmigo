package com.celvansystems.projetoamigoanimal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.celvansystems.projetoamigoanimal.R;

public class MeusAnunciosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabCadastrar = findViewById(R.id.fabcadastrar);
        fabCadastrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                abriCadastro();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void abriCadastro(){

        startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));



    }

}
