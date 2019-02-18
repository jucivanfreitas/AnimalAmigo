package com.celvansystems.projetoamigoanimal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilHumanoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth autenticacao;

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void inicializarComponentes(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        Button btnDesativarConta = findViewById(R.id.btnEncerrarConta);

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
    private void removerContaAtual(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String usuarioId = ConfiguracaoFirebase.getIdUsuario();

        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            // TODO: 18/02/2019 inserir lógica para excluir todos os anuncios do usuario excluido

                            Log.d("INFO3", "User account deleted.");
                            Toast.makeText(getApplicationContext(), "Usuário excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            Log.d("INFO3", "User account not deleted.");
                            Toast.makeText(getApplicationContext(), "Falha ao excluir usuário!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
