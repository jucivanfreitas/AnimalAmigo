package com.celvansystems.projetoamigoanimal.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.Objects;

public class DetalhesAnimalActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView textNome;
    private TextView textEspecie;
    private TextView textGenero;
    private TextView textIdade;
    private TextView textPorte;
    private TextView textEstado;
    private TextView textCidade;
    private TextView textRaca;
    private TextView textDescricao;

    private Animal anuncioSelecionado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_animal);

        //configurar toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.detalhes);

        inicializarComponentes();

        //recupera anuncio para exibicao
        anuncioSelecionado = (Animal) getIntent().getSerializableExtra("anuncioSelecionado");
        if(anuncioSelecionado != null){

            ImageListener imageListener =  new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };
            carouselView.setPageCount(anuncioSelecionado.getFotos().size());
            carouselView.setImageListener(imageListener);

            //características do animal
            textNome.setText(anuncioSelecionado.getNome());
            textEspecie.setText(anuncioSelecionado.getEspecie());
            textGenero.setText(anuncioSelecionado.getSexo());
            textIdade.setText(anuncioSelecionado.getIdade());
            textPorte.setText(anuncioSelecionado.getPorte());
            textEstado.setText(anuncioSelecionado.getUf());
            textCidade.setText(anuncioSelecionado.getCidade());
            textRaca.setText(anuncioSelecionado.getRaca());
            textDescricao.setText(anuncioSelecionado.getDescricao());
        }
    }

    private void inicializarComponentes() {

        carouselView = findViewById(R.id.carouselView);
        textNome = findViewById(R.id.txv_nome);
        textEspecie = findViewById(R.id.txv_especie);
        textGenero = findViewById(R.id.txv_genero);
        textIdade = findViewById(R.id.txv_idade);
        textPorte = findViewById(R.id.txv_porte);
        textEstado = findViewById(R.id.txv_estado);
        textCidade = findViewById(R.id.txv_cidade);
        textRaca = findViewById(R.id.txv_raca);
        textDescricao = findViewById(R.id.txv_descricao);

        Button btnVerTelefone = findViewById(R.id.btnVerTelefone);

        btnVerTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 18/02/2019 configurar para pegar o telefone cadastrado do usuario
                if(anuncioSelecionado.getDonoAnuncio()!= null) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                            anuncioSelecionado.getDonoAnuncio(), null));
                    // TODO: 10/02/2019 mudar getDonoanuncio para getTelefone
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "Telefone não cadastrado.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
