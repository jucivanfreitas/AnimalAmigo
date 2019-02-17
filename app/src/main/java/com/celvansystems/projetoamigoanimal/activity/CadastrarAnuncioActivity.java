package com.celvansystems.projetoamigoanimal.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Permissoes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener, IPickResult {

    private Spinner spnEspecie, spnSexo, spnIdade, spnPorte;
    private Spinner spnEstado, spnCidade;
    private EditText edtDescricao, edtRaca, edtNome;
    private Button btnCadastrarAnuncio;
    private List<String> listaFotosRecuperadas;
    private List<String> listaURLFotos;
    private ImageView imagem1, imagem2, imagem3;
    private AlertDialog dialog;
    private StorageReference storage;
    private int requisicao;

    //Permissoes
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        //Configuracoes iniciais
        inicializarComponentes();
    }

    /**
     * Configuracoes iniciais
     */
    private void inicializarComponentes(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        spnEspecie = findViewById(R.id.spinner_cad_Especie);
        spnSexo = findViewById(R.id.spinner_cad_Sexo);
        spnIdade = findViewById(R.id.spinner_cad_idade);
        spnPorte = findViewById(R.id.spinner_cad_porte);
        spnEstado = findViewById(R.id.spinner_cad_estado);
        spnCidade = findViewById(R.id.spinner_cad_cidade);
        edtDescricao = findViewById(R.id.editText_cad_descrição);
        edtRaca = findViewById(R.id.edtRaca);
        edtNome = findViewById(R.id.editText_cad_NomeAnimal);

        btnCadastrarAnuncio = findViewById(R.id.btnCadastrarAnuncio);

        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);

        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        listaFotosRecuperadas = new ArrayList<>();
        listaURLFotos = new ArrayList<>();

        spnEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                setAdapterSpinnerCidades();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        carregarDadosSpinner();

        //botao voltar da Activity
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        btnCadastrarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosAnuncio(v);
            }
        });
    }

    /**
     * Configura o adapter do spinner Cidades
     */
    public void setAdapterSpinnerCidades(){

        try {
            ArrayAdapter adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, Util.getCidadesJSON(spnEstado.getSelectedItem().toString(), this));
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     *
     * @param animal anuncio
     */
    private void salvarAnuncio(Animal animal){

        hideKeyboard(getApplicationContext(), edtDescricao);

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Salvando anúncio")
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e){e.printStackTrace();}

        //salvar imagem no storage
        try {
            int tamanhoLista = animal.getFotos().size();
            for (int i=0; i < tamanhoLista; i++) {
                String urlImagem = animal.getFotos().get(i);
                salvarFotosStorage(animal, urlImagem, tamanhoLista, i);
            }
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     *
     * @param animal animal
     * @param url url
     * @param totalFotos número de fotos
     * @param contador contador
     */
    private void salvarFotosStorage(final Animal animal, String url, final int totalFotos, int contador){
// TODO: 16/02/2019 tentar comrpessao de fotos neste método
        //cria nó do storage
        try {
            final StorageReference imagemAnimal = storage
                    .child("imagens")
                    .child("animais")
                    .child(animal.getIdAnimal())
                    .child("imagem"+contador);

            UploadTask uploadTask = imagemAnimal.putFile(Uri.parse(url));

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    // Continue with the task to get the download URL
                    return imagemAnimal.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri firebaseUrl = task.getResult();
                        String urlConvertida = Objects.requireNonNull(firebaseUrl).toString();
                        listaURLFotos.add(urlConvertida);

                        if(totalFotos == listaURLFotos.size()){
                            animal.setFotos(listaURLFotos);
                            animal.salvar();
                            exibirMensagem("Sucesso ao fazer upload");
                            dialog.dismiss();
                            finish();
                        }
                    } else {
                        exibirMensagem("Falha ao fazer upload");
                    }
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * cria objeto que representa os campos preenchidos da activity
     * @return animal
     */
    private Animal getAnimalDaActivity(){

        Animal retorno = new Animal();

        try {
            String nome = edtNome.getText().toString();
            String especie = spnEspecie.getSelectedItem().toString();
            String sexo = spnSexo.getSelectedItem().toString();
            String idade = spnIdade.getSelectedItem().toString();
            String porte = spnPorte.getSelectedItem().toString();
            String raca = edtRaca.getText().toString();
            String descricao = edtDescricao.getText().toString();
            String estado = spnEstado.getSelectedItem().toString();
            String cidade = spnCidade.getSelectedItem().toString();

            retorno.setDonoAnuncio(ConfiguracaoFirebase.getIdUsuario());
            retorno.setNome(nome);
            retorno.setEspecie(especie);
            retorno.setSexo(sexo);
            retorno.setIdade(idade);
            retorno.setPorte(porte);
            retorno.setRaca(raca);
            retorno.setDescricao(descricao);
            retorno.setUf(estado);
            retorno.setCidade(cidade);
            retorno.setFotos(listaFotosRecuperadas);
        } catch (Exception e){e.printStackTrace();}
        return retorno;
    }

    /**
     * valida o preenchimento dos dados pelo usuário
     * @param view view
     */
    public void validarDadosAnuncio(View view){

        Animal animal = this.getAnimalDaActivity();

        if( listaFotosRecuperadas.size() != 0 ){
            if( !animal.getNome().isEmpty() ){
                if( !animal.getEspecie().isEmpty() ){
                    if( !animal.getSexo().isEmpty() ){
                        if( !animal.getIdade().isEmpty() ){
                            if( !animal.getPorte().isEmpty() ){
                                if( !animal.getRaca().isEmpty() ){
                                    if( !animal.getUf().isEmpty() ) {
                                        if (!animal.getCidade().isEmpty()) {
                                            if (!animal.getDescricao().isEmpty()) {
                                                salvarAnuncio(animal);
                                            } else {
                                                exibirMensagem("Preencha o campo descrição!");
                                            }
                                        } else {
                                            exibirMensagem("Preencha o campo cidade!");
                                        }
                                    }else {
                                        exibirMensagem("Preencha o campo estado!");
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

    /**
     * exibe um Toast
     * @param mensagem string
     */
    private void exibirMensagem(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {

        switch ( v.getId() ){
            case R.id.imageCadastro1 :
                requisicao = 1;
                escolherImagem(1);
                break;
            case R.id.imageCadastro2 :
                requisicao = 2;
                escolherImagem(2);
                break;
            case R.id.imageCadastro3 :
                requisicao = 3;
                escolherImagem(3);
                break;
        }
    }

    /**
     *
     * @param requestCode int
     */
    public void escolherImagem(int requestCode){
        try {

            PickSetup setup = new PickSetup()
                    .setTitle("Escolha")
                    .setFlip(true)
                    .setMaxSize(500)
                    .setCameraButtonText("Câmera")
                    .setCancelText("Cancelar")
                    .setGalleryButtonText("Galeria");
            //.setTitleColor(yourColor)
            //.setBackgroundColor(yourColor)
            //.setProgressText(yourText)
            //.setProgressTextColor(yourColor)
            //.setCancelTextColor(yourColor)
            //.setButtonTextColor(R.color.colorAccent)
            //.setDimAmount(yourFloat)
            //.setIconGravity(Gravity.LEFT)
            //.setButtonOrientation(LinearLayoutCompat.VERTICAL)
            //.setSystemDialog(false)
            //.setGalleryIcon(yourIcon)
            //.setCameraIcon(yourIcon);
            PickImageDialog.build(setup).show(this);

        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onPickResult(PickResult r) {

        try {
            if (r.getError() == null) {

                Uri imagemSelecionada = r.getUri();
                String caminhoImagem = Objects.requireNonNull(imagemSelecionada).toString();

                if (requisicao == 1) {

                    imagem1.setImageURI(r.getUri());
                    Bitmap bitmap = ((BitmapDrawable) imagem1.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
                    byte[] bitmapdata = baos.toByteArray();
                    Bitmap bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                    imagem1.setImageBitmap(bmp);
                    //imagem1.setImageBitmap();

                } else if (requisicao == 2) {
                    //Tiny.BitmapCompressOptions options = new Tiny.BitmapCompressOptions();
                    //Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();

                    //BitmapResult result = Tiny.getInstance().source(caminhoImagem).asBitmap().withOptions(options).compressSync();
                    //FileResult result = Tiny.getInstance().source(caminhoImagem).asFile().withOptions(options).compressSync();
                    //imagem2.setImageURI(Uri.fromFile(new File(result.toString())));

                    imagem2.setImageURI(r.getUri());
                    Bitmap bitmap = ((BitmapDrawable) imagem2.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                    byte[] bitmapdata = baos.toByteArray();
                    Bitmap bmp = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
                    imagem2.setImageBitmap(bmp);
                    //imagem2.setImageURI(r.getUri());

                } else {

                    imagem3.setImageURI(r.getUri());
                }
                listaFotosRecuperadas.add(caminhoImagem);
            } else {
                //Handle possible errors
                //TODO: do what you have to do with r.getError();
            }
        } catch (Exception e){e.printStackTrace();}
    }

    public void comprimirImagem (String path){
    }

    /**
     *
     * @param requestCode int
     * @param resultCode int
     * @param data intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (resultCode == Activity.RESULT_OK) {
            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = Objects.requireNonNull(imagemSelecionada).toString();

            try {
                //Configura imagem no ImageView
                if (requestCode == 1) {
                    imagem1.setImageURI(imagemSelecionada);
                } else if (requestCode == 2) {
                    imagem2.setImageURI(imagemSelecionada);
                } else if (requestCode == 3) {
                    imagem3.setImageURI(imagemSelecionada);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }*/
    }
    /**
     * preenche os spinners
     */
    private void carregarDadosSpinner() {

        String[] especies = Util.getEspecies(this);
        String[] sexos = getResources().getStringArray(R.array.sexos);
        String[] idades = getResources().getStringArray(R.array.idade);
        String[] portes = getResources().getStringArray(R.array.portes);
        String[] estados = Util.getEstadosJSON(this);

        //especies
        try {
            ArrayAdapter<String> adapterEspecies = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, especies);
            adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEspecie.setAdapter(adapterEspecies);
        } catch (Exception e){e.printStackTrace();}

        //sexos
        try {
            ArrayAdapter<String> adapterSexos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sexos);

            adapterSexos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnSexo.setAdapter(adapterSexos);
        } catch (Exception e){e.printStackTrace();}

        /* idades */
        try{
            ArrayAdapter<String> adapterIdades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, idades);
            adapterIdades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnIdade.setAdapter(adapterIdades);
        } catch (Exception e){e.printStackTrace();}

        /* portes */
        try {
            ArrayAdapter<String> adapterPortes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, portes);
            adapterPortes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnPorte.setAdapter(adapterPortes);
        } catch (Exception e){e.printStackTrace();}

        /*estados*/
        try {
            ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEstado.setAdapter(adapterEstados);
        } catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            for (int permissaoResultado : grantResults) {
                if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                    alertaValidacaoPermissao();
                }
            }
        } catch (Exception e){e.printStackTrace();}
    }

    private void alertaValidacaoPermissao () {

        try {
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
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * esconde o teclado virtual
     * @param context contexto
     * @param editText view
     */
    public static void hideKeyboard (Context context, View editText){
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e){e.printStackTrace();}
    }
}


