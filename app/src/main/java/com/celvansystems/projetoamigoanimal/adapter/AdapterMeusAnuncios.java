package com.celvansystems.projetoamigoanimal.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.DetalhesAnimalActivity;
import com.celvansystems.projetoamigoanimal.fragment.AnunciosFragment;
import com.celvansystems.projetoamigoanimal.fragment.CadastrarAnuncioFragment;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.MyViewHolder>
        implements Serializable {

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


    private void configuracoesMaisOpcoes(Animal anuncio, AdapterMeusAnuncios.MyViewHolder myViewHolder) {

        if(ConfiguracaoFirebase.isUsuarioLogado()) {
            myViewHolder.imvMaisOpcoesMeusAnuncios.setVisibility(View.VISIBLE);
        } else {
            myViewHolder.imvMaisOpcoesMeusAnuncios.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {

        if(anuncios != null) {

            final Animal anuncio = anuncios.get(i);
            //anuncioComentado = anuncio;
            if (anuncio != null) {

                configuracoesMaisOpcoes(anuncio, myViewHolder);

                myViewHolder.dataCadastro.setText(anuncio.getDataCadastro());
                myViewHolder.nome.setText(anuncio.getNome());
                myViewHolder.idade.setText(anuncio.getIdade());
                myViewHolder.cidade.setText(anuncio.getCidade());

                //pega a primeira imagem cadastrada
                List<String> urlFotos = anuncio.getFotos();

                if (urlFotos != null && urlFotos.size() > 0) {
                    String urlCapa = urlFotos.get(0);

                    Picasso.get().load(urlCapa).into(myViewHolder.foto);
                }

                // ação de clique na foto do anuncio
                myViewHolder.foto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detalhesIntent = new Intent(v.getContext(), DetalhesAnimalActivity.class);
                        detalhesIntent.putExtra("anuncioSelecionado", anuncio);
                        v.getContext().startActivity(detalhesIntent);
                    }
                });

                myViewHolder.imvMaisOpcoesMeusAnuncios.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(myViewHolder.imvMaisOpcoesMeusAnuncios.getContext(), "bla", Toast.LENGTH_LONG).show();
                        List<String> opcoesLista = new ArrayList<>();

                        if (ConfiguracaoFirebase.getIdUsuario().equalsIgnoreCase(anuncio.getDonoAnuncio())) {
                            opcoesLista.add("Editar");
                            opcoesLista.add("Remover");
                        }

                        final String[] opcoes = new String[opcoesLista.size()];

                        for (int i = 0; i < opcoesLista.size(); i++) {
                            opcoes[i] = opcoesLista.get(i);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(myViewHolder.imvMaisOpcoesMeusAnuncios.getContext());

                        builder.setItems(opcoes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if ("Editar".equals(opcoes[which])) {

                                    Bundle data = new Bundle();
                                    data.putSerializable("anuncioSelecionado", anuncio);

                                    CadastrarAnuncioFragment cadFragment = new CadastrarAnuncioFragment();
                                    cadFragment.setArguments(data);

                                    AppCompatActivity activity = (AppCompatActivity) myViewHolder.itemView.getContext();
                                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.view_pager, cadFragment).addToBackStack("tag").commit();

                                } else if ("Remover".equals(opcoes[which])) {

                                    new AlertDialog.Builder(myViewHolder.itemView.getContext())
                                            .setMessage("Tem certeza de que deseja excluir este anúncio?")
                                            .setCancelable(false)
                                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    anuncio.remover();
                                                    AppCompatActivity activity = (AppCompatActivity) myViewHolder.itemView.getContext();
                                                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                    fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).addToBackStack(null).commit();
                                                    //Util.setSnackBar(myViewHolder.layout, "Remover");
                                                }
                                            })
                                            .setNegativeButton("Não", null)
                                            .show();

                                } else if ("Denunciar".equals(opcoes[which])) {
                                    Util.setSnackBar(myViewHolder.layout, "Denunciar");
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }
        }
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
        ImageView imvMaisOpcoesMeusAnuncios;
        ImageView imvCompartilharMeusAnuncios;
        View layout;

        MyViewHolder(View itemView) {
            super(itemView);

            dataCadastro = itemView.findViewById(R.id.textDataCadastroMeusAnuncios);
            nome = itemView.findViewById(R.id.txv_nome_meus_anuncios);
            idade = itemView.findViewById(R.id.textIdade_meus_anuncios);
            foto = itemView.findViewById(R.id.imganun_meus_anuncios);
            cidade = itemView.findViewById(R.id.textCidadePrincipal_meus_anuncios);
            imvMaisOpcoesMeusAnuncios = itemView.findViewById(R.id.imv_mais_opcoes_meus_anuncios);
            imvCompartilharMeusAnuncios = itemView.findViewById(R.id.imv_compartilhar_meus_anuncios);

            //Para a snackBar
            layout = itemView.findViewById(R.id.view_pager);
        }
    }
}
