package com.celvansystems.projetoamigoanimal.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.model.Comentario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.MyViewHolder>
        implements Serializable {

    private List<Comentario> comentarios;

    /**
     * construtor
     *
     * @param comentarios lista de comentarios
     */
    public AdapterComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_comentarios_activity, viewGroup, false);

        return new MyViewHolder(item);
    }

    /**
     * Bind
     *
     * @param myViewHolder myViewHolder
     * @param i            anuncio
     */
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        if (comentarios != null) {
            final Comentario comentario = comentarios.get(i);

            // TODO: 05/03/2019 após concluir desenvolvimento do cadastro do usuario, configurar restante dos atributos

            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebase()
                    .child("usuarios");

            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot usuarios : dataSnapshot.getChildren()) {
                        if (usuarios != null) {

                            if (usuarios.child("id").getValue() != null &&
                                    comentario.getUsuario().getId() != null) {

                                if (Objects.requireNonNull(usuarios.child("id").getValue()).toString()
                                        .equalsIgnoreCase(comentario.getUsuario().getId())) {
                                    //nome
                                    String nomeUsuario = Objects.requireNonNull(usuarios.child("nome").getValue()).toString();
                                    //foto
                                    if (usuarios.child("foto").getValue() != null) {
                                        String foto = Objects.requireNonNull(usuarios.child("foto").getValue()).toString();
                                        Picasso.get().load(foto).into(myViewHolder.fotousuario);
                                    }

                                    //texto do comentario
                                    myViewHolder.ttvTexto.setText(comentario.getTexto());

                                    //data/hora do comentario
                                    myViewHolder.ttvDataHora.setText(comentario.getDatahora());

                                    //nome do usuario
                                    myViewHolder.ttvNomeUsuario.setText(nomeUsuario);

                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return comentarios.size();
    }

    /**
     *
     */
    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView ttvTexto;
        TextView ttvDataHora;
        TextView ttvNomeUsuario;
        ImageView fotousuario;

        //private View layout;

        MyViewHolder(View itemView) {
            super(itemView);

            ttvTexto = itemView.findViewById(R.id.textView_texto);
            ttvDataHora = itemView.findViewById(R.id.textView_dataHora);
            ttvNomeUsuario = itemView.findViewById(R.id.textView_nome_usuario);
            fotousuario = itemView.findViewById(R.id.imageView_foto_usuario_comentarios);

            //layout = itemView.findViewById(R.id.constraint_comentarios);
        }
    }
}
