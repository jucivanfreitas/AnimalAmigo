package com.celvansystems.projetoamigoanimal.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterAnuncios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button btnCidade, btnEspecie;
    private AdapterAnuncios adapterAnuncios;
    private List<Animal> listaAnuncios = new ArrayList<Animal>();
    private AlertDialog dialog;
    private DatabaseReference anunciosPublicosRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");
        inicializarComponentes();

        //configurar recyclerview
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);

        adapterAnuncios = new AdapterAnuncios(listaAnuncios, this);
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        FloatingActionButton fab = findViewById(R.id.fabcadastrar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //seleçioan menu de acordo com o modo logado ou não logado


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()){

            case R.id.menu_logar:
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;

            case R.id.menu_meus_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
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

        if(isUsuarioLogado()){
            menu.setGroupVisible(R.id.group_log_in, true);
        } else {
            menu.setGroupVisible(R.id.group_log_off, true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isUsuarioLogado(){
        //este metodo irá buscar informação se usuario está ou não logado
        return autenticacao.getCurrentUser() != null;
    }

    private void inicializarComponentes(){

        recyclerAnunciosPublicos = (RecyclerView) findViewById(R.id.recyclerAnuncios);

    }

    private void recuperarAnunciosPublicos(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Procurando anúncios")
                .setCancelable(false)
                .build();
        dialog.show();

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
    }

}
