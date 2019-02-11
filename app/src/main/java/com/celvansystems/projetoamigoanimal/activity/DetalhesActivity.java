package com.celvansystems.projetoamigoanimal.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.Objects;

public class DetalhesActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView textNome;
    private Button btnVerTelefone;
    private Animal anuncioSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        //configurar toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhes");

        inicializarComponentes();

        //recupera anuncio para exibicao
        anuncioSelecionado = (Animal) getIntent().getSerializableExtra("anuncioSelecionado");
        if(anuncioSelecionado != null){
            textNome.setText(anuncioSelecionado.getNome());

            ImageListener imageListener =  new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };
            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);
        }
    }

    private void inicializarComponentes() {

        carouselView = findViewById(R.id.carouselView);
        textNome = findViewById(R.id.textNome);
        btnVerTelefone = findViewById(R.id.btnVerTelefone);

        btnVerTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                        anuncioSelecionado.getIdade(), null));
                // TODO: 10/02/2019 mudar getIdade para getTelefone 
                startActivity(i);
            }
        });
    }
}
