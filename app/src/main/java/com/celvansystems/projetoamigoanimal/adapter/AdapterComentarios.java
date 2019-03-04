package com.celvansystems.projetoamigoanimal.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
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

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_comentarioss, viewGroup, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, @SuppressLint("RecyclerView") int i) {

        if (comentarios != null) {
            final Comentario comentario = comentarios.get(i);

            if (comentario != null) {

                myViewHolder.ttvTexto.setText(comentario.getTexto());
                myViewHolder.ttvDataHora.setText(comentario.getDatahora());
                /*myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
                myViewHolder.nome.setText(anuncio.getNome());
                myViewHolder.idade.setText(anuncio.getIdade());
                myViewHolder.cidade.setText(anuncio.getCidade());*/


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

        ImageButton imbComentar;
        EditText edtComentar;

        private View layout;

        MyViewHolder(View itemView) {
            super(itemView);

            imbComentar = itemView.findViewById(R.id.imageButton_comentar);
            edtComentar = itemView.findViewById(R.id.editText_comentar);
            ttvTexto = itemView.findViewById(R.id.ttv_texto);
            ttvDataHora = itemView.findViewById(R.id.ttvDataHora);
            /*
            dataHora = itemView.findViewById(R.id.textDataCadastro);
            texto = itemView.findViewById(R.id.txv_nome);
            usuarioId = itemView.findViewById(R.id.textIdade);

            layout = itemView.findViewById(R.id.constraintLayout_comentar);
*/

        }
    }
}
