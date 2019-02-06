package com.celvansystems.projetoamigoanimal.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {


    private List<Animal> anuncios;
    private Context context;

    public AdapterAnuncios() {

    }

    public AdapterAnuncios(List<Animal> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_meus_anuncios, viewGroup, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        Animal anuncio = anuncios.get(i);
        myViewHolder.especie.setText(anuncio.getEspecie());
        myViewHolder.nome.setText(anuncio.getNome());
        myViewHolder.idade.setText(anuncio.getIdade());

        //pega a primeira imagem cadastrada
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);

        Picasso.get().load(urlCapa).into(myViewHolder.foto);
    }

    @Override
    public int getItemCount() {

        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView especie;
        TextView nome;
        TextView idade;
        ImageView foto;

        public MyViewHolder(View itemView) {
            super(itemView);

            especie = itemView.findViewById(R.id.textEspecie);
            nome = itemView.findViewById(R.id.textNome);
            idade = itemView.findViewById(R.id.textIdade);
            foto = itemView.findViewById(R.id.imageAnuncio);
        }
    }
}
