package com.celvansystems.projetoamigoanimal.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class AnunciosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private Button btnCidade, btnEspecie;
    private AdapterAnuncios adapterAnuncios;
    private List<Animal> listaAnuncios = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference anunciosPublicosRef;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private ArrayAdapter adapterCidades;
    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*--TENTANDO ISERIR

        setContentView(R.layout.content_auncios);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inicializarComponentes();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(on);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
*/
        //FIM DRAER LAYER

        setContentView(R.layout.activity_anuncios);

        //configurações iniciais
        inicializarComponentes();
        recuperarAnunciosPublicos();
    }

    /**
     * inicializa os componentes da view
     */
    private void inicializarComponentes(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layout = findViewById(R.id.anuncios_coordinatorlayout);

        try {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
        } catch (Exception e){e.printStackTrace();}

        RecyclerView recyclerAnunciosPublicos;
        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnuncios);
        btnCidade = findViewById(R.id.btnCidade);
        btnEspecie = findViewById(R.id.btnEspecie);

        //configurar recyclerview
        try {
            recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
            recyclerAnunciosPublicos.setHasFixedSize(true);

            adapterAnuncios = new AdapterAnuncios(listaAnuncios);
            recyclerAnunciosPublicos.setAdapter(adapterAnuncios);
        } catch (Exception e){e.printStackTrace();}

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Procurando anúncios")
                    .setCancelable(true)
                    .build();
        } catch (Exception e){e.printStackTrace();}

        //propagandas
        configuraAdMob();
    }

    /**
     * método que configura as propagandas via AdMob
     */
    private void configuraAdMob() {

        //admob
        //MobileAds.initialize(this, String.valueOf(R.string.app_id));
        //teste do google
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        //AdView
        try {
            //teste
            final AdRequest adRequest = new AdRequest.Builder().addTestDevice("33BE2250B43518CCDA7DE426D04EE231").build();

            final AdView adView = findViewById(R.id.adView);
            //final AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Toast.makeText(getApplicationContext(), "loaded. " +
                            adRequest.getContentUrl(), Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    // Toast.makeText(getApplicationContext(), "failed to load. " +
                    //        adRequest.getContentUrl(), Toast.LENGTH_SHORT).show();
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
                    // Code to be executed when when the user is about to return.
                    // to the app after tapping on an ad.
                    Toast.makeText(getApplicationContext(), "closed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * exibe os anúncios públicos
     */
    private void recuperarAnunciosPublicos(){

        dialog.show();

        try {
            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaAnuncios.clear();
                    for(DataSnapshot animais: dataSnapshot.getChildren()){
                        listaAnuncios.add(animais.getValue(Animal.class));
                    }
                    Collections.reverse(listaAnuncios);
                    adapterAnuncios.notifyDataSetChanged();

                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AnunciosActivity.this,
                            "Falha ao acessar anúncios. Verifique sua conexão à internet." + databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            dialog.dismiss();
            Toast.makeText(AnunciosActivity.this,
                    "Falha ao acessar anúncios. Verifique sua conexão à internet." + e.getMessage(),
                    Toast.LENGTH_SHORT).show();}
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

            case R.id.menu_doacao:
                startActivity(new Intent(getApplicationContext(),DoacaoActivity.class));
                break;
            case R.id.menu_meus_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;

            case R.id.menu_configuracoes:
                startActivity(new Intent(getApplicationContext(),PerfilHumanoActivity.class));
                break;

            case R.id.menu_sair:
                autenticacao.signOut();
                LoginManager.getInstance().logOut(); //logout do facebook
                invalidateOptionsMenu(); //invalida o menu e chama o onPrepare de novo

                //google
                // Configure sign-in to request the user's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...
                            }
                        });
                break;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Verifica se há conta do google logada.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            Log.d("INFO40", "Usuário logado com google");
            Util.setSnackBar(layout, "Usuário logado pelo google");
        } else {
            // Verifica se há conta do facebook logada
            AccessToken token = AccessToken.getCurrentAccessToken();

            if (token != null) {
                Log.d("INFO40", "Usuário logado pelo facebook");
                Util.setSnackBar(layout, "Usuário logado pelo facebook");
            }
        }
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
     * filtra por espécie
     * @param view view
     */
    public void filtraPorEspecie(View view){

        try {
            AlertDialog.Builder dialogEspecie = new AlertDialog.Builder(this);
            dialogEspecie.setTitle(getString(R.string.selecione_especie));

            //configura o spinner
            @SuppressLint("InflateParams")
            View viewSpinnerEspecie = getLayoutInflater().inflate(R.layout.dialog_spinner_especie, null);
            final Spinner spinnerEspecie = viewSpinnerEspecie.findViewById(R.id.spinnerFiltroEspecie);

            ArrayList<String> especiesLista = Util.getEspeciesLista(getApplicationContext());

            //especies
            ArrayAdapter<String> adapterEspecies = new ArrayAdapter<>(this, simple_spinner_item, especiesLista);
            adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEspecie.setAdapter(adapterEspecies);

            dialogEspecie.setView(viewSpinnerEspecie
            );

            dialogEspecie.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String filtroEspecie = spinnerEspecie.getSelectedItem().toString();
                    String cidadeBotao = btnCidade.getText().toString();

                    recuperarAnunciosFiltro(null, cidadeBotao, filtroEspecie);
                }
            });

            dialogEspecie.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogEspecie.create();
            dialog.show();
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     *
     * @param view view
     */
    public void filtraPorCidade(View view){

        try {
            AlertDialog.Builder dialogCidade = new AlertDialog.Builder(this);
            dialogCidade.setTitle(getString(R.string.selecione_cidade));

            //configura o spinner
            @SuppressLint("InflateParams")
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);

            ArrayList<String> estadosLista = Util.getEstadosLista(getApplicationContext());

            spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltroEstado);
            spinnerCidade = viewSpinner.findViewById(R.id.spinnerFiltroCidade);

            spinnerCidade.setVisibility(View.VISIBLE);
            ArrayList<String> cidadesLista = Util.getCidadesLista("AC", getApplicationContext());

            //cidades
            adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, cidadesLista);
            ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, simple_spinner_item, estadosLista);
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstado.setAdapter(adapterEstados);
            spinnerCidade.setAdapter(adapterCidades);

            dialogCidade.setView(viewSpinner);
            dialogCidade.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String filtroEstado = getString(R.string.todos);
                    String filtroCidade = getString(R.string.todas);
                    String filtroEspecie;

                    if(spinnerEstado.getSelectedItem()!=null) {

                        filtroEstado = spinnerEstado.getSelectedItem().toString();
                    }
                    if(spinnerCidade.getSelectedItem()!=null) {

                        filtroCidade = spinnerCidade.getSelectedItem().toString();
                    }

                    filtroEspecie = btnEspecie.getText().toString();

                    recuperarAnunciosFiltro(filtroEstado, filtroCidade, filtroEspecie);
                }
            });

            dialogCidade.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
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
                        setAdapterSpinnerCidade();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    private void setAdapterSpinnerCidade(){

        try {
            ArrayList<String> cidadesLista = new ArrayList<>();
            cidadesLista.add(getString(R.string.todas));
            String estadoSelecionado = spinnerEstado.getSelectedItem().toString();

            if(!estadoSelecionado.equalsIgnoreCase(getString(R.string.todos))) {
                cidadesLista = Util.getCidadesLista(estadoSelecionado, getApplicationContext());
            }
            adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, cidadesLista);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();

        } catch (Exception e){e.printStackTrace();}
    }

    public void recuperarAnunciosFiltro(final String estado, final String cidade, final String especie) {

        try {
            /*dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.procurando_anuncios)
                    .setCancelable(true)
                    .build();*/
            dialog.show();

            //cidade
            if (cidade!=null && !cidade.equalsIgnoreCase(getString(R.string.todas))) {
                btnCidade.setText(cidade);
            }
            //estado
            else if (estado != null){
                btnCidade.setText(estado);
            }
            //especie
            if (especie != null) {
                btnEspecie.setText(especie);
            }

            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaAnuncios.clear();

                    for (DataSnapshot animais : dataSnapshot.getChildren()) {

                        Animal animal = animais.getValue(Animal.class);
                        //Log.d("INFO5: ", animal.getCidade()+"/"+animal.getUf());

                        if (animal != null) {

                            String textoBotaoCidade = btnCidade.getText().toString();
                            String textoBotaoEspecie = btnEspecie.getText().toString();

                            //sem filtro
                            if((textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos)) ||
                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) && (
                                            textoBotaoEspecie.equalsIgnoreCase(getString(R.string.especie)) ||
                                                    textoBotaoEspecie.equalsIgnoreCase(getString(R.string.todas))))){
                                //Log.d("INFO6: ", animal.getNome()+"/ "+animal.getCidade()+"/"+animal.getUf());
                                listaAnuncios.add(animal);

                            } else {
                                //Log.d("INFOBAD: ", animal.getCidade()+"/"+animal.getUf());
                                //sem espécie
                                if (textoBotaoEspecie.equalsIgnoreCase(getString(R.string.especie)) ||
                                        textoBotaoEspecie.equalsIgnoreCase(getString(R.string.todas))) {

                                    if (textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) ||
                                            textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                            textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos))) {

                                        listaAnuncios.add(animal);

                                    } else {
                                        if (textoBotaoCidade.equalsIgnoreCase(animal.getCidade()) ||
                                                textoBotaoCidade.equalsIgnoreCase(animal.getUf())) {
                                            listaAnuncios.add(animal);
                                        }
                                    }
                                    //com espécie
                                } else {
                                    if (textoBotaoEspecie.equalsIgnoreCase(animal.getEspecie())) {

                                        //estado
                                        if (textoBotaoCidade.length()== 2) {

                                            if(textoBotaoCidade.equalsIgnoreCase(animal.getUf())) {
                                                listaAnuncios.add(animal);
                                            }
                                            //cidade
                                        } else {

                                            if (textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) ||
                                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos))) {
                                                listaAnuncios.add(animal);
                                            } else if(textoBotaoCidade.equalsIgnoreCase(animal.getCidade())) {
                                                listaAnuncios.add(animal);
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
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //DRAWER




        return false;
    }
}