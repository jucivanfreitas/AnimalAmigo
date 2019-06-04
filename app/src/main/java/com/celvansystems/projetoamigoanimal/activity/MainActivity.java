package com.celvansystems.projetoamigoanimal.activity;

import android.content.Intent;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.TextView;
import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.fragment.AnunciosFragment;
import com.celvansystems.projetoamigoanimal.fragment.CadastrarAnuncioFragment;
import com.celvansystems.projetoamigoanimal.fragment.DoacaoFragment;
import com.celvansystems.projetoamigoanimal.fragment.MeusAnunciosFragment;
import com.celvansystems.projetoamigoanimal.fragment.PerfilUsuarioFragment;
import com.celvansystems.projetoamigoanimal.fragment.SobreAppFragment;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Usuario;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private NavigationView navigationView;
    private InterstitialAd mInterstitialAd;
    private AdView adView;
    private ImageView imageViewPerfil;
    private View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configuraNavBar();

        setContentView(R.layout.activity_main);

        inicializarComponentes();

        habilitaOpcoesNav();

        //Propagandas
        configuraAdMob();
    }

    private void configuraNavBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.lightgray));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            decorView.setSystemUiVisibility(uiOptions);

        }
    }


    private void inicializarComponentes() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        View layout = findViewById(R.id.view_pager);

        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);

        imageViewPerfil = headerView.findViewById(R.id.imageView_perfil);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent myIntent = getIntent(); // gets the previously created intent
        String usuario_excluido = myIntent.getStringExtra("usuario_excluido");

        if (usuario_excluido != null) {

            if (usuario_excluido.equalsIgnoreCase(getString(R.string.sim))) {
                Util.setSnackBar(layout, getString(R.string.usuario_excluido));
            } else if (usuario_excluido.equalsIgnoreCase(getString(R.string.nao))) {
                Util.setSnackBar(layout, getString(R.string.falha_excluir_usuario));
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).commit();
    }

    private void carregaDadosUsuario() {

        //Dados do Usuário
        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                .child("usuarios");

        usuariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                    if (usuarios != null) {

                        FirebaseUser user = autenticacao.getCurrentUser();

                        if (usuarios.child("id").getValue() != null && ConfiguracaoFirebase.isUsuarioLogado()) {
                            if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(Objects.requireNonNull(user).getUid())) {

                                TextView navUsername = headerView.findViewById(R.id.textview_nome_humano);
                                TextView navEmail = headerView.findViewById(R.id.textView_email_cadastrado);

                                Usuario usuario = new Usuario();
                                usuario.setId(ConfiguracaoFirebase.getIdUsuario());

                                //nome
                                if (usuarios.child("nome").getValue() != null) {
                                    usuario.setNome(Objects.requireNonNull(usuarios.child("nome").getValue()).toString());
                                    navUsername.setText(usuario.getNome());
                                } else {
                                    navUsername.setText(user.getDisplayName());
                                }

                                //email
                                if (usuarios.child("email").getValue() != null) {

                                    usuario.setEmail(Objects.requireNonNull(usuarios.child("email").getValue()).toString());
                                    navEmail.setText(usuario.getEmail());
                                } else {
                                    navEmail.setText(user.getEmail());
                                }
                                //foto
                                if (usuarios.child("foto").getValue() != null) {
                                    usuario.setFoto(Objects.requireNonNull(usuarios.child("foto").getValue()).toString());
                                    Picasso.get().load(usuario.getFoto()).into(imageViewPerfil);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void habilitaOpcoesNav() {

        Menu menuNav = navigationView.getMenu();

        MenuItem nav_minha_conta = menuNav.findItem(R.id.nav_minha_conta);
        //MenuItem nav_config_notificacoes = menuNav.findItem(R.id.nav_config_notificacoes);
        MenuItem nav_meus_anuncios = menuNav.findItem(R.id.nav_meus_anuncios);
        MenuItem nav_pet_cad = menuNav.findItem(R.id.pet_cad);
        MenuItem nav_pet_adote = menuNav.findItem(R.id.pet_adote);
        MenuItem nav_doacao = menuNav.findItem(R.id.doacao);
        MenuItem nav_compartilhar_app = menuNav.findItem(R.id.nav_share_app);
        //MenuItem nav_conversar = menuNav.findItem(R.id.nav_conversar);
        MenuItem nav_help = menuNav.findItem(R.id.nav_help);
        MenuItem nav_sair = menuNav.findItem(R.id.nav_sair);
        //MenuItem nav_pet_procurado = menuNav.findItem(R.id.pet_procurado);

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            nav_minha_conta.setEnabled(true);
            nav_minha_conta.setTitle(R.string.txt_minha_conta);

            /*nav_minha_conta.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.view_pager, new PerfilUsuarioFragment()).addToBackStack("tag").commit();
                    return false;
                }
            });*/

            //nav_config_notificacoes.setEnabled(true);
            nav_meus_anuncios.setEnabled(true);
            nav_pet_cad.setEnabled(true);
            nav_pet_adote.setEnabled(true);
            //nav_conversar.setEnabled(true);
            nav_sair.setEnabled(true);
            //nav_pet_procurado.setEnabled(true);

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
            //nav_config_notificacoes.setEnabled(false);
            nav_meus_anuncios.setEnabled(false);
            nav_pet_cad.setEnabled(false);
            nav_pet_adote.setEnabled(false);
            //nav_conversar.setEnabled(false);
            nav_sair.setEnabled(false);
            //nav_pet_procurado.setEnabled(false);
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
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
        carregaDadosUsuario();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //implementa fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_minha_conta) {

            fragmentTransaction.replace(R.id.view_pager, new PerfilUsuarioFragment()).addToBackStack("tag").commit();
        }
        /*else if (id == R.id.nav_config_notificacoes) {

            fragmentTransaction.replace(R.id.view_pager, new NotificacoesFragment()).addToBackStack("tag").commit();

            //    setContentView(R.layout.content_notificacoes);
            // TODO: 17/02/2019 programar ações da content_cotificações
            Toast.makeText(getApplicationContext(),
                    "implementar content configuração de notificação na activity dentro da pasta fragment",
                    Toast.LENGTH_SHORT).show();

        } */
        else if (id == R.id.nav_meus_anuncios) {

            //reuso da activit meus anuncios
            fragmentTransaction.replace(R.id.view_pager, new MeusAnunciosFragment()).addToBackStack("tag").commit();
            mostraInterstitialAd();

        } else if (id == R.id.pet_cad) {
            // fragment pet_cad cadastrar anuncio
            fragmentTransaction.replace(R.id.view_pager, new CadastrarAnuncioFragment()).addToBackStack("tag").commit();
        } else if (id == R.id.pet_adote) {
            //reuso da activity cadastrar anuncio
            fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).addToBackStack("tag").commit();
        } else if (id == R.id.doacao) {
            // implementar funções na activit doação.
            fragmentTransaction.replace(R.id.view_pager, new DoacaoFragment()).addToBackStack("tag").commit();

        } else if (id == R.id.nav_share_app) {

            // TODO: Trocar o application ID em Constantes após publicacao na play store

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, Constantes.APPLICATION_NAME);
                String shareMessage = "\n" + Constantes.APPLICATION_MESSAGE + "\n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + Constantes.APPLICATION_ID + "\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, getText(R.string.escolha_uma_opcao)));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        /*else if (id == R.id.nav_conversar) {

            fragmentTransaction.replace(R.id.view_pager, new MensagensFragment()).addToBackStack("tag").commit();
            Toast.makeText(getApplicationContext(),
                    "criar e implementar fragment messenger com chat" +
                            " resumida a envio e recebimento de mensagens, " +
                            "semelhante ao zap",
                    Toast.LENGTH_SHORT).show();

            // TODO: 17/02/2019 implementar activity com conversar: funcionalidades basicas do zap e enviar e receber mensagens pelo app

        }*/
        else if (id == R.id.nav_help) {

            fragmentTransaction.replace(R.id.view_pager, new SobreAppFragment()).addToBackStack("tag").commit();

            // TODO: 17/02/2019 imPlementar janela de ajudas sobre o app
        } else if (id == R.id.nav_sair) {

            startActivity(new Intent(this, LoginActivity.class));

            mostraInterstitialAd();

            autenticacao.signOut();
            LoginManager.getInstance().logOut();

            invalidateOptionsMenu(); //invalida o menu e chama o onPrepare de novo
            finish();
        }
        /*else if (id == R.id.pet_procurado) {

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
        }*/
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
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    //Util.setSnackBar(layout, "intersticial failed");
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                    //Util.setSnackBar(layout, "intersticial opened");
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                    //Util.setSnackBar(layout, "intersticial on left");
                }

                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    //Util.setSnackBar(layout, "intersticial closed");
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
                    adView.loadAd(adRequest);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareInterstitialAd() {

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interAdTestId));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void mostraInterstitialAd() {
        if (mInterstitialAd == null) {
            prepareInterstitialAd();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        prepareInterstitialAd();
    }

}
