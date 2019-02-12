package com.celvansystems.projetoamigoanimal.adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.DetalhesActivity;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> implements Serializable {

    private List<Animal> anuncios;
    private int n;

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
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        n = i;
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

        myViewHolder.foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animal anuncioSelecionado = anuncios.get(n);
                Intent j = new Intent(v.getContext(), DetalhesActivity.class);
                j.putExtra("anuncioSelecionado", anuncioSelecionado);
                v.getContext().startActivity(j);
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
        ImageView imv_perfil_principal;

        MyViewHolder(View itemView) {
            super(itemView);

            dataCadastro = itemView.findViewById(R.id.textDataCadastro);
            nome = itemView.findViewById(R.id.textNome);
            idade = itemView.findViewById(R.id.textIdade);
            foto = itemView.findViewById(R.id.imganun);
            cidade = itemView.findViewById(R.id.textCidadePrincipal);
            //imv_perfil_principal = itemView.findViewById(R.id.imv_perfil_curtir_Principal3);
        }
    }
}
