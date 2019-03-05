package com.celvansystems.projetoamigoanimal.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.model.Comentario;

import java.io.Serializable;
import java.util.List;

public class AdapterComentarios extends RecyclerView.Adapter<AdapterComentarios.MyViewHolder>
        implements Serializable {

    private List<Comentario> comentarios;

    /**
     * construtor
     * @param comentarios lista de comentarios
     */
    public AdapterComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_comentario_fotos, viewGroup, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        if (comentarios != null) {
            final Comentario comentario = comentarios.get(i);

            // TODO: 05/03/2019 após concluir desenvolvimento do cadastro do usuario, configurar restante dos atributos
            if (comentario != null) {
                String nomeUsuario = comentario.getUsuario().getNome();

                String foto = comentario.getUsuario().getFoto();
                if(foto != null) {
                    myViewHolder.fotousuario.setImageURI(Uri.parse(foto));
                }
                myViewHolder.ttvTexto.setText(comentario.getTexto());
                myViewHolder.ttvDataHora.setText(comentario.getDatahora());

                if(nomeUsuario!= null) {
                    myViewHolder.ttvNomeUsuario.setText(nomeUsuario);
                }
            }
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

        //ImageButton imbComentar;
        //EditText edtComentar;

        private View layout;

        MyViewHolder(View itemView) {
            super(itemView);

            ttvTexto = itemView.findViewById(R.id.textView_texto);
            ttvDataHora = itemView.findViewById(R.id.textView_dataHora);
            ttvNomeUsuario = itemView.findViewById(R.id.textView_nome_usuario);
            fotousuario = itemView.findViewById(R.id.imageView_foto_usuario);

            layout = itemView.findViewById(R.id.constraint_comentarios);
        }
    }
}
