package com.celvansystems.projetoamigoanimal.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.celvansystems.projetoamigoanimal.R;

public class CadastrarAnuncioActivity extends AppCompatActivity {

    private Spinner spnEspecie, spnSexo, spnIdade, spnPorte, spnVacina;
    private EditText edtDescricao;
    private Button btnCadastrarAnuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spnIdade = (Spinner) findViewById(R.id.spinner_cad_idade);
        spnPorte = (Spinner) findViewById(R.id.spinner_cad_porte);
        edtDescricao = (EditText) findViewById(R.id.editText_cad_descrição);
        btnCadastrarAnuncio = (Button) findViewById(R.id.btnCadastrarAnuncio);

        carregarDadosSpinner();

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

    private void carregarDadosSpinner() {

        //String [] estados = getResources().getStringArray(R.array.estados);

        String [] idades = getResources().getStringArray(R.array.idade);
        String [] portes = getResources().getStringArray(R.array.portes);

        //idades
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, idades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIdade.setAdapter(adapter);

        //portes
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, portes);
        spnPorte.setAdapter(adapter);
        };
    }


