package com.example.projetoamigoanimal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.celvansystems.projetoamigoanimal.MeusAnunciosActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


    //seleçioan menu de acordo com omodo logado ou não logado
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

            usuarioLogado();
        menu.setGroupVisible(R.id.group_log_in,false);
        menu.setGroupVisible(R.id.group_log_off,false);

            if (usuarioLogado()=="1"){//se logado

                menu.setGroupVisible(R.id.group_log_in,true);
            }else{//se não logado


                menu.setGroupVisible(R.id.group_log_off,true);

            }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        switch (item.getItemId()){

            case R.id.menu_logar:
               startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
            case R.id.menu_sair:
                // TODO: 27/01/2019 fazer logout

              invalidateOptionsMenu();//apo´s habilitar função de logi logout retorna ao menu group para usuário deslogados

                break;
            case R.id.menu_meus_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private String usuarioLogado(){
        //este metodo irá buscar informação se usuario está ou não logado
        // TODO: 27/01/2019 após realizar implementação de firebase , implementar rotina para pesquisar usuario logado ou nao alogado  afim de definir menu

        String tipo="1";
        return tipo;

    }
}
