package com.celvansystems.projetoamigoanimal.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.MyViewHolder> {

    private List<Animal> anuncios;

    public AdapterMeusAnuncios(List<Animal> anuncios) {
        this.anuncios = anuncios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_meus_anuncios, viewGroup, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Log.d("INFO1"," "+ i);

        Animal anuncio = anuncios.get(i);
        myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
        myViewHolder.nome.setText(anuncio.getNome());
        myViewHolder.idade.setText(anuncio.getIdade());
        myViewHolder.cidade.setText(anuncio.getCidade());

        //pega a primeira imagem cadastrada
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(myViewHolder.foto);
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

        MyViewHolder(View itemView) {
            super(itemView);

            dataCadastro = itemView.findViewById(R.id.textDataCadastro);
            nome = itemView.findViewById(R.id.textNome);
            idade = itemView.findViewById(R.id.textIdade);
            foto = itemView.findViewById(R.id.imageAnuncio);
            cidade = itemView.findViewById(R.id.textCidadePrincipal);
        }
    }
}
