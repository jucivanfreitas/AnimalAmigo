package com.celvansystems.projetoamigoanimal.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Permissoes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;

public class CadastrarAnuncioActivity extends AppCompatActivity
        implements View.OnClickListener, IPickResult {

    private Spinner spnEspecie, spnSexo, spnIdade, spnPorte;
    private Spinner spnEstado, spnCidade;
    private EditText edtRaca, edtNome;
    private AutoCompleteTextView edtDescricao;
    private List<String> listaFotosRecuperadas;
    private List<String> listaURLFotos;
    private ImageView imagem1, imagem2, imagem3;
    private AlertDialog dialog;
    private StorageReference storage;
    private int requisicao;
    private View layout;
    private InterstitialAd mInterstitialAd;

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

        layout = findViewById(R.id.cadastrar_anuncio_layout);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        spnEspecie = findViewById(R.id.spinner_cad_Especie);
        spnSexo = findViewById(R.id.spinner_cad_Sexo);
        spnIdade = findViewById(R.id.spinner_cad_idade);
        spnPorte = findViewById(R.id.spinner_cad_porte);
        spnEstado = findViewById(R.id.spinner_cad_estado);
        spnCidade = findViewById(R.id.spinner_cad_cidade);
        edtDescricao = findViewById(R.id.editText_cad_descrição);
        edtDescricao.setVerticalScrollBarEnabled(true);
        edtDescricao.setMovementMethod(new ScrollingMovementMethod());
        edtRaca = findViewById(R.id.edtRaca);
        edtNome = findViewById(R.id.editText_cad_NomeAnimal);

        Button btnCadastrarAnuncio = findViewById(R.id.btnCadastrarAnuncio);

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
        //propagandas
        configuraAdMob();
    }

    /**
     * método que configura as propagandas via AdMob
     */
    private void configuraAdMob() {

        //admob
        //MobileAds.initialize(this, String.valueOf(R.string.app_id));
        //teste do google
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        //AdView
        try {
            //teste
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    Util.setSnackBar(layout, "intersticial loaded");
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


        } catch (Exception e) {e.printStackTrace();}
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
                    .setMessage(R.string.salvando_anuncio)
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

        try {
            //cria nó do storage
            final StorageReference imagemAnimal = storage
                    .child("imagens")
                    .child("animais")
                    .child(animal.getIdAnimal())
                    .child("imagem"+contador);

            Uri selectedImage = Uri.parse(url);

            //imagem comprimida
            byte[] byteArray = comprimirImagem(selectedImage);
            UploadTask uploadTask = imagemAnimal.putBytes(byteArray);

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
                            exibirMensagem(getString(R.string.sucesso_ao_fazer_upload));
                            dialog.dismiss();
                            finish();
                        }
                    } else {
                        exibirMensagem(getString(R.string.falha_upload));
                    }
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * metodo auxiliar para comprimir imagens que serao salvas do firebase storage
     * @param selectedImage imagem selecionada
     * @return byte[]
     */
    public byte[] comprimirImagem (Uri selectedImage){
        byte[] byteArray = null;
        try {
            InputStream imageStream;

            imageStream = getContentResolver().openInputStream(
                    selectedImage);

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, Constantes.QUALIDADE_IMAGEM, stream);
            byteArray = stream.toByteArray();

            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {

            e.printStackTrace();
        }
        return byteArray;
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

            // TODO: 18/02/2019 configurar Usuario para pegar dados do banco
            //Usuario donoAnuncio = new Usuario();
            //donoAnuncio.setId(ConfiguracaoFirebase.getIdUsuario());

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
                                                exibirMensagem(getString(R.string.preencha_descricao));
                                            }
                                        } else {
                                            exibirMensagem(getString(R.string.preencha_cidade));
                                        }
                                    }else {
                                        exibirMensagem(getString(R.string.preencha_estado));
                                    }
                                }else {
                                    exibirMensagem(getString(R.string.preencha_raca));
                                }
                            }else {
                                exibirMensagem(getString(R.string.preencha_porte));
                            }
                        }else {
                            exibirMensagem(getString(R.string.preencha_idade));
                        }
                    }else {
                        exibirMensagem(getString(R.string.preencha_sexo));
                    }
                }else {
                    exibirMensagem(getString(R.string.preencha_especie));
                }
            }else {
                exibirMensagem(getString(R.string.preencha_nome));
            }
        } else {
            exibirMensagem(getString(R.string.selecione_foto));
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
                break;
            case R.id.imageCadastro2 :
                requisicao = 2;
                break;
            case R.id.imageCadastro3 :
                requisicao = 3;
                break;
        }
        escolherImagem(requisicao);
    }

    /**
     *
     * @param requestCode int
     */
    public void escolherImagem(int requestCode){
        try {

            PickSetup setup = new PickSetup()
                    .setTitle(getString(R.string.escolha))
                    .setFlip(true)
                    .setMaxSize(500)
                    .setCameraButtonText(getString(R.string.camera))
                    .setCancelText(getString(R.string.cancelar))
                    .setGalleryButtonText(getString(R.string.galeria));
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

                } else if (requisicao == 2) {

                    imagem2.setImageURI(r.getUri());

                } else {

                    imagem3.setImageURI(r.getUri());
                }
                listaFotosRecuperadas.add(caminhoImagem);
            }
        } catch (Exception e){e.printStackTrace();}
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
            builder.setTitle(getString(R.string.permissoes_negadas));
            builder.setMessage(getString(R.string.necessario_aceitar_permissoes));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.confirmar), new DialogInterface.OnClickListener() {
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


