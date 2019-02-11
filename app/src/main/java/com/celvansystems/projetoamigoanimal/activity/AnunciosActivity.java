package com.celvansystems.projetoamigoanimal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
    private boolean filtrandoCidade, filtrandoEspecie;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("anuncios");
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

        //eventos de ckick
        /*recyclerAnunciosPublicos.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerAnunciosPublicos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Animal anuncioSelecionado = listaAnuncios.get(position);
                        Intent i = new Intent(AnunciosActivity.this, DetalhesActivity.class);
                        i.putExtra("anuncioSelecionado", anuncioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));*/

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

    // método chamado antes de mostrar o menu
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

    private void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnuncios);

        adView = findViewById(R.id.adView);

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
        } catch (Exception e){e.printStackTrace();}


        try {

            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    listaAnuncios.clear();
                    for(DataSnapshot estados: dataSnapshot.getChildren()){
                        for(DataSnapshot cidades: estados.getChildren()){
                            for(DataSnapshot anuncios: cidades.getChildren()){
                                Animal anuncio = anuncios.getValue(Animal.class);
                                listaAnuncios.add(anuncio);

                            }
                        }
                    }
                    Collections.reverse(listaAnuncios);
                    adapterAnuncios.notifyDataSetChanged();

                    dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    public void filtraPorEspecie(View view){

        filtrandoEspecie = true;

        AlertDialog.Builder dialogEspecie = new AlertDialog.Builder(this);
        dialogEspecie.setTitle("Selecione a espécie desejada");

        //configura o spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinnerEspecie = viewSpinner.findViewById(R.id.spinnerFiltro);
        final Spinner spinnerCidade = viewSpinner.findViewById(R.id.spinnerFiltro2);
        spinnerCidade.setVisibility(View.GONE);

        String [] especies = Util.getEspecies(getApplicationContext());

        //especies
        ArrayAdapter<String> adapterEspecies = new ArrayAdapter<>(this, simple_spinner_item, especies);
        adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(adapterEspecies);

        dialogEspecie.setView(viewSpinner);


        dialogEspecie.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String filtroEspecie = spinnerEspecie.getSelectedItem().toString();
                recuperarAnunciosPorEspecie(filtroEspecie);
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

    public void recuperarAnunciosPorEspecie(final String especie){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Procurando anúncios da espécie " + especie)
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for(DataSnapshot estados: dataSnapshot.getChildren()){
                    for(DataSnapshot cidades: estados.getChildren()){
                        if(!filtrandoCidade) {
                            for (DataSnapshot anuncios : cidades.getChildren()) {

                                Animal anuncio = anuncios.getValue(Animal.class);
                                if(anuncio != null) {

                                    if (anuncio.getEspecie().equalsIgnoreCase(especie)) {
                                        listaAnuncios.add(anuncio);
                                    }
                                }
                            }
                        } else {
                            if(cidades.getKey().equalsIgnoreCase(btnCidade.getText().toString())) {
                                for (DataSnapshot anuncios : cidades.getChildren()) {

                                    Animal anuncio = anuncios.getValue(Animal.class);
                                    if(anuncio != null) {

                                        if (anuncio.getEspecie().equalsIgnoreCase(especie)) {
                                            listaAnuncios.add(anuncio);
                                        }
                                    }
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

        String[] estados = Util.getEstadosJSON(this);

        spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);
        spinnerCidade = viewSpinner.findViewById(R.id.spinnerFiltro2);

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

                recuperarAnunciosPorCidade(filtroEstado, filtroCidade);

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

    /**
     * recupera anuncios por estado e cidade
     * @param estado uf
     * @param cidade cidade
     */
    public void recuperarAnunciosPorCidade(String estado, final String cidade){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Procurando anúncios em " + estado+"/"+cidade)
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear();
                for(DataSnapshot estados: dataSnapshot.getChildren()){
                    for(DataSnapshot cidades: estados.getChildren()){
                        if(cidades.getKey().equalsIgnoreCase(cidade)) {
                            for (DataSnapshot anuncios : cidades.getChildren()) {

                                if(!filtrandoEspecie) {
                                    Animal anuncio = anuncios.getValue(Animal.class);

                                    if(anuncio!= null) {
                                        if (anuncio.getCidade().equalsIgnoreCase(cidade)) {
                                            listaAnuncios.add(anuncio);
                                        }
                                    }
                                } else {
                                    Animal anuncio = anuncios.getValue(Animal.class);
                                    if(anuncio!= null) {

                                        if (anuncio.getEspecie().equalsIgnoreCase(btnEspecie.getText().toString())) {
                                            if (anuncio.getCidade().equalsIgnoreCase(cidade)) {
                                                listaAnuncios.add(anuncio);
                                            }
                                        }
                                    }
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
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
