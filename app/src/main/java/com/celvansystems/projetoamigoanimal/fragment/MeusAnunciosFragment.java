package com.celvansystems.projetoamigoanimal.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.activity.DetalhesAnimalActivity;
import com.celvansystems.projetoamigoanimal.adapter.AdapterMeusAnuncios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.RecyclerItemClickListener;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeusAnunciosFragment extends Fragment {

    private List<Animal> anuncios = new ArrayList<>();
    private AdapterMeusAnuncios adapterMeusAnuncios;
    private DatabaseReference anuncioUsuarioRef;
    private StorageReference storage;
    private View viewFragment;
    private SwipeRefreshLayout swipeRefreshLayout;


    private AlertDialog dialog;

    public MeusAnunciosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewFragment =  inflater.inflate(R.layout.fragment_anuncios, container, false);
        inicializarComponentes();

        return viewFragment;
    }

    /**
     * metodo auxilicar que apaga as fotos de um animal
     * @param anuncio animal
     */
    private void apagarFotosStorage (Animal anuncio){

        try {
            StorageReference imagemAnimal = storage
                    .child("imagens")
                    .child("animais")
                    .child(anuncio.getIdAnimal());

            int numFotos = anuncio.getFotos().size();

            for (int i = 0; i < numFotos; i++) {
                String textoFoto = "imagem" + i;

                imagemAnimal.child(textoFoto).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), getString(R.string.fotos_excluidas), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getContext(), getString(R.string.falha_deletar_fotos), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * recupera os anuncios do usuario que estiver logado
     */
    private void recuperarAnuncios(){

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(getContext())
                    .setMessage(R.string.procurando_anuncios)
                    .setCancelable(false)
                    .build();
            dialog.show();
        } catch (Exception e){e.printStackTrace();}

        try {
            anuncioUsuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    anuncios.clear();
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Animal animal = ds.getValue(Animal.class);
                        if(animal!= null && animal.getDonoAnuncio()!= null) {
                            if (animal.getDonoAnuncio().equalsIgnoreCase(ConfiguracaoFirebase.getIdUsuario())) {
                                anuncios.add(animal);
                            }
                        }
                    }
                    Collections.reverse(anuncios);
                    adapterMeusAnuncios.notifyDataSetChanged();

                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), getString(R.string.falha_carregar_anuncios), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * inicializa os componentes da view
     */
    @SuppressLint("RestrictedApi")
    private void inicializarComponentes(){

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        FloatingActionButton fabCadastrar = viewFragment.findViewById(R.id.fabcadastrar);
        fabCadastrar.setVisibility(View.VISIBLE);

        fabCadastrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                FragmentManager fragmentManager= Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.view_pager, new CadastrarAnuncioFragment()).commit();
            }
        });

        RecyclerView recyclerAnuncios = viewFragment.findViewById(R.id.recyclerAnuncios);

        // configuracoes iniciais
        try {
            anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
        } catch (Exception e){e.printStackTrace();}

        //configurar recyclerview
        try {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(viewFragment.getContext());

            recyclerAnuncios.setLayoutManager(lm);
            //recyclerAnuncios.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerAnuncios.setHasFixedSize(true);

            adapterMeusAnuncios = new AdapterMeusAnuncios(anuncios);
            recyclerAnuncios.setAdapter(adapterMeusAnuncios);
        } catch (Exception e){e.printStackTrace();}

        //recupera anuncios para o usuario
        recuperarAnuncios();

        //adiciona evento de clique
        recyclerAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(), recyclerAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Animal anuncioSelecionado = anuncios.get(position);
                Intent detalhesIntent = new Intent(view.getContext(), DetalhesAnimalActivity.class);
                detalhesIntent.putExtra("anuncioSelecionado", anuncioSelecionado);
                view.getContext().startActivity(detalhesIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

                Animal anuncioSelecionado = anuncios.get(position);
                anuncioSelecionado.remover();

                apagarFotosStorage(anuncioSelecionado);

                adapterMeusAnuncios.notifyDataSetChanged();

                Toast.makeText(getContext(), getString(R.string.anuncio_excluido), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }
        ));
        //refresh
        swipeRefreshLayout = viewFragment.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerAnuncios();
            }
        });
    }

    private void refreshRecyclerAnuncios(){
        // Refresh items
        Toast.makeText(getContext(), "refreshing anuncios", Toast.LENGTH_LONG).show();

        anuncios.clear();
        recuperarAnuncios();
        swipeRefreshLayout.setRefreshing(false);
    }
}
