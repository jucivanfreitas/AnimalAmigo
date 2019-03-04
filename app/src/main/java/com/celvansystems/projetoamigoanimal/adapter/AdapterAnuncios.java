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
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.celvansystems.projetoamigoanimal.model.Comentario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        if(anuncios != null) {

            final Animal anuncio = anuncios.get(i);

            if(anuncio != null) {
                myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
                myViewHolder.nome.setText(anuncio.getNome());
                myViewHolder.idade.setText(anuncio.getIdade());
                myViewHolder.cidade.setText(anuncio.getCidade());

                //comentarios
                myViewHolder.textViewTodosComentarios.setVisibility(View.GONE);
                if(anuncio.getListaComentarios()!= null) {
                    int qtdeComentarios = anuncio.getListaComentarios().size();
                    if(qtdeComentarios > 1) {
                        myViewHolder.textViewTodosComentarios.setVisibility(View.VISIBLE);
                        myViewHolder.textViewTodosComentarios.setText(String.valueOf(R.string.ver_todos_os) + qtdeComentarios + R.string.comentarios);
                    } else if (qtdeComentarios == 1) {
                        myViewHolder.textViewTodosComentarios.setVisibility(View.VISIBLE);
                        myViewHolder.textViewTodosComentarios.setText(anuncio.getListaComentarios().get(0).getTexto());
                    }
                }

                //verifica se o animal foi curtido pelo usuario atual
                atualizaCurtidas(anuncio, myViewHolder);

                //pega a primeira imagem cadastrada
                List<String> urlFotos = anuncio.getFotos();

                if(urlFotos != null && urlFotos.size() > 0) {
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
                //acao de clique no botao curtir anuncio
                myViewHolder.imvCurtirAnuncio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        curtirAnuncio(myViewHolder.itemView.getContext(), myViewHolder,
                                anuncio, myViewHolder.imvCurtirAnuncio);
                    }
                });

                //acao de clique no botao comentar anuncio
                myViewHolder.imbComentarAnuncio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        comentarAnuncio (myViewHolder,
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
                //visibilidade do campo comentário
                if(ConfiguracaoFirebase.isUsuarioLogado()) {
                    myViewHolder.edtComentar.setVisibility(View.VISIBLE);
                    myViewHolder.imbComentarAnuncio.setVisibility(View.VISIBLE);
                } else {
                    myViewHolder.edtComentar.setVisibility(View.INVISIBLE);
                    myViewHolder.imbComentarAnuncio.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * metodo que insere comentarios no firebase
     * @param myViewHolder myviewholder
     * @param anuncio animal
     */
    private void comentarAnuncio(final MyViewHolder myViewHolder, final Animal anuncio) {

        if (ConfiguracaoFirebase.isUsuarioLogado()) {

            final DatabaseReference comentarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais")
                    .child(anuncio.getIdAnimal())
                    .child("comentarios");

            String texto = myViewHolder.edtComentar.getText().toString();
            String idUsuario = ConfiguracaoFirebase.getIdUsuario();
            final Comentario coment = new Comentario(idUsuario, texto, Util.getDataAtualBrasil());

            Task<Void> inserirComentarioRef = comentarioRef
                    .push().setValue(coment);

            inserirComentarioRef.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Util.setSnackBar(myViewHolder.layout, "Comentário inserido!");
                    comentarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // get total available quest
                            int size = (int) dataSnapshot.getChildrenCount();
                            atualizaComentarios(size, anuncio, myViewHolder);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    myViewHolder.edtComentar.setText(null);
                }
            });

        } else {
            Toast.makeText(myViewHolder.itemView.getContext(), "Usuário não logado!", Toast.LENGTH_LONG).show();
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
        } catch (Exception e) {e.printStackTrace();}
    }

    @SuppressLint("SetTextI18n")
    private void atualizaComentarios(int size, Animal anuncio, MyViewHolder myViewHolder) {
        try {

            if (anuncio.getListaComentarios() != null) {
                if(size > 1) {
                    myViewHolder.textViewTodosComentarios.setText(
                            String.valueOf(R.string.ver_todos_os) + size + R.string.comentarios);
                } else if (size == 1) {
                    myViewHolder.textViewTodosComentarios.setText(String.valueOf(R.string.ver) + size + R.string.comentario);
                }
                myViewHolder.textViewTodosComentarios.setVisibility(View.VISIBLE);
            } else {
                myViewHolder.textViewTodosComentarios.setVisibility(View.INVISIBLE);
            }

        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Método auxiliar que processa a curtida do usuários
     * @param ctx contexto
     * @param myViewHolder myViewHolder
     * @param anuncio animal
     * @param imageView coração vermelho
     */
    private void curtirAnuncio(final Context ctx, final RecyclerView.ViewHolder myViewHolder,
                               final Animal anuncio, final ImageView imageView){

        if (ConfiguracaoFirebase.isUsuarioLogado()) {
            DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();
            List<String> listaCurtidas = new ArrayList<>();

            if(isCurtido(anuncio)){
                Toast.makeText(ctx, "Usuário já curtiu " + anuncio.getNome()+"!", Toast.LENGTH_SHORT).show();
            } else {
                if(ConfiguracaoFirebase.isUsuarioLogado()) {
                    if (anuncio.getCurtidas() != null) {
                        listaCurtidas = anuncio.getCurtidas();
                    }
                    listaCurtidas.add(usuarioAtual);
                    anuncio.setCurtidas(listaCurtidas);

                    Toast.makeText(ctx, "Animal curtido!", Toast.LENGTH_SHORT).show();

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
     * @param ctx contexto
     * @param anuncio animal
     */
    private void compartilharAnuncio(Context ctx, Animal anuncio){
        try {
            // TODO: 13/02/2019 configurar quando o app for publicado do google play
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, "Conheça o "+anuncio.getNome()+". Baixe o App Amigo Animal em " +
                    "https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_SUBJECT, "Anúncio de animal");
            share.setType("image/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse(anuncio.getFotos().get(0)));
            ctx.startActivity(Intent.createChooser(share, "Compartilhando anúncio de animal..."));

        }catch (Exception e) {e.printStackTrace();}
    }

    /**
     * método auxiliar que verifica se o usuário atual já curtiu o anúncio
     * @param animal anuncio
     * @return boolean
     */
    private boolean isCurtido(Animal animal){
        boolean retorno = false;

        if(ConfiguracaoFirebase.isUsuarioLogado()) {
            String usuarioAtual = ConfiguracaoFirebase.getIdUsuario();

            if (animal != null && animal.getCurtidas() != null)
                if (animal.getCurtidas().contains(usuarioAtual)) {
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
     *
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
        ImageView imvCompartilharAnuncio, imvCurtirAnuncio;
        ImageButton imbComentarAnuncio;
        EditText edtComentar;
        View layout;

        MyViewHolder(View itemView) {
            super(itemView);

            dataCadastro = itemView.findViewById(R.id.textDataCadastro);
            nome = itemView.findViewById(R.id.txv_nome);
            idade = itemView.findViewById(R.id.textIdade);
            foto = itemView.findViewById(R.id.imageAnuncio);
            cidade = itemView.findViewById(R.id.textCidadePrincipal);
            numeroCurtidas = itemView.findViewById(R.id.txv_num_curtidas);
            textViewCurtidas = itemView.findViewById(R.id.textViewCurtidas);
            textViewTodosComentarios = itemView.findViewById(R.id.ttv_todos_comentarios);

            layout = itemView.findViewById(R.id.constraintLayout_comentar);

            //curtir, comentar e compartilhar anuncio da tela principal
            imvCompartilharAnuncio = itemView.findViewById(R.id.imv_compartilhar_anuncio);
            imbComentarAnuncio = itemView.findViewById(R.id.imageButton_comentar);
            imvCurtirAnuncio = itemView.findViewById(R.id.imv_curtir_anuncio);
            edtComentar = itemView.findViewById(R.id.editText_comentar);
        }
    }
}
