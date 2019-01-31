package com.celvansystems.projetoamigoanimal.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Permissoes;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener{

    private Spinner spnEspecie, spnSexo, spnIdade, spnPorte;
    private EditText edtDescricao, edtRaca, edtNome;
    private Button btnCadastrarAnuncio;
    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaURLFotos = new ArrayList<>();
    private ImageView imagem1, imagem2, imagem3;
    private AlertDialog dialog;
    private StorageReference storage;
    //Permissoes
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Configuracoes iniciais
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        inicializarComponentes();

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        carregarDadosSpinner();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCadastrarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosAnuncio(v);
            }
        });
    }

    private void inicializarComponentes(){
        spnEspecie = (Spinner) findViewById(R.id.spinner_cad_Especie);
        spnSexo = (Spinner) findViewById(R.id.spinner_cad_Sexo);
        spnIdade = (Spinner) findViewById(R.id.spinner_cad_idade);
        spnPorte = (Spinner) findViewById(R.id.spinner_cad_porte);
        edtDescricao = (EditText) findViewById(R.id.editText_cad_descrição);
        edtRaca = (EditText) findViewById(R.id.edtRaca);
        edtNome = (EditText) findViewById(R.id.editText_cad_NomeAnimal);

        btnCadastrarAnuncio = (Button) findViewById(R.id.btnCadastrarAnuncio);

        imagem1 = (ImageView) findViewById(R.id.imageCadastro1);
        imagem2 = (ImageView) findViewById(R.id.imageCadastro2);
        imagem3 = (ImageView) findViewById(R.id.imageCadastro3);

        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);
    }

    // A implementar
    private void salvarAnuncio(Animal animal){

        hideKeyboard(getApplicationContext(), edtDescricao);

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando anúncio")
                .setCancelable(false)
                .build();
        dialog.show();

        String valor = animal.getEspécie();
        Log.d("salvar", "salvarAnuncio: " + valor );

        //salvar imagem no storage
        int tamanhoLista = animal.getFotos().size();
        for (int i=0; i < tamanhoLista; i++) {
            String urlImagem = animal.getFotos().get(i);
            salvarFotosStorage(animal, urlImagem, tamanhoLista, i);
        }

    }

    private void salvarFotosStorage(final Animal animal, String url, final int totalFotos, int contador){

        //cria nó do storage
        StorageReference imagemAnimal = storage
                .child("imagens")
                .child("animais")
                .child(animal.getIdAnimal())
                .child("imagem"+contador);
        //faz upload do arquivo
        UploadTask uploadTask = imagemAnimal.putFile(Uri.parse(url));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri firebaseUrl = taskSnapshot.getDownloadUrl();
                String urlConvertida = firebaseUrl.toString();
                listaURLFotos.add(urlConvertida);

                if(totalFotos == listaURLFotos.size()){
                    animal.setFotos(listaURLFotos);
                    animal.salvar();
                    exibirMensagem("Sucesso ao fazer upload");
                    Log.i("INFO", "Sucesso ao fazer upload");
                    dialog.dismiss();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagem("Falha ao fazer upload");
                Log.i("INFO", "Falha ao fazer upload: " + e.getMessage());
            }
        });
    }

    //cria objeto que representa os campos preenchidos da activity
    private Animal getAnimalDaActivity(){

        String nome = edtNome.getText().toString();
        String especie = spnEspecie.getSelectedItem().toString();
        String sexo = spnSexo.getSelectedItem().toString();
        String idade = spnIdade.getSelectedItem().toString();
        String porte = spnPorte.getSelectedItem().toString();
        String raca = edtRaca.getText().toString();
        String descricao = edtDescricao.getText().toString();

        Animal retorno = new Animal();

        retorno.setNome(nome);
        retorno.setEspécie(especie);
        retorno.setSexo(sexo);
        retorno.setIdade(idade);
        retorno.setPorte(porte);
        retorno.setRaca(raca);
        retorno.setDescricao(descricao);
        retorno.setFotos(listaFotosRecuperadas);

        return retorno;
    }

    public void validarDadosAnuncio(View view){

        Animal animal = this.getAnimalDaActivity();

        if( listaFotosRecuperadas.size() != 0 ){
            if( !animal.getNome().isEmpty() ){
                if( !animal.getEspécie().isEmpty() ){
                    if( !animal.getSexo().isEmpty() ){
                        if( !animal.getIdade().isEmpty() ){
                            if( !animal.getPorte().isEmpty() ){
                                if( !animal.getRaca().isEmpty() ){
                                    if( !animal.getDescricao().isEmpty()){
                                        salvarAnuncio(animal);
                                    }else {
                                        exibirMensagem("Preencha o campo descrição!");
                                    }
                                }else {
                                    exibirMensagem("Preencha o campo raça!");
                                }
                            }else {
                                exibirMensagem("Preencha o campo porte!");
                            }
                        }else {
                            exibirMensagem("Preencha o campo idade!");
                        }
                    }else {
                        exibirMensagem("Preencha o campo sexo!");
                    }
                }else {
                    exibirMensagem("Preencha o campo especie!");
                }
            }else {
                exibirMensagem("Preencha o campo nome!");
            }
        } else {
            exibirMensagem("Selecione ao menos uma foto!");
        }
    }

    //
    private void exibirMensagem(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        Log.d("onClick", "onClick: " + v.getId() );
        switch ( v.getId() ){
            case R.id.imageCadastro1 :
                Log.d("onClick", "onClick: " );
                escolherImagem(1);
                break;
            case R.id.imageCadastro2 :
                escolherImagem(2);
                break;
            case R.id.imageCadastro3 :
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == Activity.RESULT_OK){

            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if( requestCode == 1 ){
                imagem1.setImageURI( imagemSelecionada );
            }else if( requestCode == 2 ){
                imagem2.setImageURI( imagemSelecionada );
            }else if( requestCode == 3 ){
                imagem3.setImageURI( imagemSelecionada );
            }
            listaFotosRecuperadas.add( caminhoImagem );
        }
    }

    private void carregarDadosSpinner() {

        //String [] estados = getResources().getStringArray(R.array.estados);
        String [] especies = getResources().getStringArray(R.array.especies);
        String [] sexos = getResources().getStringArray(R.array.sexos);
        String [] idades = getResources().getStringArray(R.array.idade);
        String [] portes = getResources().getStringArray(R.array.portes);

        //especies
        ArrayAdapter<String> adapterEspecies = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, especies);
        adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnEspecie.setAdapter(adapterEspecies);

        //sexos
        ArrayAdapter<String> adapterSexos = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexos);
        adapterSexos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSexo.setAdapter(adapterSexos);

        //idades
        ArrayAdapter<String> adapterIdades = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, idades);
        adapterIdades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnIdade.setAdapter(adapterIdades);

        //portes
        ArrayAdapter<String> adapterPortes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, portes);
        adapterPortes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPorte.setAdapter(adapterPortes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permissaoResultado : grantResults ){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


}


