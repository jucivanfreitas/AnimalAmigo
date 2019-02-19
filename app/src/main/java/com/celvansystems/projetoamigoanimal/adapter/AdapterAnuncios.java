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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.DetalhesAnimalActivity;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
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

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        if(anuncios != null) {
            final Animal anuncio = anuncios.get(i);

            if(anuncio != null) {
                myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
                myViewHolder.nome.setText(anuncio.getNome());
                myViewHolder.idade.setText(anuncio.getIdade());
                myViewHolder.cidade.setText(anuncio.getCidade());

                //verifica se o animal foi curtido pelo usuario atual
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
                myViewHolder.imvComentarAnuncio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 13/02/2019 inserir comentário no anuncio

                    }
                });

                //acao de clique no botao compartilhar anuncio
                myViewHolder.imvCompartilharAnuncio.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        compartilharAnuncio(myViewHolder.itemView.getContext(), anuncio);
                    }
                });
            }
        }
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
                            if (anuncio.getCurtidas() != null) {
                                /*Toast.makeText(myViewHolder.itemView.getContext(), anuncio.getCurtidas().size() +
                                        " curtida(s)", Toast.LENGTH_LONG).show();*/
                            }
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
        ImageView foto;
        ImageView imvCompartilharAnuncio, imvComentarAnuncio, imvCurtirAnuncio;

        MyViewHolder(View itemView) {
            super(itemView);

            dataCadastro = itemView.findViewById(R.id.textDataCadastro);
            nome = itemView.findViewById(R.id.txv_nome);
            idade = itemView.findViewById(R.id.textIdade);
            foto = itemView.findViewById(R.id.imageAnuncio);
            cidade = itemView.findViewById(R.id.textCidadePrincipal);
            numeroCurtidas = itemView.findViewById(R.id.txv_num_curtidas);
            textViewCurtidas = itemView.findViewById(R.id.textViewCurtidas);

            //curtir, comentar e compartilhar anuncio da tela principal
            imvCompartilharAnuncio = itemView.findViewById(R.id.imv_compartilhar_anuncio);
            imvComentarAnuncio = itemView.findViewById(R.id.imv_comentar_anuncio);
            imvCurtirAnuncio = itemView.findViewById(R.id.imv_curtir_anuncio);
        }
    }
}
