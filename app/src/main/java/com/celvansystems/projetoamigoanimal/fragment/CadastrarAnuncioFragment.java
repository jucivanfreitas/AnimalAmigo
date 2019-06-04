package com.celvansystems.projetoamigoanimal.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;

/**
 * A simple {@link Fragment} subclass.
 */
public class CadastrarAnuncioFragment extends Fragment
        implements View.OnClickListener {

    private Spinner spnEspecie, spnSexo, spnIdade, spnPorte;
    private Spinner spnEstado, spnCidade;
    private EditText edtRaca, edtNome;
    private AutoCompleteTextView edtDescricao;
    private HashMap<Integer, String> listaFotosRecuperadas;
    private List<String> listaURLFotos;
    private ImageView imagem1, imagem2, imagem3;
    private AlertDialog dialog;
    private StorageReference storage;
    private int requisicao;
    private View layout;
    private View viewFragment;
    private Button btnCadastrarAnuncio;
    private ArrayAdapter<String> adapterEspecies, adapterIdades;
    private ArrayAdapter<String> adapterCidades, adapterSexos;
    private ArrayAdapter<String> adapterEstados, adapterPortes;
    private boolean editando = false;
    private String idEditando;

    //Permissoes
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public CadastrarAnuncioFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewFragment = inflater.inflate(R.layout.fragment_cadastrar_anuncio, container, false);
        Bundle bundle = this.getArguments();

        inicializarComponentes();

        if (bundle != null) {
            Animal anuncio = (Animal) bundle.getSerializable("anuncioSelecionado");
            btnCadastrarAnuncio.setText(getString(R.string.atualizar_cadastro));
            if (anuncio != null) {
                preencheCampos(anuncio);
            }
        }
        return viewFragment;
    }

    /**
     * Configuracoes iniciais
     */
    @SuppressLint("UseSparseArrays")
    private void inicializarComponentes() {

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        layout = viewFragment.findViewById(R.id.constraintLayout_cadastrar);

        spnEspecie = viewFragment.findViewById(R.id.spinner_cad_Especie);
        spnSexo = viewFragment.findViewById(R.id.spinner_cad_Sexo);
        spnIdade = viewFragment.findViewById(R.id.spinner_cad_idade);
        spnPorte = viewFragment.findViewById(R.id.spinner_cad_porte);
        spnEstado = viewFragment.findViewById(R.id.spinner_cad_estado_complemento);
        spnCidade = viewFragment.findViewById(R.id.spinner_cad_cidade_complemento);
        edtDescricao = viewFragment.findViewById(R.id.editText_cad_descrição);
        edtDescricao.setVerticalScrollBarEnabled(true);
        edtDescricao.setMovementMethod(new ScrollingMovementMethod());
        edtRaca = viewFragment.findViewById(R.id.edtRaca);
        edtNome = viewFragment.findViewById(R.id.editText_cad_NomeAnimal);

        btnCadastrarAnuncio = viewFragment.findViewById(R.id.btnCadAnuncio);

        //imageviews
        imagem1 = viewFragment.findViewById(R.id.imageCad1);
        imagem2 = viewFragment.findViewById(R.id.imageCad2);
        imagem3 = viewFragment.findViewById(R.id.imageCad3);
        imagem1.setOnClickListener(this);
        imagem2.setOnClickListener(this);
        imagem3.setOnClickListener(this);

        //listaFotosRecuperadas = new ArrayList<>();
        listaFotosRecuperadas = new HashMap<>();
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
        Permissoes.validarPermissoes(permissoes, getActivity(), 1);

        carregarDadosSpinner();

        btnCadastrarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosAnuncio();
            }
        });

        //propagandas
        configuraAdMob();
    }

    private void preencheCampos(Animal anuncio) {

        edtNome.setText(anuncio.getNome());
        edtRaca.setText(anuncio.getRaca());
        edtDescricao.setText(anuncio.getDescricao());

        int spinnerPosition = adapterEspecies.getPosition(anuncio.getEspecie());
        spnEspecie.setSelection(spinnerPosition);

        spinnerPosition = adapterSexos.getPosition(anuncio.getSexo());
        spnSexo.setSelection(spinnerPosition);

        spinnerPosition = adapterIdades.getPosition(anuncio.getIdade());
        spnIdade.setSelection(spinnerPosition);

        spinnerPosition = adapterPortes.getPosition(anuncio.getPorte());
        spnPorte.setSelection(spinnerPosition);

        spinnerPosition = adapterEstados.getPosition(anuncio.getUf());
        spnEstado.setSelection(spinnerPosition);

        setAdapterSpinnerCidades();
        spinnerPosition = adapterCidades.getPosition(anuncio.getCidade());
        spnCidade.setSelection(spinnerPosition);

        editando = true;
        idEditando = anuncio.getIdAnimal();

        configuraFotos(anuncio);
    }

    /**
     * Popula imaageviews das imagens
     *
     * @param anuncio animal
     */
    private void configuraFotos(Animal anuncio) {

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(getContext())
                    .setMessage(R.string.carregando_fotos)
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> listaFotos = anuncio.getFotos();

        String[] fotosAnuncioSelecionado = new String[listaFotos.size()];
        fotosAnuncioSelecionado = listaFotos.toArray(fotosAnuncioSelecionado);

        final Context ctx = viewFragment.getContext();

        final String[] finalFotosAnuncioSelecionado = fotosAnuncioSelecionado;

        for (int i = 0; i < fotosAnuncioSelecionado.length; i++) {

            switch (i) {

                //imagem 1
                case 0:
                    try {
                        imagem1.post(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load(finalFotosAnuncioSelecionado[0])
                                        .resize(imagem1.getWidth(), imagem1.getHeight())
                                        .centerCrop()
                                        .into(imagem1, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Drawable drawable = imagem1.getDrawable();

                                                Bitmap mBitmap = ((BitmapDrawable) drawable).getBitmap();

                                                String path = MediaStore.Images.Media.insertImage(ctx
                                                        .getContentResolver(), mBitmap, null, null);
                                                Uri uri = Uri.parse(path);

                                                listaFotosRecuperadas.put(1, uri.toString());
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                dialog.dismiss();
                                                e.printStackTrace();
                                            }
                                        });
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                //imagem 2
                case 1:
                    try {
                        imagem2.post(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load(finalFotosAnuncioSelecionado[1])
                                        .resize(imagem2.getWidth(), imagem2.getHeight())
                                        .centerCrop()
                                        .into(imagem2, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Drawable drawable = imagem2.getDrawable();

                                                Bitmap mBitmap = ((BitmapDrawable) drawable).getBitmap();

                                                String path = MediaStore.Images.Media.insertImage(ctx
                                                        .getContentResolver(), mBitmap, null, null);
                                                Uri uri = Uri.parse(path);

                                                listaFotosRecuperadas.put(2, uri.toString());
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                dialog.dismiss();
                                                e.printStackTrace();
                                            }
                                        });
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                //imagem 3
                case 2:

                    try {
                        imagem3.post(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load(finalFotosAnuncioSelecionado[2])
                                        .resize(imagem3.getWidth(), imagem3.getHeight())
                                        .centerCrop()
                                        .into(imagem3, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Drawable drawable = imagem3.getDrawable();
                                                // ...
                                                Bitmap mBitmap = ((BitmapDrawable) drawable).getBitmap();

                                                String path = MediaStore.Images.Media.insertImage(ctx
                                                        .getContentResolver(), mBitmap, null, null);
                                                Uri uri = Uri.parse(path);

                                                listaFotosRecuperadas.put(3, uri.toString());
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                dialog.dismiss();
                                                e.printStackTrace();
                                            }
                                        });
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    /**
     * Configura o adapter do spinner Cidades
     */
    public void setAdapterSpinnerCidades() {

        try {
            adapterCidades = new ArrayAdapter<>(Objects.requireNonNull(getContext()), simple_spinner_item,
                    Util.getCidadesJSON(spnEstado.getSelectedItem().toString(), getContext()));
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param animal anuncio
     */
    private void salvarAnuncio(Animal animal) {

        hideKeyboard(getContext(), edtDescricao);

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(getContext())
                    .setMessage(R.string.salvando_anuncio)
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //salvar imagem no storage
        try {
            int tamanhoLista = animal.getFotos().size();
            for (int i = 0; i < tamanhoLista; i++) {
                String urlImagem = animal.getFotos().get(i);
                salvarFotosStorage(animal, urlImagem, tamanhoLista, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param animal     animal
     * @param url        url
     * @param totalFotos número de fotos
     * @param contador   contador
     */
    private void salvarFotosStorage(final Animal animal, String url, final int totalFotos, int contador) {

        try {
            //cria nó do storage
            final StorageReference imagemAnimal = storage
                    .child("imagens")
                    .child("animais")
                    .child(animal.getIdAnimal())
                    .child("imagem" + contador);

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

                        if (totalFotos == listaURLFotos.size()) {
                            animal.setFotos(listaURLFotos);
                            animal.salvar();
                            Util.setSnackBar(layout, getString(R.string.sucesso_ao_fazer_upload));

                            dialog.dismiss();

                            //redireciona para MeusAnunciosFragment
                            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.view_pager, new MeusAnunciosFragment()).addToBackStack("tag").commit();
                        }
                    } else {
                        Util.setSnackBar(layout, getString(R.string.falha_upload));
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * metodo auxiliar para comprimir imagens que serao salvas do firebase storage
     *
     * @param selectedImage imagem selecionada
     * @return byte[]
     */
    public byte[] comprimirImagem(Uri selectedImage) {
        byte[] byteArray = null;
        try {
            InputStream imageStream;

            imageStream = Objects.requireNonNull(getContext()).getApplicationContext().getContentResolver().openInputStream(
                    selectedImage);

            Bitmap bmp = BitmapFactory.decodeStream(imageStream);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, Constantes.QUALIDADE_IMAGEM, stream);
            byteArray = stream.toByteArray();

            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return byteArray;
    }

    /**
     * cria objeto que representa os campos preenchidos da activity
     *
     * @return animal
     */
    private Animal getAnimalDaActivity() {

        Animal retorno = new Animal();
        if (editando) {
            retorno.setIdAnimal(idEditando);
        }

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
            retorno.setFotos(new ArrayList<>(listaFotosRecuperadas.values()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retorno;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * valida o preenchimento dos dados pelo usuário
     */
    public void validarDadosAnuncio() {

        Animal animal = this.getAnimalDaActivity();

        if (listaFotosRecuperadas.size() != 0) {
            if (!animal.getNome().isEmpty()) {
                if (!animal.getRaca().isEmpty()) {
                    if (!animal.getDescricao().isEmpty()) {

                        salvarAnuncio(animal);

                    } else {
                        Util.setSnackBar(layout, getString(R.string.preencha_descricao));
                    }
                } else {
                    Util.setSnackBar(layout, getString(R.string.preencha_raca));
                }
            } else {
                Util.setSnackBar(layout, getString(R.string.preencha_nome));
            }
        } else {
            Util.setSnackBar(layout, getString(R.string.selecione_foto));
        }
    }

    /**
     * clique das fotos
     *
     * @param v view
     */
    @Override
    public void onClick(View v) {

        requisicao = 1;

        switch (v.getId()) {
            case R.id.imageCad1:
                requisicao = 1;
                break;
            case R.id.imageCad2:
                requisicao = 2;
                break;
            case R.id.imageCad3:
                requisicao = 3;
                break;
        }
        escolherImagem();
    }

    /**
     * ImagePick
     */
    public void escolherImagem() {
        try {

            PickSetup setup = new PickSetup()
                    .setTitle(getString(R.string.escolha))
                    .setFlip(true)
                    .setMaxSize(Constantes.PICK_MAX_SIZE)
                    .setCameraButtonText(getString(R.string.cameraa))
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
            //PickImageDialog.build(setup).show(this);
            PickImageDialog.build(setup)
                    .setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult r) {
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
                                /*if(listaFotosRecuperadas.size() == 3) {
                                    listaFotosRecuperadas.remove(0);
                                }*/
                                //listaFotosRecuperadas.add(caminhoImagem);
                                listaFotosRecuperadas.put(requisicao, caminhoImagem);

                            }
                        }
                    })
                    .setOnPickCancel(new IPickCancel() {
                        @Override
                        public void onCancelClick() {

                        }
                    }).show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * preenche os spinners
     */
    private void carregarDadosSpinner() {

        String[] especies = Util.getEspecies(Objects.requireNonNull(getContext()));
        String[] sexos = getResources().getStringArray(R.array.sexos);
        String[] idades = getResources().getStringArray(R.array.idade);
        String[] portes = getResources().getStringArray(R.array.portes);
        String[] estados = Util.getEstadosJSON(getContext());

        //especies
        try {
            adapterEspecies = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, especies);
            adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEspecie.setAdapter(adapterEspecies);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //sexos
        try {
            adapterSexos = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sexos);

            adapterSexos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnSexo.setAdapter(adapterSexos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* idades */
        try {
            adapterIdades = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, idades);
            adapterIdades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnIdade.setAdapter(adapterIdades);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* portes */
        try {
            adapterPortes = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, portes);
            adapterPortes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnPorte.setAdapter(adapterPortes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*estados*/
        try {
            adapterEstados = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, estados);
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEstado.setAdapter(adapterEstados);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        try {
            for (int permissaoResultado : grantResults) {
                if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                    alertaValidacaoPermissao();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertaValidacaoPermissao() {

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getString(R.string.permissoes_negadas));
            builder.setMessage(getString(R.string.necessario_aceitar_permissoes));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.confirmar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //todo: implementar
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * esconde o teclado virtual
     *
     * @param context  contexto
     * @param editText view
     */
    public static void hideKeyboard(Context context, View editText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * método que configura as propagandas via AdMob
     */
    private void configuraAdMob() {

        //admob
        //MobileAds.initialize(this, String.valueOf(R.string.app_id));
        //teste do google
        MobileAds.initialize(getContext(), "ca-app-pub-3940256099942544~3347511713");

        //AdView
        try {
            //teste
            InterstitialAd mInterstitialAd = new InterstitialAd(Objects.requireNonNull(getContext()));
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            mInterstitialAd.show();
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    //Util.setSnackBar(layout, "intersticial loaded");
                    // TODO: 23/02/2019 descomentar proxima linha
                    //mInterstitialAd.show();
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
                    //mInterstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}