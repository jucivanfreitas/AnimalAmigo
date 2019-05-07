package com.celvansystems.projetoamigoanimal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import static android.R.layout.simple_spinner_item;

public class ComplementoLoginActivity extends AppCompatActivity {

    private ImageView imvFoto;
    private EditText edtNome;
    private EditText edtTelefone;
    private EditText edtDataNascimento;
    private Spinner spnSexo;
    private Spinner spnPais;
    private Spinner spnEstado;
    private Spinner spnCidade;
    private EditText edtProfissao;
    private EditText edtSobreMim;
    private Button btnFinalizarComplemento;
    private View layout;
    private ArrayAdapter<String> adapterCidades;

    private ConstraintLayout layout_inserir_nome;
    private ConstraintLayout layout_inserir_foto;
    private ConstraintLayout layout_inserir_cidade;
    private ConstraintLayout layout_inserir_fone;
    private Button btn_proximo_nome;
    private Button btn_proximo_fone;
    private Button btn_proximo_foto;

    private Button btn_voltar_foto;
    private Button btn_voltar_fone;
    private Button btn_voltar_cidade;

    Usuario usuario = new Usuario();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complemento_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

        try {

            layout = findViewById(R.id.const_layout_complemento);

            imvFoto = findViewById(R.id.imv_foto_complemento);
            edtNome = findViewById(R.id.edt_cad_nome);
            edtTelefone = findViewById(R.id.edt_cad_telefone);
            edtDataNascimento = findViewById(R.id.edt_data_nascimento);
            spnSexo = findViewById(R.id.spinner_cad_Sexo);
            spnPais = findViewById(R.id.spinner_cad_pais_complemento);
            spnEstado = findViewById(R.id.spinner_cad_estado_complemento);
            spnCidade = findViewById(R.id.spinner_cad_cidade_complemento);
            edtProfissao = findViewById(R.id.editText_cad_profissao);
            edtSobreMim = findViewById(R.id.editText_cad_sobremim);

            btn_proximo_nome = findViewById(R.id.btn_proximo_nome);
            btn_proximo_fone = findViewById(R.id.btn_proximo_fone);
            btn_proximo_foto = findViewById(R.id.btn_proximo_foto);
            btnFinalizarComplemento = findViewById(R.id.btn_finalizar_complemento);

            btn_voltar_fone = findViewById(R.id.btn_volta_fone);
            btn_voltar_foto = findViewById(R.id.btn_voltar_foto);
            btn_voltar_cidade = findViewById(R.id.btn_voltar_cidade);

            layout_inserir_nome = findViewById(R.id.layout_inserir_nome);
            layout_inserir_foto = findViewById(R.id.layout_inserir_foto);
            layout_inserir_cidade = findViewById(R.id.layout_inserir_cidade);
            layout_inserir_fone = findViewById(R.id.layout_inserir_fone);

            preencheDadosSpinners();

            //edtNome.setText(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getDisplayName());

            btn_proximo_nome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_nome.setVisibility(View.INVISIBLE);
                    layout_inserir_fone.setVisibility(View.VISIBLE);
                    //seta nome do usuario
                    usuario.setNome(edtNome.getText().toString());
                }
            });

            btn_proximo_fone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_fone.setVisibility(View.INVISIBLE);
                    layout_inserir_foto.setVisibility(View.VISIBLE);
                    //seta telefone
                    usuario.setTelefone(edtTelefone.getText().toString());
                }
            });

            btn_proximo_foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    layout_inserir_foto.setVisibility(View.INVISIBLE);
                    layout_inserir_cidade.setVisibility(View.VISIBLE);
                    // TODO: 06/05/2019 configurar foto do usuario
                    usuario.setFoto("");
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

                }
            });

            imvFoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            ArrayList<String> cidadesLista = Util.getCidadesLista("AC", this);
            cidadesLista.remove(0);

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
}
