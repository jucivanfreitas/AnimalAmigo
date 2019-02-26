package com.celvansystems.projetoamigoanimal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.fragment.AnunciosFragment;
import com.celvansystems.projetoamigoanimal.fragment.CadastrarAnuncioFragment;
import com.celvansystems.projetoamigoanimal.fragment.DoacaoFragment;
import com.celvansystems.projetoamigoanimal.fragment.MensagensFragment;
import com.celvansystems.projetoamigoanimal.fragment.NotificacoesFragment;
import com.celvansystems.projetoamigoanimal.fragment.PerfilUsuarioFragment;
import com.celvansystems.projetoamigoanimal.fragment.ProcuradoFragment;
import com.celvansystems.projetoamigoanimal.fragment.SobreAppFragment;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class PerfilHumanoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;
    private View layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_humano);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inicializarComponentes();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //ativa uso de fragments
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).commit();

        //propagandas
        configuraAdMob();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //substitui page view com o fragment selecionado

        //implementa fragmento
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();


        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            // Handle the camera action
            fragmentTransaction.replace(R.id.view_pager, new PerfilUsuarioFragment()).commit();
            //

            Toast.makeText(getApplicationContext(), "foi aberto a fragment Perfil.." +
                            " realizar proframação na fragment PerfilUser"
                    , Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_config_notificacoes) {

            fragmentTransaction.replace(R.id.view_pager, new NotificacoesFragment()).commit();

            //    setContentView(R.layout.content_notificacoes);
            // TODO: 17/02/2019 programar ações da content_cotificações
            Toast.makeText(getApplicationContext(),
                    "implementar content configuração de notificação" +
                            " na activity dentro da pasta fragment",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_meus_anuncios) {
            //reuso da activit meus anuncios
            startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));


        } else if (id == R.id.pet_cad) {
            // fragment pet_cad cadastrar anuncio
            fragmentTransaction.replace(R.id.view_pager, new CadastrarAnuncioFragment()).commit();


        }
        else if (id == R.id.pet_adote) {
            //reuso da activity cadastrar anuncio
            fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).commit();


        } else if (id == R.id.doacao) {
            // implementar funções na activit doação.
            fragmentTransaction.replace(R.id.view_pager, new DoacaoFragment()).commit();
            Toast.makeText(getApplicationContext(),
                    "implementar content  de doações " +
                            " na activity dentro da pasta fragment." +
                            "essa fragmente terá apelos afim de comover " +
                            "o usuário a doar e links para " +
                            "receber doação na activity doação"
                    ,
                    Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_share_app) {

            Toast.makeText(getApplicationContext(),
                    "implementar função de compartilhar app para instalação",
                    Toast.LENGTH_SHORT).show();
            // TODO: 17/02/2019 programar ação do botão compartilahar app on clic compartilhar para instalação do app

        } else if (id == R.id.nav_conversar) {

            fragmentTransaction.replace(R.id.view_pager, new MensagensFragment()).commit();
            Toast.makeText(getApplicationContext(),
                    "criar e implementar fragment messenger com chat" +
                            " resumida a envio e recebimento de mensagens, " +
                            "semelhante ao zap",
                    Toast.LENGTH_SHORT).show();

            // TODO: 17/02/2019 implementar activity com conversar: funcionalidades basicas do zap e enviar e receber mensagens pelo app

        }else if (id == R.id.nav_help) {

            fragmentTransaction.replace(R.id.view_pager, new SobreAppFragment()).commit();
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

            autenticacao.signOut();
            LoginManager.getInstance().logOut();

            invalidateOptionsMenu(); //invalida o menu e chama o onPrepare de novo
            // TODO: 17/02/2019 implementar a função de invalidade do menu para retorno ao menus de usuarios não logado 
            finish();
// TODO: 17/02/2019 imPlementar janela
        }else if (id == R.id.pet_procurado) {

            fragmentTransaction.replace(R.id.view_pager, new ProcuradoFragment()).commit();
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

    private void inicializarComponentes(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        Button btnDesativarConta = findViewById(R.id.btnEncerrarConta);

        layout = findViewById(R.id.constraint_perfil_humano);

        btnDesativarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDesativarConta = new AlertDialog.Builder(v.getContext());

                alertDesativarConta.setTitle("Tem certeza que deseja desativar sua conta? ");
                alertDesativarConta.setMessage("Esta opção não poderá ser revertida.");

                //final EditText input = new EditText(v.getContext());
                //input.setHint("Digite sua senha");
                //alertDesativarConta.setView(input);

                alertDesativarConta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 18/02/2019 codigo para excluir conta do usuario e todos os seus anuncios

                        removerContaAtual();

                        dialog.dismiss();
                    }
                });

                alertDesativarConta.setNegativeButton("Não", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = alertDesativarConta.create();
                alert.show();
            }
        });
    }

    /**
     * metodo que exclui o usuario atual
     */
    private boolean removerContaAtual(){
        boolean retorno = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String usuarioId = ConfiguracaoFirebase.getIdUsuario();
        Log.d("INFO29", "usuario id: " + usuarioId);
        Task<Void> task = user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            deletarAnunciosUsuario(usuarioId );

                            // TODO: 18/02/2019 inserir lógica para excluir todos os anuncios do usuario excluido

                            //google sign in
                            // TODO: 21/02/2019 verificar se eh conta google ou facebook
                            revokeAccess();

                            //
                            Log.d("INFO3", "User account deleted.");
                            Toast.makeText(getApplicationContext(), "Usuário excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), PerfilHumanoActivity.class));
                            finish();



                        } else {
                            Log.d("INFO3", "User account not deleted.");
                            Toast.makeText(getApplicationContext(), "Falha ao excluir usuário!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(task.isSuccessful()) {
            retorno = true;
        }
        return retorno;
    }

    private void deletarAnunciosUsuario(final String usuarioId) {

        try {
            //excluindo todos os anuncios do usuario removido
            DatabaseReference anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
            Log.d("INFO29", "passou1");
            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot animal: snapshot.getChildren()){
                        Log.d("INFO29", "passou2");
                        Animal anuncio = animal.getValue(Animal.class);
                        if(anuncio != null && anuncio.getDonoAnuncio()!= null) {
                            Log.d("INFO29", "passou3: "+ anuncio.getDonoAnuncio());
                            if (anuncio.getDonoAnuncio().equalsIgnoreCase(usuarioId)) {
                                Log.d("INFO29", "passou4: animal removido: "+ anuncio.getIdAnimal());
                                animal.getRef().removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("INFO29", "passou5: nancelled: "+ databaseError.getMessage());
                    throw databaseError.toException();
                }
            });
        } catch (Exception e) {
            Log.d("INFO29", "passou6: excecao!!!: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //google
    private void revokeAccess() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });

    }

    /**
     * método que configura as propagandas via AdMob
     */
    private void configuraAdMob() {

        //admob
        //MobileAds.initialize(this, String.valueOf(R.string.app_id));
        //teste do google
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        Log.d("INFO50", "configurando admob");
        //AdView
        try {
            //teste
            InterstitialAd mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Log.d("INFO50", "intersticial loaded");
                    // TODO: 23/02/2019 definir se mostra ou nao intersticial nos anuncios
                    //mInterstitialAd.show();
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
                    //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });

            //banner
            final AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
                    .build();

            final AdView adView = findViewById(R.id.adView2);
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
}
