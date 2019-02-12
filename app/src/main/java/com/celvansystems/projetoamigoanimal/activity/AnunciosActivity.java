package com.celvansystems.projetoamigoanimal.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterAnuncios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;


public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button btnCidade, btnEspecie;
    private AdapterAnuncios adapterAnuncios;
    private List<Animal> listaAnuncios = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference anunciosPublicosRef;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private ArrayAdapter adapterCidades;
    private String [] cidades;
    private boolean filtrandoEstado, filtrandoCidade, filtrandoEspecie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            //anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
            //        .child("anuncios");
            anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
        } catch (Exception e){e.printStackTrace();}

        inicializarComponentes();

        //configurar recyclerview
        try {
            recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
            recyclerAnunciosPublicos.setHasFixedSize(true);

            adapterAnuncios = new AdapterAnuncios(listaAnuncios);
            recyclerAnunciosPublicos.setAdapter(adapterAnuncios);
        } catch (Exception e){e.printStackTrace();}

        recuperarAnunciosPublicos();

        //admob
        MobileAds.initialize(this, "ca-app-pub-6718857112988900~5442725100");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
     *seleciona menu de acordo com o modo logado ou não logado
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()){

            case R.id.menu_logar:
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                break;

            case R.id.menu_meus_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;

            case R.id.menu_configuracoes:
                //startActivity(new Intent(getApplicationContext(),ConfiguracoesActivity.class));
                break;

            case R.id.menu_sair:
                autenticacao.signOut();
                invalidateOptionsMenu(); //invalida o menu e chama o onPrepare de novo
                break;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * método chamado antes de mostrar o menu
     * @param menu menu
     * @return boolean
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        try {
            if(ConfiguracaoFirebase.isUsuarioLogado()){
                menu.setGroupVisible(R.id.group_log_in, true);
            } else {
                menu.setGroupVisible(R.id.group_log_off, true);
            }
        } catch (Exception e){e.printStackTrace();}

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * inicializa os componentes da view
     */
    private void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnuncios);
        btnCidade = findViewById(R.id.btnCidade);
        btnEspecie = findViewById(R.id.btnEspecie);

        //AdView
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Toast.makeText(getApplicationContext(), "loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                Toast.makeText(getApplicationContext(), "falied to load", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                Toast.makeText(getApplicationContext(), "opened", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                Toast.makeText(getApplicationContext(), "closed", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void recuperarAnunciosPublicos(){

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Procurando anúncios")
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e){e.printStackTrace();
        }

        try {
            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaAnuncios.clear();
                    for(DataSnapshot users: dataSnapshot.getChildren()){
                        for(DataSnapshot animais: users.getChildren()){
                            listaAnuncios.add(animais.getValue(Animal.class));
                        }
                    }
                    Collections.reverse(listaAnuncios);
                    adapterAnuncios.notifyDataSetChanged();

                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),
                            "Falha ao acessar anúncios" + databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.d("INFO1: ", databaseError.getMessage());
                    dialog.dismiss();

                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * filtra por espécie
     * @param view view
     */
    public void filtraPorEspecie(View view){

        filtrandoEspecie = true;

        AlertDialog.Builder dialogEspecie = new AlertDialog.Builder(this);
        dialogEspecie.setTitle("Selecione a espécie desejada");

        //configura o spinner
        @SuppressLint("InflateParams")
        View viewSpinnerEspecie = getLayoutInflater().inflate(R.layout.dialog_spinner_especie, null);
        final Spinner spinnerEspecie = viewSpinnerEspecie.findViewById(R.id.spinnerFiltroEspecie);

        String [] especies = Util.getEspecies(getApplicationContext());

        //especies
        ArrayAdapter<String> adapterEspecies = new ArrayAdapter<>(this, simple_spinner_item, especies);
        adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(adapterEspecies);

        dialogEspecie.setView(viewSpinnerEspecie
        );

        dialogEspecie.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String filtroEspecie = spinnerEspecie.getSelectedItem().toString();

                String cidadeBotao = btnCidade.getText().toString();

                if(!cidadeBotao.equalsIgnoreCase("Todas") &&
                        cidadeBotao.equalsIgnoreCase("cidade")) {
                    recuperarAnunciosFiltro(null, null,filtroEspecie);
                } else {
                    recuperarAnunciosFiltro(null, cidadeBotao,filtroEspecie);
                }
                try {
                    btnEspecie = findViewById(R.id.btnEspecie);
                    btnEspecie.setText(filtroEspecie);
                }catch (Exception e){e.printStackTrace();}
            }
        });

        dialogEspecie.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogEspecie.create();
        dialog.show();
    }

     /**
     *
     * @param view view
     */
    public void filtraPorCidade(View view){

        filtrandoCidade = true;

        AlertDialog.Builder dialogCidade = new AlertDialog.Builder(this);
        dialogCidade.setTitle("Selecione a cidade desejada");


        //configura o spinner
        @SuppressLint("InflateParams")
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        String[] estados = Util.getEstadosJSON(this);

        spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltroEstado);
        spinnerCidade = viewSpinner.findViewById(R.id.spinnerFiltroCidade);

        spinnerCidade.setVisibility(View.VISIBLE);
        cidades = Util.getCidadesJSON("AC", this);

        //cidades
        adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, cidades);
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, simple_spinner_item, estados);
        adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapterEstados);
        spinnerCidade.setAdapter(adapterCidades);

        dialogCidade.setView(viewSpinner);

        dialogCidade.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String filtroEstado = spinnerEstado.getSelectedItem().toString();
                String filtroCidade = spinnerCidade.getSelectedItem().toString();

                //recuperarAnunciosPorCidade(filtroEstado, filtroCidade);
                String especieBotao = btnEspecie.getText().toString();

                if(!especieBotao.equalsIgnoreCase("Todas") &&
                        !especieBotao.equalsIgnoreCase("espécie")) {
                    recuperarAnunciosFiltro(filtroEstado, filtroCidade, btnEspecie.getText().toString());
                } else {
                    recuperarAnunciosFiltro(filtroEstado, filtroCidade, null);
                }

                try {
                    btnCidade = findViewById(R.id.btnCidade);
                    btnCidade.setText(filtroCidade);
                }catch (Exception e){e.printStackTrace();}
            }
        });

        dialogCidade.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = dialogCidade.create();
        dialog.show();

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    String ufSelecionado = spinnerEstado.getItemAtPosition(position).toString();
                    cidades = Util.getCidadesJSON(ufSelecionado, getApplicationContext());
                    setAdapterSpinnerCidade();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setAdapterSpinnerCidade(){

        try {
            adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, cidades);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();
        } catch (Exception e){e.printStackTrace();}
    }

    public void recuperarAnunciosFiltro(final String estado, final String cidade, final String especie) {

        filtrandoEstado = false;
        filtrandoCidade = false;
        filtrandoEspecie = false;
        //cidade
        if (cidade != null) {
            if (!cidade.equalsIgnoreCase("Todas")) {
                btnCidade.setText(cidade);
                filtrandoCidade = true;
                filtrandoEstado = false;
            }
            //estado
            else if (estado != null) {
                if (!estado.equalsIgnoreCase("Todos")) {
                    btnCidade.setText(estado);
                    filtrandoEstado = true;
                    filtrandoCidade = false;
                }
            }
        }
        //especie
        if (especie != null) {
            if (!especie.equalsIgnoreCase("Todas")) {
                //btnEspecie = this.<Button>findViewById(R.id.btnEspecie);
                btnEspecie.setText(especie);
                filtrandoEspecie = true;
            }
        }

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Procurando anúncios da espécie " + especie)
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaAnuncios.clear();

                for (DataSnapshot users : dataSnapshot.getChildren()) {
                    for (DataSnapshot animais : users.getChildren()) {

                        Animal animal = animais.getValue(Animal.class);

                        if (animal != null) {

                            if(!filtrandoEspecie && !filtrandoCidade && !filtrandoEstado) {
                                listaAnuncios.add(animal);
                            } else if (filtrandoEspecie && !filtrandoCidade && !filtrandoEstado) {
                                if(animal.getEspecie().equalsIgnoreCase(especie)){
                                    listaAnuncios.add(animal);
                                }
                            } else if (filtrandoEspecie && filtrandoCidade && !filtrandoEstado) {
                                if(animal.getEspecie().equalsIgnoreCase(especie) &&
                                        animal.getCidade().equalsIgnoreCase(cidade) &&
                                        animal.getUf().equalsIgnoreCase(estado)){
                                    listaAnuncios.add(animal);
                                }
                            } else if (filtrandoEspecie && !filtrandoCidade && filtrandoEstado) {
                                if(animal.getEspecie().equalsIgnoreCase(especie) &&
                                        animal.getUf().equalsIgnoreCase(estado)){
                                    listaAnuncios.add(animal);
                                }
                            } else if(!filtrandoEspecie && filtrandoCidade && !filtrandoEstado) {
                                if(animal.getCidade().equalsIgnoreCase(cidade) &&
                                        animal.getUf().equalsIgnoreCase(estado)){
                                    listaAnuncios.add(animal);
                                }
                            } else if(!filtrandoEspecie && !filtrandoCidade && filtrandoEstado) {
                                if(animal.getUf().equalsIgnoreCase(estado)){
                                    listaAnuncios.add(animal);
                                }
                            }
                        }
                    }
                }
                Collections.reverse(listaAnuncios);
                adapterAnuncios.notifyDataSetChanged();

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}