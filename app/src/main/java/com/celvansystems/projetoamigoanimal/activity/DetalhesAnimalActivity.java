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
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
    private Button btnTeste;

    private View layout;

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
        if (anuncioSelecionado != null) {

            ImageListener imageListener = new ImageListener() {
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
        textNome = findViewById(R.id.txv_nome_meus_anuncios);
        textEspecie = findViewById(R.id.txv_especie);
        textGenero = findViewById(R.id.txv_genero);
        textIdade = findViewById(R.id.txv_idade);
        textPorte = findViewById(R.id.txv_porte);
        textEstado = findViewById(R.id.txv_estado);
        textCidade = findViewById(R.id.txv_cidade);
        textRaca = findViewById(R.id.txv_raca);
        textDescricao = findViewById(R.id.txv_descricao);
        btnTeste = findViewById(R.id.btnTeste);

        btnTeste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DetalhesAnimalActivity.this, SomeActivity.class));
                finish();
            }
        });
        layout = findViewById(R.id.linear_layout_detalhes_animal);
        Button btnVerTelefone = findViewById(R.id.btnVerTelefone);

        btnVerTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 18/02/2019 configurar para pegar o telefone cadastrado do usuario
                if (anuncioSelecionado.getDonoAnuncio() != null) {
                    Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",
                            anuncioSelecionado.getDonoAnuncio(), null));
                    // TODO: 10/02/2019 mudar getDonoanuncio para getTelefone
                    startActivity(i);
                } else {
                    Util.setSnackBar(layout, getString(R.string.telefone_nao_cadastrado));
                }
            }
        });

        configuraAdMob();
    }

    /**
     * método que configura as propagandas via AdMob
     */
    private void configuraAdMob() {

        //admob
        //MobileAds.initialize(this, String.valueOf(R.string.app_id));
        //teste do google
        MobileAds.initialize(getApplicationContext(), getString(R.string.mobileadsIdTeste));

        //AdView
        try {
            //banner teste
            final AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.testeDeviceId))
                    .build();

            AdView adView = findViewById(R.id.banner_detalhes_animal);
            //final AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    // Toast.makeText(this, "failed to load. " +
                    //        adRequest.getContentUrl(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return.
                    // to the app after tapping on an ad.
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
