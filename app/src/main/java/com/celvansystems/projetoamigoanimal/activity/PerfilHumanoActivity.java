package com.celvansystems.projetoamigoanimal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class PerfilHumanoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_humano);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inicializarComponentes();
       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
      //  fab.setOnClickListener(new View.OnClickListener() {
      //      @Override
       //     public void onClick(View view) {
      //          Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
     //   });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   // @Override
 //   public boolean onCreateOptionsMenu(Menu menu) {
  //      // Inflate the menu; this adds items to the action bar if it is present.
  //      getMenuInflater().inflate(R.menu.perfil_humano, menu);
    //    return true;
   // }

  //  @Override
  //  public boolean onOptionsItemSelected(MenuItem item) {
   //     // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
   //     int id = item.getItemId();

   //     //noinspection SimplifiableIfStatement
    //    if (id == R.id.action_settings) {
      //      return true;
  //      }

    //    return super.onOptionsItemSelected(item);
    //}

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            // Handle the camera action

            Toast.makeText(getApplicationContext(), "falta implementar esta função de navegabilidade sobre o perfil humano"
                    , Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_config_notificacoes) {
            // TODO: 17/02/2019 programar ações da content_cotificações
            Toast.makeText(getApplicationContext(),
                    "implementar content configuração de notificação",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_meus_anuncios) {
            //reuso da activit meus anuncios
            startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));

        } else if (id == R.id.pet_adote) {
            //reuso da activity cadastrar anuncio
            startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

        } else if (id == R.id.doacao) {
            // implementar funções na activit doação.
            startActivity(new Intent(getApplicationContext(), DoacaoActivity.class));

        } else if (id == R.id.nav_share_app) {

            Toast.makeText(getApplicationContext(),
                    "implementar função de compartilhar app para instalação",
                    Toast.LENGTH_SHORT).show();
            // TODO: 17/02/2019 programar ação do botão compartilahar app on clic compartilhar para instalação do app

        } else if (id == R.id.nav_conversar) {
            Toast.makeText(getApplicationContext(),
                    "criar e implementar activity conversas resumida a envio e recebimento de mensagens, " +
                            "semelhante ao zap",
                    Toast.LENGTH_SHORT).show();

            // TODO: 17/02/2019 implementar activity com conversar: funcionalidades basicas do zap e enviar e receber mensagens pelo app

        }else if (id == R.id.nav_help) {

            Toast.makeText(getApplicationContext(),
                    "implementar content help sobre o APP",
                    Toast.LENGTH_SHORT).show();
// TODO: 17/02/2019 imPlementar janela de ajudas sobre o app
       }else if (id == R.id.nav_sair) {

            Toast.makeText(getApplicationContext(),
                    "Volte sempre, precisamos de você!!!",
                    Toast.LENGTH_SHORT).show();

            autenticacao.signOut();
            invalidateOptionsMenu(); //invalida o menu e chama o onPrepare de novo
            // TODO: 17/02/2019 implementar a função de invalidade do menu para retorno ao menus de usuarios não logado 
            finish();
// TODO: 17/02/2019 imPlementar janela
        }else if (id == R.id.pet_procurado) {
            // TODO: 17/02/2019 programar activit cadastrar procurado
            Toast.makeText(getApplicationContext(),
                    "implementar content cadastro de procurados",
                    Toast.LENGTH_SHORT).show();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inicializarComponentes(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    }
}
