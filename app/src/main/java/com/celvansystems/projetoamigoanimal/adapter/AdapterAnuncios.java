package com.celvansystems.projetoamigoanimal.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
     *
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


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {


        if (anuncios != null) {

            final Animal anuncio = anuncios.get(i);
            //anuncioComentado = anuncio;
            if (anuncio != null) {


                configuraViewHolder(anuncio, myViewHolder);

                configuraComentarios(anuncio, myViewHolder);

                //curtidas
                atualizaCurtidas(anuncio, myViewHolder);
                //denuncias
                atualizaDenuncias(anuncio, myViewHolder);
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
        myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
        myViewHolder.nome.setText(anuncio.getNome());
        myViewHolder.idade.setText(anuncio.getIdade());
        myViewHolder.cidade.setText(anuncio.getCidade());
    }

    /**
     * auxiliar que configura os comentarios
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void configuraComentarios(final Animal anuncio, final MyViewHolder myViewHolder) {
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
    }

    /**
     * auxiliar que configura a foto do anuncio
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void configuraFotoAnuncio(final Animal anuncio, MyViewHolder myViewHolder) {
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
    }

    /**
     * configura visibilidade do texto quantidade de comentarios
     *
     * @param anuncio      animal
     * @param myViewHolder myViewHolder
     */
    private void confuguraVisibilidadeCampoComentario(Animal anuncio, MyViewHolder myViewHolder) {
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

                curtirAnuncio(myViewHolder.itemView.getContext(), myViewHolder,
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

                compartilharAnuncio(myViewHolder.itemView.getContext(), anuncio);
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

        myViewHolder.imvDenunciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                denunciarAnuncio(myViewHolder.itemView.getContext(), myViewHolder,
                        anuncio, myViewHolder.imvDenunciar);
            }
        });
    }

    private void denunciarAnuncio(final Context ctx, final RecyclerView.ViewHolder myViewHolder,
                                  final Animal anuncio, final ImageView imageView) {

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();
            List<String> listaDenuncias = new ArrayList<>();

            if (isDenunciado(anuncio)) {
                Toast.makeText(ctx, "Usuário já denunciou " + anuncio.getNome() + "!", Toast.LENGTH_SHORT).show();
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

                    imageView.setVisibility(View.GONE);

                    anuncioDenunciasRef.addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            atualizaDenuncias(anuncio, (MyViewHolder) myViewHolder);

                            //envia email sobre denuncia do anuncio
                            if (anuncio.getDenuncias().size() >= Constantes.MAX_DENUNCIAS) {
                                anuncio.remover();
                            }
                            Util.setSnackBar(((MyViewHolder) myViewHolder).layout, "Anúncio denunciado!");
                        }
                    });
                }
            }
        } else {
            Toast.makeText(myViewHolder.itemView.getContext(), "Usuário não logado!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * metodo que insere comentarios no firebase
     *
     * @param myViewHolder myviewholder
     * @param anuncio      animal
     */
    private void comentarAnuncio(final MyViewHolder myViewHolder, final Animal anuncio) {

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
                                    usuario.setNome("Anônimo");
                                }

                                //Inserindo o comentário
                                if (Util.validaTexto(texto)) {

                                    Comentario coment = new Comentario(usuario, texto, Util.getDataAtualBrasil());

                                    comentarioRef.push().setValue(coment)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    Util.setSnackBar(((MyViewHolder) myViewHolder).layout, "Comentário inserido!");
                                                    myViewHolder.edtComentar.setText(null);
                                                }
                                            });
                                } else {
                                    Util.setSnackBar(((MyViewHolder) myViewHolder).layout, "Comentário inválido!");
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
            Util.setSnackBar(((MyViewHolder) myViewHolder).layout, "Usuário não logado!");
        }
    }


        /*if (ConfiguracaoFirebase.isUsuarioLogado()) {

            final DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(anuncio.getIdAnimal())
                    .child("comentarios");

            String texto = myViewHolder.edtComentar.getText().toString();
            String nomeUsuario = Objects.requireNonNull(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser()).getDisplayName();
            Usuario usuario = new Usuario();

            if(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getPhotoUrl() != null) {
                usuario.setFoto(ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getPhotoUrl().toString());
            }
            if(nomeUsuario!=null) {
                usuario.setId(ConfiguracaoFirebase.getIdUsuario());
                usuario.setNome(nomeUsuario);
            }

            final Comentario coment = new Comentario(usuario, texto, Util.getDataAtualBrasil());

            if(Util.validaTexto(texto)){
                Task<Void> inserirComentarioRef = comentarioRef
                        .push().setValue(coment);

                inserirComentarioRef.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Util.setSnackBar(myViewHolder.layout, "Comentário inserido!");
                        myViewHolder.edtComentar.setText(null);
                    }
                });
            } else {
                Util.setSnackBar(myViewHolder.layout, "Comentário inválido!");
            }

        } else {
            Util.setSnackBar(myViewHolder.layout, "Usuário não logado!");
        }*/


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

    private void atualizaDenuncias(Animal anuncio, MyViewHolder myViewHolder) {
        try {
            if (isDenunciado(anuncio)) {
                myViewHolder.imvDenunciar.setVisibility(View.GONE);
            } else {
                // TODO: 29/04/2019 van, trocar imagem aqui->>>
                myViewHolder.imvDenunciar.setImageResource(R.drawable.ic_add_circle_outline_black_24dp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizaComentarios(int size, Animal anuncio, MyViewHolder myViewHolder) {
        try {

            if (anuncio.getListaComentarios() != null) {
                if (size > 1) {
                    myViewHolder.textViewTodosComentarios.setText(
                            ("Ver todos os " + size + " comentários"));
                } else if (size == 1) {
                    myViewHolder.textViewTodosComentarios.setText("Ver " + size + " comentário");
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
     * @param ctx          contexto
     * @param myViewHolder myViewHolder
     * @param anuncio      animal
     * @param imageView    coração vermelho
     */
    private void curtirAnuncio(final Context ctx, final RecyclerView.ViewHolder myViewHolder,
                               final Animal anuncio, final ImageView imageView) {

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();
            List<String> listaCurtidas = new ArrayList<>();

            if (isCurtido(anuncio)) {
                Toast.makeText(ctx, "Usuário já curtiu " + anuncio.getNome() + "!", Toast.LENGTH_SHORT).show();
            } else {
                if (ConfiguracaoFirebase.isUsuarioLogado()) {
                    if (anuncio.getCurtidas() != null) {
                        listaCurtidas = anuncio.getCurtidas();
                    }
                    listaCurtidas.add(usuarioAtual);
                    anuncio.setCurtidas(listaCurtidas);

                    Toast.makeText(ctx, "Anúncio curtido!", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(myViewHolder.itemView.getContext(), "Usuário não logado!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * método auxiliar para compartilhamento de anuncios
     *
     * @param ctx     contexto
     * @param anuncio animal
     */
    private void compartilharAnuncio(Context ctx, Animal anuncio) {
        try {
            // TODO: 13/02/2019 configurar quando o app for publicado do google play
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Conheça o " + anuncio.getNome() + ". Baixe o App Amigo Animal em " +
                    "https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Anúncio de animal");
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(anuncio.getFotos().get(0)));
            ctx.startActivity(Intent.createChooser(share, "Compartilhando anúncio de animal..."));

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
        ImageView imvCompartilharAnuncio, imvCurtirAnuncio, imvComentarAnuncio, imvDenunciar;
        ImageButton imbComentarAnuncio;
        EditText edtComentar;
        View layout;

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
            imvDenunciar = itemView.findViewById(R.id.imv_denunciar);

            //Para a snackBar
            layout = itemView.findViewById(R.id.constraintLayout_comentar);

            //curtir, comentar e compartilhar anuncio da tela principal
            imvCompartilharAnuncio = itemView.findViewById(R.id.imv_compartilhar_anuncio);
            imbComentarAnuncio = itemView.findViewById(R.id.imageButton_comentar);
            imvComentarAnuncio = itemView.findViewById(R.id.imv_comentar_anuncio);
            imvCurtirAnuncio = itemView.findViewById(R.id.imv_curtir_anuncio);
            edtComentar = itemView.findViewById(R.id.editText_comentar);
        }
    }
}
