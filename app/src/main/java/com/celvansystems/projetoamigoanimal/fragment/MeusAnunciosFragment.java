package com.celvansystems.projetoamigoanimal.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterMeusAnuncios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
    private View viewFragment;
    //private SwipeRefreshLayout swipeRefreshLayout;
    private View layout;


    private AlertDialog dialog;

    public MeusAnunciosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewFragment =  inflater.inflate(R.layout.fragment_meus_anuncios, container, false);
        inicializarComponentes();

        return viewFragment;
    }

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
                    Util.setSnackBar(layout, getString(R.string.falha_carregar_anuncios));
                }
            });
        } catch (Exception e){e.printStackTrace();
        }
    }

    /**
     * inicializa os componentes da view
     */
    @SuppressLint({"RestrictedApi", "CutPasteId"})
    private void inicializarComponentes(){

        layout = viewFragment.findViewById(R.id.frame_layout_meus_anuncios_fragment);

        //esconde os botoes de cidade e especie
        //LinearLayout linearLayoutBotoes = viewFragment.findViewById(R.id.linearLayoutBotoes);
        //linearLayoutBotoes.setVisibility(View.GONE);

        //fab
        FloatingActionButton fabCadastrar = viewFragment.findViewById(R.id.fabcadastrar_meus_anuncios);
        fabCadastrar.setVisibility(View.VISIBLE);
        fabCadastrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                FragmentManager fragmentManager= Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.view_pager, new CadastrarAnuncioFragment()).addToBackStack("tag").commit();
            }
        });

        RecyclerView recyclerMeusAnuncios = viewFragment.findViewById(R.id.recyclerMeusAnuncios);

        // configuracoes iniciais
        try {
            anuncioUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");
        } catch (Exception e){e.printStackTrace();}

        //configurar recyclerview
        try {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(viewFragment.getContext());

            recyclerMeusAnuncios.setLayoutManager(lm);
            recyclerMeusAnuncios.setHasFixedSize(true);

            adapterMeusAnuncios = new AdapterMeusAnuncios(anuncios);
            recyclerMeusAnuncios.setAdapter(adapterMeusAnuncios);
        } catch (Exception e){e.printStackTrace();}

        //recupera anuncios para o usuario
        recuperarAnuncios();

        //adiciona evento de clique

        /*recyclerMeusAnuncios.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(), recyclerMeusAnuncios, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*
                Animal anuncioSelecionado = anuncios.get(position);
                Intent detalhesIntent = new Intent(view.getContext(), DetalhesAnimalActivity.class);
                detalhesIntent.putExtra("anuncioSelecionado", anuncioSelecionado);
                view.getContext().startActivity(detalhesIntent);
                */
            /*}

            @Override
            public void onLongItemClick(View view, int position) {

                /*Animal anuncioSelecionado = anuncios.get(position);
                anuncioSelecionado.remover();

                adapterMeusAnuncios.notifyDataSetChanged();

                Util.setSnackBar(layout,  getString(R.string.anuncio_excluido));*/
            /*}

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        }
        ));*/
        //refresh
        /*swipeRefreshLayout = viewFragment.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerAnuncios();
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();

        /*if(getView() == null){
            return;
        }

        // açao de voltar
        /*getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    FragmentManager fragmentManager= Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction =fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.view_pager, new AnunciosFragment()).commit();
                    return true;
                }
                return false;
            }
        });*/
    }

    private void refreshRecyclerAnuncios(){
        // Refresh items
        Util.setSnackBar(layout,  getString(R.string.atualizando_pets));

        anuncios.clear();
        recuperarAnuncios();
        //swipeRefreshLayout.setRefreshing(false);
    }
}
    