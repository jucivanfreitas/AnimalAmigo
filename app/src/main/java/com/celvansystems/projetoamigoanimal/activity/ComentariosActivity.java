package com.celvansystems.projetoamigoanimal.activity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterComentarios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.celvansystems.projetoamigoanimal.model.Comentario;
import com.celvansystems.projetoamigoanimal.model.Usuario;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComentariosActivity extends AppCompatActivity {

    private Animal anuncioSelecionado;
    private AdapterComentarios adapterComentarios;
    private EditText edtComentario;
    private RecyclerView recyclercomentarios;

    private View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        //configurar toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.comentarios_titulo);

        layout = findViewById(R.id.constraint_comentarios);

        recyclercomentarios = findViewById(R.id.recyclerComentarios);
        recyclercomentarios.setItemAnimator(null);
        anuncioSelecionado = (Animal) getIntent().getSerializableExtra("anuncioSelecionado");


        if (anuncioSelecionado != null) {

            //comentarios
            List<Comentario> listaComentarios = anuncioSelecionado.getListaComentarios();
            edtComentario = findViewById(R.id.editTextComentarAnuncio);
            ImageButton imbComentario = findViewById(R.id.imageButton_comentarAnuncio);
            imbComentario.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    comentarAnuncio(anuncioSelecionado);
                    hideKeyboard(getApplicationContext(), edtComentario);
                }
            });

            //configurar recyclerview
            try {
                RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
                recyclercomentarios.setLayoutManager(lm);
                recyclercomentarios.setHasFixedSize(true);
                adapterComentarios = new AdapterComentarios(listaComentarios);
                recyclercomentarios.setAdapter(adapterComentarios);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        configuraAdMob();
    }

    /**
     * metodo que insere comentarios no firebase
     *
     * @param anuncio animal
     */
    private void comentarAnuncio(final Animal anuncio) {

        if (ConfiguracaoFirebase.isUsuarioLogado()) {

            final Context ctx = this.getApplicationContext();

            if (edtComentario.getText() != null && !edtComentario.getText().toString().equalsIgnoreCase("")) {

                final DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebase()
                        .child("meus_animais")
                        .child(anuncio.getIdAnimal())
                        .child("comentarios");

                //Dados do Usuário
                DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                        .child("usuarios");

                usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                            if (usuarios != null) {

                                UserInfo user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();

                                if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(Objects.requireNonNull(user).getUid())) {

                                    Usuario usuario = new Usuario();
                                    usuario.setId(ConfiguracaoFirebase.getIdUsuario());

                                    //Dados fora do cadastro
                                    String texto = edtComentario.getText().toString();

                                    if (usuarios.child("nome").getValue() != null) {
                                        usuario.setNome(Objects.requireNonNull(usuarios.child("nome").getValue()).toString());
                                    } else {
                                        String nomeUsuario = Objects.requireNonNull(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser()).getDisplayName();
                                        if (nomeUsuario != null) {
                                            usuario.setNome(nomeUsuario);
                                        }
                                    }
                                    if (usuarios.child("foto").getValue() != null) {
                                        usuario.setFoto(Objects.requireNonNull(usuarios.child("foto").getValue()).toString());
                                    }

                                    //Inserindo o comentário
                                    if (Util.validaTexto(texto)) {
                                        usuario.setId(ConfiguracaoFirebase.getIdUsuario());
                                        Comentario coment = new Comentario(usuario, texto, Util.getDataAtualBrasil());

                                        comentarioRef.push().setValue(coment)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        Util.setSnackBar(layout, getString(R.string.comentario_inserido));
                                                        edtComentario.setText(null);
                                                    }
                                                });
                                    } else {
                                        Util.setSnackBar(layout, ctx.getString(R.string.insira_comentario_valido));
                                    }

                                    //Update do RecyclerView
                                    comentarioRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            List<Comentario> comentsList = new ArrayList<>();
                                            for (DataSnapshot comentarios : dataSnapshot.getChildren()) {
                                                Comentario coment = new Comentario();
                                                if (comentarios != null) {

                                                    coment.setDatahora(Objects.requireNonNull(comentarios.child("datahora").getValue()).toString());
                                                    coment.setTexto(Objects.requireNonNull(comentarios.child("texto").getValue()).toString());
                                                    Usuario usuario = new Usuario();
                                                    usuario.setId(Objects.requireNonNull(comentarios.child("usuario").child("id").getValue()).toString());
                                                    usuario.setNome(Objects.requireNonNull(comentarios.child("usuario").child("nome").getValue()).toString());
                                                    //usuario.setFoto(Objects.requireNonNull(comentarios.child("usuario").child("foto").getValue()).toString());

                                                    // TODO: 05/03/2019 concluir atributos de usuario apos activity para cadastro de usuario
                                                    //usuario.setFoto(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getPhotoUrl().toString());
                                                    coment.setUsuario(usuario);
                                                    comentsList.add(coment);
                                                }
                                            }
                                            adapterComentarios = new AdapterComentarios(comentsList);
                                            recyclercomentarios.setAdapter(adapterComentarios);
                                            adapterComentarios.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                                    //Fim do update do Recycler
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                // fim dos dados do usuario

                Util.configuraNotificacoes(ctx, anuncio);
                ////////////////
                /*final DatabaseReference comentRef = ConfiguracaoFirebase.getFirebase()
                        .child("meus_animais")
                        .child(anuncio.getIdAnimal())
                        .child("comentarios");

                comentRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (anuncio.getDonoAnuncio().equalsIgnoreCase(ConfiguracaoFirebase.getIdUsuario())) {

                            Comentario coment = new Comentario();
                            int size = anuncio.getListaComentarios().size() - 1;
                            String texto = anuncio.getListaComentarios().get(size).getTexto();
                            coment.setTexto(texto);

                            int sizeComentsNotificacoes = Util.comentariosNotificacoes.size();
                            if ((sizeComentsNotificacoes == 0 || !Util.comentariosNotificacoes.get(sizeComentsNotificacoes-1).equalsIgnoreCase(texto))
                                    && !anuncio.getDonoAnuncio().equalsIgnoreCase(ConfiguracaoFirebase.getIdUsuario())) {
                                Util.createNotificationMessage(ctx, ctx.getString(R.string.novo_comentario), coment.getTexto(), anuncio);
                                //ultimoComentario = texto;
                                Util.comentariosNotificacoes.add(texto);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });*/
                ///////////////////////


            } else {
                Util.setSnackBar(layout, ctx.getString(R.string.insira_comentario_valido));
            }

        } else {
            Util.setSnackBar(layout, getString(R.string.usuario_nao_logado));
        }
    }

    /**
     * método que configura as propagandas via AdMob
     */
    private void configuraAdMob() {

        //admob
        //MobileAds.initialize(this, String.valueOf(R.string.app_id));
        //teste do google
        MobileAds.initialize(getApplicationContext(), getString(R.string.mobileadsIdTeste));

        //AdView
        try {
            //banner teste
            final AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.testeDeviceId))
                    .build();

            AdView adView = findViewById(R.id.banner_comentarios);
            //final AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    // Toast.makeText(this, "failed to load. " +
                    //        adRequest.getContentUrl(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the user is about to return.
                    // to the app after tapping on an ad.
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Context context, View editText) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
