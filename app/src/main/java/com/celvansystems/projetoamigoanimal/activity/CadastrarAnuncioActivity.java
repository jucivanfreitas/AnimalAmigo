package com.celvansystems.projetoamigoanimal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.celvansystems.projetoamigoanimal.R;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    //    FloatingActionButton fab = findViewById(R.id.fabcadastrar);
  //      fab.setOnClickListener(new View.OnClickListener() {
       //     @Override
     //       public void onClick(View view) {
    //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
       //                 .setAction("Action", null).show();
      //      }
    //    });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
