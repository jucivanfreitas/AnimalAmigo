package com.celvansystems.projetoamigoanimal.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.ComentariosActivity;
import com.celvansystems.projetoamigoanimal.activity.DetalhesAnimalActivity;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.celvansystems.projetoamigoanimal.model.Comentario;
import com.celvansystems.projetoamigoanimal.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder>
        implements Serializable {

    private List<Animal> anuncios;

    /**
     * construtor
     * @param anuncios lista de animais
     */
    public AdapterAnuncios(List<Animal> anuncios) {
        this.anuncios = anuncios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_anuncios, viewGroup, false);
        return new MyViewHolder(item);
    }

    /**
     * só habilitar o menu quando o usuario está logado
     * @param myViewHolder myViewHolder
     */
    private void configuracoesMaisOpcoes(AdapterAnuncios.MyViewHolder myViewHolder) {

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            myViewHolder.imvMaisOpcoesAnuncios.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.imvMaisOpcoesAnuncios.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        if (anuncios != null) {

            final Animal anuncio = anuncios.get(i);
            //anuncioComentado = anuncio;
            if (anuncio != null) {

                configuracoesMaisOpcoes(myViewHolder);

                configuraViewHolder(anuncio, myViewHolder);

                configuraComentarios(anuncio, myViewHolder);

                //curtidas
                atualizaCurtidas(anuncio, myViewHolder);
                //denuncias
                //atualizaDenuncias(anuncio, myViewHolder);
                //Foto do anúncio
                configuraFotoAnuncio(anuncio, myViewHolder);
                //Acoes dos botoes
                configuraAcoes(myViewHolder, anuncio);
                //Texto da quantidade de comentarios
                confuguraVisibilidadeCampoComentario(anuncio, myViewHolder);
            }
        }
    }

    /**
     * auxiliar que preenche campos com dados do anuncio
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void configuraViewHolder(Animal anuncio, MyViewHolder myViewHolder) {

        try {
            myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
            myViewHolder.nome.setText(anuncio.getNome());
            myViewHolder.idade.setText(anuncio.getIdade());
            myViewHolder.cidade.setText(anuncio.getCidade());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * auxiliar que configura os comentarios
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void configuraComentarios(final Animal anuncio, final MyViewHolder myViewHolder) {

        try {
            //comentarios
            DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(anuncio.getIdAnimal())
                    .child("comentarios");

            comentarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int size = (int) dataSnapshot.getChildrenCount();
                    atualizaComentarios(size, anuncio, myViewHolder);

                    List<Comentario> comentsList = new ArrayList<>();
                    for (DataSnapshot comentarios : dataSnapshot.getChildren()) {
                        Comentario coment = new Comentario();

                        if (comentarios != null) {
                            coment.setDatahora(Objects.requireNonNull(comentarios.child("datahora").getValue()).toString());
                            coment.setTexto(Objects.requireNonNull(comentarios.child("texto").getValue()).toString());
                            Usuario usuario = new Usuario();
                            usuario.setNome(Objects.requireNonNull(comentarios.child("usuario").child("nome").getValue()).toString());
                            // TODO: 05/03/2019 concluir atributos de usuario apos activity para cadastro de usuario
                            //usuario.setFoto(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getPhotoUrl().toString());
                            coment.setUsuario(usuario);
                            comentsList.add(coment);
                        }
                    }
                    anuncio.setListaComentarios(comentsList);
                    atualizaComentarios(anuncio.getListaComentarios().size(), anuncio, myViewHolder);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * auxiliar que configura a foto do anuncio
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void configuraFotoAnuncio(final Animal anuncio, MyViewHolder myViewHolder) {

        try {
            //pega a primeira imagem cadastrada
            List<String> urlFotos = anuncio.getFotos();

            if (urlFotos != null && urlFotos.size() > 0) {
                String urlCapa = urlFotos.get(0);

                Picasso.get().load(urlCapa).into(myViewHolder.foto);

                // ação de clique na foto do anuncio
                myViewHolder.foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detalhesIntent = new Intent(v.getContext(), DetalhesAnimalActivity.class);
                        detalhesIntent.putExtra("anuncioSelecionado", anuncio);
                        v.getContext().startActivity(detalhesIntent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * configura visibilidade do texto quantidade de comentarios
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void confuguraVisibilidadeCampoComentario(Animal anuncio, MyViewHolder myViewHolder) {

        try {
            //visibilidade do campo comentário
            if (ConfiguracaoFirebase.isUsuarioLogado()) {
                myViewHolder.edtComentar.setVisibility(View.VISIBLE);
                myViewHolder.imbComentarAnuncio.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.edtComentar.setVisibility(View.GONE);
                myViewHolder.imbComentarAnuncio.setVisibility(View.GONE);
            }
            if (anuncio.getListaComentarios() != null && anuncio.getListaComentarios().size() == 0) {
                myViewHolder.textViewTodosComentarios.setText(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * configura acoes do elementos da tela
     *
     * @param myViewHolder myViewHolder
     * @param anuncio      animal
     */
    private void configuraAcoes(final MyViewHolder myViewHolder, final Animal anuncio) {

        //acao de clique no botao curtir anuncio
        myViewHolder.imvCurtirAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                curtirAnuncio(myViewHolder,
                        anuncio, myViewHolder.imvCurtirAnuncio);
            }
        });

        myViewHolder.imvComentarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent comentariosIntent = new Intent(v.getContext(), ComentariosActivity.class);
                comentariosIntent.putExtra("anuncioSelecionado", anuncio);
                v.getContext().startActivity(comentariosIntent);
            }
        });

        //acao de clique no botao comentar anuncio
        myViewHolder.imbComentarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comentarAnuncio(myViewHolder,
                        anuncio);
            }
        });

        //acao de clique no botao compartilhar anuncio
        myViewHolder.imvCompartilharAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                compartilharAnuncio(myViewHolder, anuncio);
            }
        });

        myViewHolder.textViewTodosComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent comentariosIntent = new Intent(v.getContext(), ComentariosActivity.class);
                comentariosIntent.putExtra("anuncioSelecionado", anuncio);
                v.getContext().startActivity(comentariosIntent);
            }
        });

        myViewHolder.imvMaisOpcoesAnuncios.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(final View v) {

                final Context ctx = myViewHolder.itemView.getContext();

                List<String> opcoesLista = new ArrayList<>();

                if (ConfiguracaoFirebase.isUsuarioLogado()) {
                    opcoesLista.add(ctx.getString(R.string.curtir));
                    opcoesLista.add(ctx.getString(R.string.comentar));
                    opcoesLista.add(ctx.getString(R.string.adotar));
                    opcoesLista.add(ctx.getString(R.string.denunciar));
                }

                final String[] opcoes = new String[opcoesLista.size()];

                for (int i = 0; i < opcoesLista.size(); i++) {
                    opcoes[i] = opcoesLista.get(i);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(myViewHolder.imvMaisOpcoesAnuncios.getContext());

                builder.setItems(opcoes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (ctx.getString(R.string.curtir).equalsIgnoreCase(opcoes[which])) {

                            curtirAnuncio(myViewHolder,
                                    anuncio, myViewHolder.imvCurtirAnuncio);

                        } else if (ctx.getString(R.string.comentar).equalsIgnoreCase(opcoes[which])) {

                            Intent detalhesIntent = new Intent(v.getContext(), ComentariosActivity.class);
                            detalhesIntent.putExtra("anuncioSelecionado", anuncio);
                            v.getContext().startActivity(detalhesIntent);

                        } else if (ctx.getString(R.string.adotar).equalsIgnoreCase(opcoes[which])) {

                            Intent detalhesIntent = new Intent(v.getContext(), DetalhesAnimalActivity.class);
                            detalhesIntent.putExtra("anuncioSelecionado", anuncio);
                            v.getContext().startActivity(detalhesIntent);

                        } else if (ctx.getString(R.string.denunciar).equalsIgnoreCase(opcoes[which])) {
                            new AlertDialog.Builder(myViewHolder.itemView.getContext())
                                    .setMessage(ctx.getText(R.string.tem_certeza_denunciar_anuncio))
                                    .setCancelable(false)
                                    .setPositiveButton(ctx.getText(R.string.sim), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            denunciarAnuncio(myViewHolder,
                                                    anuncio);
                                        }
                                    })
                                    .setNegativeButton(ctx.getText(R.string.nao), null)
                                    .show();
                        }
                    }

                });
                builder.show();
            }
        });
    }

    /**
     * lógica da denuncia de anuncios
     * @param myViewHolder myViewHolder
     * @param anuncio Animal
     */
    private void denunciarAnuncio(final RecyclerView.ViewHolder myViewHolder,
                                  final Animal anuncio) {

        final Context ctx = myViewHolder.itemView.getContext();

        if (ConfiguracaoFirebase.isUsuarioLogado()) {

            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();
            List<String> listaDenuncias = new ArrayList<>();

            // se o anuncio ja tiver sido denunciado
            if (isDenunciado(anuncio)) {

                Util.setSnackBar(((MyViewHolder) myViewHolder).layout,
                        ctx.getString(R.string.usuario_ja_denunciou) + anuncio.getNome() + "!");

            } else {
                if (ConfiguracaoFirebase.isUsuarioLogado()) {
                    if (anuncio.getDenuncias() != null) {
                        listaDenuncias = anuncio.getDenuncias();
                    }
                    listaDenuncias.add(usuarioAtual);
                    anuncio.setDenuncias(listaDenuncias);

                    Task<Void> anuncioDenunciasRef = anuncioRef
                            .child(anuncio.getIdAnimal())
                            .child("denuncias")
                            .setValue(listaDenuncias);

                    anuncioDenunciasRef.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            //envia email sobre denuncia do anuncio
                            if (anuncio.getDenuncias().size() >= Constantes.MAX_DENUNCIAS) {
                                anuncio.remover();
                            }
                            Util.setSnackBar(((MyViewHolder) myViewHolder).layout, ctx.getString(R.string.anuncio_denunciado));
                        }
                    });
                }
            }
        } else {
            Util.setSnackBar(((MyViewHolder) myViewHolder).layout, ctx.getString(R.string.usuario_nao_logado));
        }
    }

    /**
     * metodo que insere comentarios no firebase
     *
     * @param myViewHolder myviewholder
     * @param anuncio      animal
     */
    private void comentarAnuncio(final MyViewHolder myViewHolder, final Animal anuncio) {

        final Context ctx = myViewHolder.itemView.getContext();

        if (ConfiguracaoFirebase.isUsuarioLogado()) {

            final DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(anuncio.getIdAnimal())
                    .child("comentarios");

            //Dados do Usuário
            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                    .child("usuarios");

            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot usuarios : dataSnapshot.getChildren()) {

                        if (usuarios != null) {

                            UserInfo user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();

                            if (Objects.requireNonNull(usuarios.child("id").getValue()).toString().equalsIgnoreCase(Objects.requireNonNull(user).getUid())) {

                                Usuario usuario = new Usuario();
                                usuario.setId(ConfiguracaoFirebase.getIdUsuario());

                                //Dados fora do cadastro
                                String texto = myViewHolder.edtComentar.getText().toString();

                                if (usuarios.child("nome").getValue() != null) {
                                    usuario.setNome(Objects.requireNonNull(usuarios.child("nome").getValue()).toString());
                                } else if (user.getDisplayName() != null) {
                                    String nomeUsuario = user.getDisplayName();
                                    usuario.setNome(nomeUsuario);

                                } else {
                                    usuario.setNome(ctx.getString(R.string.anonimo));
                                }

                                //Inserindo o comentário
                                if (Util.validaTexto(texto)) {

                                    Comentario coment = new Comentario(usuario, texto, Util.getDataAtualBrasil());

                                    comentarioRef.push().setValue(coment)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Util.setSnackBar(myViewHolder.layout, ctx.getString(R.string.comentario_inserido));
                                                    myViewHolder.edtComentar.setText(null);
                                                }
                                            });
                                } else {
                                    Util.setSnackBar(myViewHolder.layout, ctx.getString(R.string.comentario_invalido));
                                }
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            // fim dos dados do usuario

        } else {
            Util.setSnackBar(myViewHolder.layout, ctx.getString(R.string.usuario_nao_logado));
        }
    }

    private void atualizaCurtidas(Animal anuncio, MyViewHolder myViewHolder) {
        try {
            if (isCurtido(anuncio)) {
                myViewHolder.imvCurtirAnuncio.setImageResource(R.drawable.ic_coracao_vermelho_24dp);
            } else {
                myViewHolder.imvCurtirAnuncio.setImageResource(R.drawable.ic_coracao_branco_24dp);
            }
            if (anuncio.getCurtidas() != null) {
                int numeroCurtidas = anuncio.getCurtidas().size();
                if (numeroCurtidas == 1) {
                    myViewHolder.textViewCurtidas.setText(R.string.curtida);
                } else if (numeroCurtidas > 1) {
                    myViewHolder.textViewCurtidas.setText(R.string.curtidas);
                }

                myViewHolder.numeroCurtidas.setText(String.valueOf(numeroCurtidas));

            } else {
                myViewHolder.numeroCurtidas.setText("");
                myViewHolder.textViewCurtidas.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizaComentarios(int size, Animal anuncio, MyViewHolder myViewHolder) {
        try {
            
            Context ctx = myViewHolder.itemView.getContext();

            if (anuncio.getListaComentarios() != null) {
                if (size > 1) {
                    myViewHolder.textViewTodosComentarios.setText(
                            String.format(ctx.getString(R.string.ver_todos_comentarios), size));
                } else if (size == 1) {
                    myViewHolder.textViewTodosComentarios.setText(
                            String.format(ctx.getString(R.string.ver_comentario), size));
                }
                myViewHolder.textViewTodosComentarios.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.textViewTodosComentarios.setText(null);
                myViewHolder.textViewTodosComentarios.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método auxiliar que processa a curtida do usuários
     *
     * @param myViewHolder myViewHolder
     * @param anuncio      animal
     * @param imageView    coração vermelho
     */
    private void curtirAnuncio(final RecyclerView.ViewHolder myViewHolder,
                               final Animal anuncio, final ImageView imageView) {

        final Context ctx = myViewHolder.itemView.getContext();

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();
            List<String> listaCurtidas = new ArrayList<>();

            if (isCurtido(anuncio)) {
                Util.setSnackBar(((MyViewHolder) myViewHolder).layout, ctx.getString(R.string.usuario_ja_curtiu) + anuncio.getNome() + "!");
            } else {
                if (ConfiguracaoFirebase.isUsuarioLogado()) {
                    if (anuncio.getCurtidas() != null) {
                        listaCurtidas = anuncio.getCurtidas();
                    }
                    listaCurtidas.add(usuarioAtual);
                    anuncio.setCurtidas(listaCurtidas);

                    Util.setSnackBar(((MyViewHolder) myViewHolder).layout, ctx.getString(R.string.anuncio_curtido));

                    Task<Void> anuncioCurtidasRef = anuncioRef
                            .child(anuncio.getIdAnimal())
                            .child("curtidas")
                            .setValue(listaCurtidas);

                    imageView.setImageResource(R.drawable.ic_coracao_vermelho_24dp);

                    anuncioCurtidasRef.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            atualizaCurtidas(anuncio, (MyViewHolder) myViewHolder);
                        }
                    });
                }
            }
        } else {
            /*Intent intent = new Intent(myViewHolder.itemView.getContext(), LoginActivity.class);
            myViewHolder.itemView.getContext().startActivity(intent);*/
            Util.setSnackBar(((MyViewHolder) myViewHolder).layout, ctx.getString(R.string.usuario_nao_logado));
        }
    }

    /**
     * método auxiliar para compartilhamento de anuncios
     * @param myViewHolder myViewHolder
     * @param anuncio Animal
     */
    private void compartilharAnuncio(MyViewHolder myViewHolder, Animal anuncio) {

        try {
            // TODO: 13/02/2019 configurar quando o app for publicado do google play

            Drawable mDrawable = myViewHolder.foto.getDrawable();
            Bitmap mBitmap = ((BitmapDrawable) mDrawable).getBitmap();

            String path = MediaStore.Images.Media.insertImage(myViewHolder.itemView.getContext()
                    .getContentResolver(), mBitmap, null, null);
            Uri uri = Uri.parse(path);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_TEXT, "Instale o App Pet Amigo e conheça o "+ anuncio.getNome()+ "! Disponível em " +
                    "https://play.google.com/store/apps/details?id=" + Constantes.APPLICATION_ID +"\n\n");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            myViewHolder.itemView.getContext().startActivity(Intent.createChooser(intent, "Compartilhando imagem..."));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * método auxiliar que verifica se o usuário atual já curtiu o anúncio
     *
     * @param animal anuncio
     * @return boolean
     */
    private boolean isCurtido(Animal animal) {
        boolean retorno = false;

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();

            if (animal != null && animal.getCurtidas() != null)
                if (animal.getCurtidas().contains(usuarioAtual)) {
                    retorno = true;
                }
        }
        return retorno;
    }

    private boolean isDenunciado(Animal animal) {
        boolean retorno = false;

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();

            if (animal != null && animal.getDenuncias() != null)
                if (animal.getDenuncias().contains(usuarioAtual)) {
                    retorno = true;
                }
        }
        return retorno;
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    /**
     * MyViewHolder
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dataCadastro;
        TextView nome;
        TextView idade;
        TextView cidade;
        TextView numeroCurtidas;
        TextView textViewCurtidas;
        TextView textViewTodosComentarios;
        ImageView foto;
        ImageView imvCompartilharAnuncio, imvCurtirAnuncio, imvComentarAnuncio;
        //ImageView imvDenunciar;
        ImageButton imbComentarAnuncio;
        EditText edtComentar;
        View layout;
        ImageView imvMaisOpcoesAnuncios;

        MyViewHolder(View itemView) {
            super(itemView);

            //elementos
            dataCadastro = itemView.findViewById(R.id.textDataCadastro);
            nome = itemView.findViewById(R.id.txv_nome_meus_anuncios);
            idade = itemView.findViewById(R.id.textIdade_meus_anuncios);
            foto = itemView.findViewById(R.id.imageAnuncio);
            cidade = itemView.findViewById(R.id.textCidadePrincipal_meus_anuncios);
            numeroCurtidas = itemView.findViewById(R.id.txv_num_curtidas);
            textViewCurtidas = itemView.findViewById(R.id.textViewCurtidas);
            textViewTodosComentarios = itemView.findViewById(R.id.ttv_todos_comentarios);

            //Para a snackBar
            layout = itemView.findViewById(R.id.constraintLayout_comentar);

            //curtir, comentar e compartilhar anuncio da tela principal
            imvCompartilharAnuncio = itemView.findViewById(R.id.imv_compartilhar_anuncio);
            imbComentarAnuncio = itemView.findViewById(R.id.imageButton_comentar);
            imvComentarAnuncio = itemView.findViewById(R.id.imv_comentar_anuncio);
            imvCurtirAnuncio = itemView.findViewById(R.id.imv_curtir_anuncio);
            edtComentar = itemView.findViewById(R.id.editText_comentar);
            imvMaisOpcoesAnuncios = itemView.findViewById(R.id.imv_mais_opcoes_anuncios);
        }
    }
}
