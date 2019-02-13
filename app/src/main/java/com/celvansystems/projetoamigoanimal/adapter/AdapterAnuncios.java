package com.celvansystems.projetoamigoanimal.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.DetalhesActivity;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> implements Serializable {

    private List<Animal> anuncios;
    private int n;
    String urlCapa;
    DatabaseReference anuncioRef;
    boolean curtido;

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

        n = i;



        final Animal anuncio = anuncios.get(i);
        myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
        myViewHolder.nome.setText(anuncio.getNome());
        myViewHolder.idade.setText(anuncio.getIdade());
        myViewHolder.cidade.setText(anuncio.getCidade());

        //pega a primeira imagem cadastrada
        List<String> urlFotos = anuncio.getFotos();
        urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(myViewHolder.foto);

        // ação de clique na foto do anuncio
        myViewHolder.foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animal anuncioSelecionado = anuncios.get(n);
                Intent j = new Intent(v.getContext(), DetalhesActivity.class);
                j.putExtra("anuncioSelecionado", anuncioSelecionado);
                v.getContext().startActivity(j);
            }
        });

        //acao de clique no botao curtir anuncio
        myViewHolder.imvCurtirAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 13/02/2019 curtir anuncio

                if(ConfiguracaoFirebase.isUsuarioLogado()) {
                    anuncioRef = ConfiguracaoFirebase.getFirebase()
                            .child("meus_animais");

                    final String idUsuario = ConfiguracaoFirebase.getIdUsuario();
                    curtido = false;
                    anuncioRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for(DataSnapshot users: dataSnapshot.getChildren()){
                                String usuario = users.getKey();
                                for(DataSnapshot animais: users.getChildren()){
                                    Animal animal = animais.getValue(Animal.class);
                                    if(animais.getKey().equalsIgnoreCase(anuncio.getIdAnimal()) &&
                                    curtido == false) {
                                        Task<Void> anuncioCurtidasRef = anuncioRef
                                                .child(usuario)
                                                .child(animal.getIdAnimal())
                                                .child("curtidas")
                                                .setValue(usuario);

                                        //PAREI NAS CURTIDAS (TENTANDO...)

                                        myViewHolder.imvCurtirAnuncio.setBackgroundColor(Color.RED);
                                        curtido = true;

                                        anuncioCurtidasRef.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(myViewHolder.itemView.getContext(), "Animal curtido!", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        break;
                                    }

                                    /*if(animais.getKey().equalsIgnoreCase(anuncio.getIdAnimal())) {
                                        anuncioRef.child("curtidas");
                                        anuncioRef.push().setValue(idUsuario);

                                    }*/
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                    /*DatabaseReference anuncioRef = ConfiguracaoFirebase.getFirebase()
                            .child("meus_animais");

                    try {
                        String idUsuario = ConfiguracaoFirebase.getIdUsuario();
                        DatabaseReference animalRef = ConfiguracaoFirebase.getFirebase()
                                .child("meus_animais");

                        DataSnapshot contactSnapshot = anuncioRef.child("contacts");
                        Iterable<DataSnapshot> contactChildren = contactSnapshot.getChildren();
                        animalRef.child(idUsuario)
                                .child(anuncio.getIdAnimal())
                                .child("curtidas")
                                .child(idUsuario);
                        myViewHolder.imvCurtirAnuncio.setBackgroundColor(Color.RED);
                    } catch (Exception e){e.printStackTrace();}*/

                } else {
                    Toast.makeText(myViewHolder.itemView.getContext(), "Usuário não logado!", Toast.LENGTH_LONG).show();
                }
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
                // TODO: 13/02/2019 configurar quando o app for publicado do google play
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Baixe o App Amigo Animal em " +
                        "https://play.google.com/store/apps/details?id=com.google.android.apps.plus");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, anuncio.getNome());
                shareIntent.putExtra(Intent.EXTRA_STREAM, urlCapa);
                shareIntent.setType("image/*");

                myViewHolder.itemView.getContext().startActivity(
                        Intent.createChooser(shareIntent, "Compartilhe este animal"));

            }
        });

        }

    @Override
    public int getItemCount() {

        return anuncios.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dataCadastro;
        TextView nome;
        TextView idade;
        TextView cidade;
        ImageView foto;
        ImageView imvCompartilharAnuncio, imvComentarAnuncio, imvCurtirAnuncio;

        //ImageView imv_perfil_principal;

        MyViewHolder(View itemView) {
            super(itemView);

            dataCadastro = itemView.findViewById(R.id.textDataCadastro);
            nome = itemView.findViewById(R.id.textNome);
            idade = itemView.findViewById(R.id.textIdade);
            foto = itemView.findViewById(R.id.imageAnuncio);
            cidade = itemView.findViewById(R.id.textCidadePrincipal);
            //curtir, comentar e compartilhar anuncio da tela principal
            imvCompartilharAnuncio = itemView.findViewById(R.id.imv_compartilhar_anuncio);
            imvComentarAnuncio = itemView.findViewById(R.id.imv_comentar_anuncio);
            imvCurtirAnuncio = itemView.findViewById(R.id.imv_curtir_anuncio);

            //imv_perfil_principal = itemView.findViewById(R.id.imv_perfil_curtir_Principal3);
        }
    }
}
