package com.celvansystems.projetoamigoanimal.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.model.Usuario;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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
    private Button btnCadastrar;
    private View layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complemento_login);

        inicializarComponentes();
    }

    private void inicializarComponentes() {

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
        btnCadastrar = findViewById(R.id.btn_cad_complemento);

        edtNome.setText(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getDisplayName());

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public Usuario getUsuarioDaActivity(){
        return null;
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

            imageStream = Objects.requireNonNull(this.getContentResolver().openInputStream(
                    selectedImage));

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
}
