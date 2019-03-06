package com.celvansystems.projetoamigoanimal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.fragment.AnunciosFragment;
import com.celvansystems.projetoamigoanimal.fragment.CadastrarAnuncioFragment;
import com.celvansystems.projetoamigoanimal.fragment.DoacaoFragment;
import com.celvansystems.projetoamigoanimal.fragment.MensagensFragment;
import com.celvansystems.projetoamigoanimal.fragment.MeusAnunciosFragment;
import com.celvansystems.projetoamigoanimal.fragment.NotificacoesFragment;
import com.celvansystems.projetoamigoanimal.fragment.PerfilUsuarioFragment;
import com.celvansystems.projetoamigoanimal.fragment.ProcuradoFragment;
import com.celvansystems.projetoamigoanimal.fragment.SobreAppFragment;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private View layout;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private InterstitialAd mInterstitialAd;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();

        habilitaOpcoesNav();

        //Propagandas
        configuraAdMob();
        //configuraInterstitialAdTimer(Constantes.DELAY_INTERSTITIAL, Constantes.TIME_INTERSTITIAL);
    }

    private void inicializarComponentes(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        layout = findViewById(R.id.constraint_perfil_humano);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).commit();
    }

    private void carregaDadosUsuario() {

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.textview_nome_humano);
        TextView navEmail = headerView.findViewById(R.id.textView_email_cadastrado);

        FirebaseUser user = autenticacao.getCurrentUser();

        if(user != null) {
            navUsername.setText(user.getDisplayName());
            navEmail.setText(user.getEmail());
        } else {
            navUsername.setText(getString(R.string.usuario));
            navEmail.setText(getString(R.string.email));
        }

    }
    /**
     * configuracao da exibicao de intersticial periodico
     */
    /*private void configuraInterstitialAdTimer(int delay, int segundos) {


        prepareInterstitialAd();
        ScheduledExecutorService scheduler =
                Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {

                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        }
                        prepareInterstitialAd();
                    }
                });

            }
        }, delay, segundos, TimeUnit.SECONDS);
    }*/

    private void habilitaOpcoesNav() {

        Menu menuNav = navigationView.getMenu();

        MenuItem nav_minha_conta = menuNav.findItem(R.id.nav_minha_conta);
        MenuItem nav_config_notificacoes = menuNav.findItem(R.id.nav_config_notificacoes);
        MenuItem nav_meus_anuncios = menuNav.findItem(R.id.nav_meus_anuncios);
        MenuItem nav_pet_cad = menuNav.findItem(R.id.pet_cad);
        MenuItem nav_pet_adote = menuNav.findItem(R.id.pet_adote);
        MenuItem nav_doacao = menuNav.findItem(R.id.doacao);
        MenuItem nav_share_app = menuNav.findItem(R.id.nav_share_app);
        MenuItem nav_conversar = menuNav.findItem(R.id.nav_conversar);
        MenuItem nav_help = menuNav.findItem(R.id.nav_help);
        MenuItem nav_sair = menuNav.findItem(R.id.nav_sair);
        MenuItem nav_pet_procurado = menuNav.findItem(R.id.pet_procurado);

        if(ConfiguracaoFirebase.isUsuarioLogado()){
            nav_minha_conta.setEnabled(true);
            nav_minha_conta.setTitle(R.string.txt_minha_conta);

            nav_minha_conta.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.view_pager, new PerfilUsuarioFragment()).addToBackStack("tag").commit();
                    return false;
                }
            });

            nav_config_notificacoes.setEnabled(true);
            nav_meus_anuncios.setEnabled(true);
            nav_pet_cad.setEnabled(true);
            nav_pet_adote.setEnabled(true);
            nav_conversar.setEnabled(true);
            nav_sair.setEnabled(true);
            nav_pet_procurado.setEnabled(true);

        } else {
            nav_minha_conta.setTitle(R.string.txt_entrar);
            nav_minha_conta.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
            });
            nav_config_notificacoes.setEnabled(false);
            nav_meus_anuncios.setEnabled(false);
            nav_pet_cad.setEnabled(false);
            nav_pet_adote.setEnabled(false);
            nav_conversar.setEnabled(false);
            nav_sair.setEnabled(false);
            nav_pet_procurado.setEnabled(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if(adView!=null){
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adView!=null){
            adView.resume();
        }
        carregaDadosUsuario();
    }

    @Override
    protected void onDestroy() {
        if(adView!=null){
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //implementa fragmento
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_minha_conta) {
            // Handle the camera action
            fragmentTransaction.replace(R.id.view_pager, new PerfilUsuarioFragment()).addToBackStack("tag").commit();

            Toast.makeText(getApplicationContext(), "foi aberto a fragment Perfil.." +
                    " realizar proframação na fragment PerfilUser", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_config_notificacoes) {

            fragmentTransaction.replace(R.id.view_pager, new NotificacoesFragment()).addToBackStack("tag").commit();

            //    setContentView(R.layout.content_notificacoes);
            // TODO: 17/02/2019 programar ações da content_cotificações
            Toast.makeText(getApplicationContext(),
                    "implementar content configuração de notificação na activity dentro da pasta fragment",
                    Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_meus_anuncios) {

            //reuso da activit meus anuncios
            fragmentTransaction.replace(R.id.view_pager, new MeusAnunciosFragment()).addToBackStack("tag").commit();
            mostraInterstitialAd();

        } else if (id == R.id.pet_cad) {
            // fragment pet_cad cadastrar anuncio
            fragmentTransaction.replace(R.id.view_pager, new CadastrarAnuncioFragment()).addToBackStack("tag").commit();
        }
        else if (id == R.id.pet_adote) {
            //reuso da activity cadastrar anuncio
            fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).addToBackStack("tag").commit();
        } else if (id == R.id.doacao) {
            // implementar funções na activit doação.
            fragmentTransaction.replace(R.id.view_pager, new DoacaoFragment()).addToBackStack("tag").commit();
            Toast.makeText(getApplicationContext(),
                    "implementar content  de doações " +
                            " na activity dentro da pasta fragment." +
                            "essa fragmente terá apelos afim de comover " +
                            "o usuário a doar e links para " +
                            "receber doação na activity doação",
                    Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share_app) {

            Toast.makeText(getApplicationContext(),
                    "implementar função de compartilhar app para instalação",
                    Toast.LENGTH_SHORT).show();
            // TODO: 17/02/2019 programar ação do botão compartilahar app on clic compartilhar para instalação do app

        } else if (id == R.id.nav_conversar) {

            fragmentTransaction.replace(R.id.view_pager, new MensagensFragment()).addToBackStack("tag").commit();
            Toast.makeText(getApplicationContext(),
                    "criar e implementar fragment messenger com chat" +
                            " resumida a envio e recebimento de mensagens, " +
                            "semelhante ao zap",
                    Toast.LENGTH_SHORT).show();

            // TODO: 17/02/2019 implementar activity com conversar: funcionalidades basicas do zap e enviar e receber mensagens pelo app

        }else if (id == R.id.nav_help) {

            fragmentTransaction.replace(R.id.view_pager, new SobreAppFragment()).addToBackStack("tag").commit();
            Toast.makeText(getApplicationContext(),
                    "implementar fragment help " +
                            "sobre o APP",
                    Toast.LENGTH_SHORT).show();
            // TODO: 17/02/2019 imPlementar janela de ajudas sobre o app
        }else if (id == R.id.nav_sair) {

            Toast.makeText(getApplicationContext(),
                    "Volte sempre, precisamos de você!!!",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,LoginActivity.class));

            mostraInterstitialAd();

            autenticacao.signOut();
            LoginManager.getInstance().logOut();

            invalidateOptionsMenu(); //invalida o menu e chama o onPrepare de novo
            // TODO: 17/02/2019 implementar a função de invalidade do menu para retorno ao menus de usuarios não logado 
            finish();
            // TODO: 17/02/2019 imPlementar janela
        }else if (id == R.id.pet_procurado) {

            fragmentTransaction.replace(R.id.view_pager, new ProcuradoFragment()).addToBackStack("tag").commit();
            // TODO: 17/02/2019 programar activit cadastrar procurado
            Toast.makeText(getApplicationContext(),
                    "implementar fragment cadastro de procurados" +
                            "com objetivo de quem perdeu seu pet," +
                            " oferecer recompensas a quem encontrar seu pet" +
                            "ou não." +
                            "os cadastros realizado nesta seçao, irão notificar" +
                            "aqueles usuários proximos" +
                            "afimde ajudar a achar o pet" +
                            "perdido",
                    Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
            //teste Interstitial
            InterstitialAd mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            if(mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    // TODO: 23/02/2019 definir se mostra ou nao intersticial nos anuncios
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    Util.setSnackBar(layout, "intersticial failed");
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                    Util.setSnackBar(layout, "intersticial opened");
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                    Util.setSnackBar(layout, "intersticial on left");
                }

                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    Util.setSnackBar(layout, "intersticial closed");
                    prepareInterstitialAd();
                }
            });

            prepareInterstitialAd();

            //banner teste
            final AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.testeDeviceId))
                    .build();

            adView = findViewById(R.id.banner_main);
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
                    // covers the screen.
                    //Toast.makeText(getApplicationContext(), "opened", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                    //Toast.makeText(getApplicationContext(), "left", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return.
                    // to the app after tapping on an ad.
                    //Toast.makeText(getApplicationContext(), "closed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    private void prepareInterstitialAd(){

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interAdTestId));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void mostraInterstitialAd(){
        if(mInterstitialAd==null) {
            prepareInterstitialAd();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        prepareInterstitialAd();
    }
}
