package com.celvansystems.projetoamigoanimal.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Permissoes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Usuario;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;

public class ComplementoLoginActivity extends AppCompatActivity {

    private ImageView imvFoto;
    private EditText edtNome;
    private EditText edtTelefone;
    //private EditText edtDataNascimento;
    //private Spinner spnSexo;
    private Spinner spnPais;
    private Spinner spnEstado;
    private Spinner spnCidade;
    private View layout;
    private ArrayAdapter<String> adapterCidades;
    private StorageReference storage;
    private AlertDialog dialog;

    private ConstraintLayout layout_inserir_nome;
    private ConstraintLayout layout_inserir_foto;
    private ConstraintLayout layout_inserir_cidade;
    private ConstraintLayout layout_inserir_fone;

    //private String urlFoto;

    //Permissoes
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complemento_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        try {
            usuario = new Usuario();

            layout = findViewById(R.id.const_layout_complemento);
            storage = ConfiguracaoFirebase.getFirebaseStorage();

            imvFoto = findViewById(R.id.imv_foto_complemento);
            edtNome = findViewById(R.id.edt_cad_nome);
            edtTelefone = findViewById(R.id.edt_cad_telefone);
            //edtDataNascimento = findViewById(R.id.edt_data_nascimento);
            //spnSexo = findViewById(R.id.spinner_cad_Sexo);
            spnPais = findViewById(R.id.spinner_cad_pais_complemento);
            spnEstado = findViewById(R.id.spinner_cad_estado_complemento);
            spnCidade = findViewById(R.id.spinner_cad_cidade_complemento);
            //EditText edtProfissao = findViewById(R.id.editText_cad_profissao);
            //EditText edtSobreMim = findViewById(R.id.editText_cad_sobremim);

            Button btn_proximo_nome = findViewById(R.id.btn_proximo_nome);
            Button btn_proximo_fone = findViewById(R.id.btn_proximo_fone);
            Button btn_proximo_foto = findViewById(R.id.btn_proximo_foto);
            Button btnFinalizarComplemento = findViewById(R.id.btn_finalizar_complemento);

            Button btn_voltar_fone = findViewById(R.id.btn_volta_fone);
            Button btn_voltar_foto = findViewById(R.id.btn_voltar_foto);
            Button btn_voltar_cidade = findViewById(R.id.btn_voltar_cidade);

            layout_inserir_nome = findViewById(R.id.layout_inserir_nome);
            layout_inserir_foto = findViewById(R.id.layout_inserir_foto);
            layout_inserir_cidade = findViewById(R.id.layout_inserir_cidade);
            layout_inserir_fone = findViewById(R.id.layout_inserir_fone);

            preencheDadosSpinners();

            btn_proximo_nome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_nome.setVisibility(View.INVISIBLE);
                    layout_inserir_fone.setVisibility(View.VISIBLE);
                }
            });

            btn_proximo_fone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_fone.setVisibility(View.INVISIBLE);
                    layout_inserir_foto.setVisibility(View.VISIBLE);
                }
            });

            btn_proximo_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_foto.setVisibility(View.INVISIBLE);
                    layout_inserir_cidade.setVisibility(View.VISIBLE);
                }
            });

            btn_voltar_fone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_nome.setVisibility(View.VISIBLE);
                    layout_inserir_fone.setVisibility(View.INVISIBLE);
                }
            });

            btn_voltar_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_foto.setVisibility(View.INVISIBLE);
                    layout_inserir_fone.setVisibility(View.VISIBLE);
                }
            });

            btn_voltar_cidade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_cidade.setVisibility(View.INVISIBLE);
                    layout_inserir_foto.setVisibility(View.VISIBLE);
                }
            });

            btnFinalizarComplemento.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Id
                    usuario.setId(ConfiguracaoFirebase.getIdUsuario());
                    //Email
                    usuario.setEmail(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getEmail());
                    //Nome
                    if (edtNome.getText() != null && !edtNome.getText().toString().equalsIgnoreCase("")) {
                        usuario.setNome(edtNome.getText().toString());
                    }
                    //Telefone
                    if (edtTelefone.getText() != null && !edtTelefone.getText().toString().equalsIgnoreCase("")) {
                        usuario.setTelefone(edtTelefone.getText().toString());
                    }
                    //Pais
                    usuario.setPais(spnPais.getSelectedItem().toString());
                    //Estado
                    if (spnEstado.getSelectedItem() != null && !spnEstado.getSelectedItem().toString().equalsIgnoreCase("")) {
                        usuario.setUf(spnEstado.getSelectedItem().toString());
                    }
                    //Cidade
                    if (spnCidade.getSelectedItem() != null && !spnCidade.getSelectedItem().toString().equalsIgnoreCase("")) {
                        usuario.setCidade(spnCidade.getSelectedItem().toString());
                    }

                    salvarUsuario(usuario);
                }
            });

            imvFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    escolherImagem();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Validar permissões
        Permissoes.validarPermissoes(permissoes, this, 1);
    }

    private void preencheDadosSpinners() {

        try {
            //spinner países//////////////
            ArrayList<String> paisesLista = new ArrayList<>();
            paisesLista.add("BR");

            ArrayAdapter<String> adapterPaises = new ArrayAdapter<>(this, simple_spinner_item, paisesLista);
            adapterPaises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnPais.setAdapter(adapterPaises);
            /////////////////////////////

            ArrayList<String> estadosLista = Util.getEstadosLista(this);
            estadosLista.remove(0);
            estadosLista.add(0, "");
            ArrayList<String> cidadesLista = Util.getCidadesLista("AC", this);
            cidadesLista.remove(0);
            cidadesLista.add(0, "");

            //cidades
            adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, cidadesLista);
            ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, simple_spinner_item, estadosLista);
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnEstado.setAdapter(adapterEstados);
            spnCidade.setAdapter(adapterCidades);

            spnEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        setAdapterSpinnerCidade();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAdapterSpinnerCidade() {
        try {
            ArrayList<String> cidadesLista;
            String estadoSelecionado = spnEstado.getSelectedItem().toString();

            cidadesLista = Util.getCidadesLista(estadoSelecionado, this);
            cidadesLista.remove(0);
            cidadesLista.add(0, "");

            adapterCidades = new ArrayAdapter<>(this, simple_spinner_item, cidadesLista);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();

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

            imageStream = Objects.requireNonNull(this.getContentResolver().openInputStream(
                    selectedImage));

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
     * ImagePick
     */
    public void escolherImagem() {
        try {

            PickSetup setup = new PickSetup()
                    .setTitle(getString(R.string.escolha))
                    .setFlip(true)
                    .setMaxSize(Constantes.PICK_MAX_SIZE)
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
            //PickImageDialog.build(setup).show(this);
            PickImageDialog.build(setup)
                    .setOnPickResult(new IPickResult() {
                        @Override
                        public void onPickResult(PickResult r) {
                            if (r.getError() == null) {
                                Log.d("INFO1", "555");
                                Uri imagemSelecionada = r.getUri();
                                String caminhoImagem = Objects.requireNonNull(imagemSelecionada).toString();

                                imvFoto.setImageURI(r.getUri());
                                //urlFoto = caminhoImagem;
                                usuario.setFoto(caminhoImagem);
                            }
                        }
                    })
                    .setOnPickCancel(new IPickCancel() {
                        @Override
                        public void onCancelClick() {

                        }
                    }).show(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarFotosStorage(final Usuario usuario, String url) {

        try {
            if (url != null) {
                //cria nó do storage
                final StorageReference imagemUsuario = storage
                        .child("imagens")
                        .child("usuarios")
                        .child(usuario.getId())
                        .child("perfil");

                Uri selectedImage = Uri.parse(url);
                //imagem comprimida
                byte[] byteArray = comprimirImagem(selectedImage);
                UploadTask uploadTask = imagemUsuario.putBytes(byteArray);

                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw Objects.requireNonNull(task.getException());
                        }

                        // Continue with the task to get the download URL
                        return imagemUsuario.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri firebaseUrl = task.getResult();
                            String urlConvertida = Objects.requireNonNull(firebaseUrl).toString();

                            usuario.setFoto(urlConvertida);
                            usuario.salvar();
                            Util.setSnackBar(layout, getString(R.string.sucesso_ao_fazer_upload));

                            dialog.dismiss();

                            startActivity(new Intent(ComplementoLoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Util.setSnackBar(layout, getString(R.string.falha_upload));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            } else {
                usuario.salvar();
                Util.setSnackBar(layout, getString(R.string.sucesso_ao_fazer_upload));

                dialog.dismiss();

                startActivity(new Intent(ComplementoLoginActivity.this, MainActivity.class));
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void salvarUsuario(Usuario usuario) {

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage(R.string.salvando_usuario)
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //salvar imagem no storage
        try {
            String urlImagem = usuario.getFoto();
            salvarFotosStorage(usuario, urlImagem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
