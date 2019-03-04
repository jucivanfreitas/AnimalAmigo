package com.celvansystems.projetoamigoanimal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.celvansystems.projetoamigoanimal.R;

import java.util.Objects;

public class ComentariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //configurar toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.comentarios_titulo);

    }
}
