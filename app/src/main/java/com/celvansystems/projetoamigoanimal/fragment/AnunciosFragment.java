package com.celvansystems.projetoamigoanimal.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.adapter.AdapterAnuncios;
import com.celvansystems.projetoamigoanimal.helper.ConfiguracaoFirebase;
import com.celvansystems.projetoamigoanimal.helper.Util;
import com.celvansystems.projetoamigoanimal.model.Animal;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnunciosFragment extends Fragment {

    private Button btnCidade, btnEspecie;
    private AdapterAnuncios adapterAnuncios;
    private List<Animal> listaAnuncios = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference anunciosPublicosRef;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private ArrayAdapter adapterCidades;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    public AnunciosFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.fragment_anuncios, container, false);
        inicializarComponentes();

        return view;
    }

    /**
     * inicializa os componentes da view
     */
    @SuppressLint("RestrictedApi")
    private void inicializarComponentes(){

        FloatingActionButton fabCadastrar = view.findViewById(R.id.fabcadastrar);
        fabCadastrar.setVisibility(View.GONE);

        try {
            anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

        } catch (Exception e){e.printStackTrace();}

        RecyclerView recyclerAnunciosPublicos = view.findViewById(R.id.recyclerAnuncios);
        recyclerAnunciosPublicos.setItemAnimator(null);

        btnCidade = view.findViewById(R.id.btnCidade);
        btnEspecie = view.findViewById(R.id.btnEspecie);

        btnCidade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtraPorCidade(v);
            }
        });
        btnEspecie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtraPorEspecie(v);
            }
        });

        //configurar recyclerview
        try {
            RecyclerView.LayoutManager lm = new LinearLayoutManager(view.getContext());

            recyclerAnunciosPublicos.setLayoutManager(lm);

            recyclerAnunciosPublicos.setHasFixedSize(true);

            adapterAnuncios = new AdapterAnuncios(listaAnuncios);

            recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        } catch (Exception e){e.printStackTrace();}

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(view.getContext())
                    .setMessage("Procurando anúncios")
                    .setCancelable(true)
                    .build();
        } catch (Exception e){e.printStackTrace();}

        //refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerAnuncios();
            }
        });
    }

    private boolean isListaAnunciosPopulada(){
        return listaAnuncios.size() > 0;
    }

    private void refreshRecyclerAnuncios(){
        // Refresh items
        Toast.makeText(view.getContext(), "refreshing anuncios", Toast.LENGTH_LONG).show();

        listaAnuncios.clear();
        recuperarAnunciosPublicos();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * exibe os anúncios públicos
     */
    private void recuperarAnunciosPublicos(){

        dialog.show();

        try {
            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //se a lista de anuncios estiver vazia
                    if (!isListaAnunciosPopulada()) {

                        for (DataSnapshot animais : dataSnapshot.getChildren()) {
                            Animal animal = animais.getValue(Animal.class);
                            if (animal != null) {
                                Log.d("INFO50", animal.getNome());
                                listaAnuncios.add(animal);
                            }
                        }

                        Collections.reverse(listaAnuncios);
                        adapterAnuncios.notifyDataSetChanged();

                        dialog.dismiss();
                        Log.d("INFO50", "passou total");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(view.getContext(),
                            "Falha ao acessar anúncios. Verifique sua conexão à internet." + databaseError.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.d("INFO50", "cancelado");

                    dialog.dismiss();
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            dialog.dismiss();

            Toast.makeText(view.getContext(),
                    "Falha ao acessar anúncios. Verifique sua conexão à internet." + e.getMessage(),
                    Toast.LENGTH_SHORT).show();}
    }

    @Override
    public void onStart() {
        super.onStart();

        // Verifica se há conta do google logada.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(view.getContext());
        if(account != null) {
            Log.d("INFO40", "Usuário logado com google");
            //Util.setSnackBar(layout, "Usuário logado pelo google");
        } else {
            // Verifica se há conta do facebook logada
            AccessToken token = AccessToken.getCurrentAccessToken();

            if (token != null) {
                Log.d("INFO40", "Usuário logado pelo facebook");
                //Util.setSnackBar(layout, "Usuário logado pelo facebook");
            }
        }
        // TODO: 26/02/2019 apenas se o usuario estiver logado
        //refreshRecyclerAnuncios();
    }

    @Override
    public void onResume(){
        super.onResume();
        refreshRecyclerAnuncios();
    }

    /**
     * filtro dos anuncioos
     * @param estado uf
     * @param cidade cidade
     * @param especie especie
     */
   public void recuperarAnunciosFiltro(final String estado, final String cidade, final String especie) {

        try {
            dialog.show();

            //cidade
            if (cidade!=null && !cidade.equalsIgnoreCase(getString(R.string.todas))) {
                btnCidade.setText(cidade);
            }
            //estado
            else if (estado != null){
                btnCidade.setText(estado);
            }
            //especie
            if (especie != null) {
                btnEspecie.setText(especie);
            }

            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listaAnuncios.clear();

                    for (DataSnapshot animais : dataSnapshot.getChildren()) {

                        Animal animal = animais.getValue(Animal.class);

                        if (animal != null) {

                            String textoBotaoCidade = btnCidade.getText().toString();
                            String textoBotaoEspecie = btnEspecie.getText().toString();

                            //sem filtro
                            if((textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos)) ||
                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) && (
                                            textoBotaoEspecie.equalsIgnoreCase(getString(R.string.especie)) ||
                                                    textoBotaoEspecie.equalsIgnoreCase(getString(R.string.todas))))){
                                listaAnuncios.add(animal);

                            } else {
                                //sem espécie
                                if (textoBotaoEspecie.equalsIgnoreCase(getString(R.string.especie)) ||
                                        textoBotaoEspecie.equalsIgnoreCase(getString(R.string.todas))) {

                                    if (textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) ||
                                            textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                            textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos))) {

                                        listaAnuncios.add(animal);

                                    } else {
                                        if (textoBotaoCidade.equalsIgnoreCase(animal.getCidade()) ||
                                                textoBotaoCidade.equalsIgnoreCase(animal.getUf())) {
                                            listaAnuncios.add(animal);
                                        }
                                    }
                                    //com espécie
                                } else {
                                    if (textoBotaoEspecie.equalsIgnoreCase(animal.getEspecie())) {

                                        //estado
                                        if (textoBotaoCidade.length()== 2) {

                                            if(textoBotaoCidade.equalsIgnoreCase(animal.getUf())) {
                                                listaAnuncios.add(animal);
                                            }
                                            //cidade
                                        } else {

                                            if (textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) ||
                                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                                    textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos))) {
                                                listaAnuncios.add(animal);
                                            } else if(textoBotaoCidade.equalsIgnoreCase(animal.getCidade())) {
                                                listaAnuncios.add(animal);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                    Collections.reverse(listaAnuncios);
                    adapterAnuncios.notifyDataSetChanged();

                    dialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * filtra por espécie
     */
    public void filtraPorEspecie(View view){

        try {
            AlertDialog.Builder dialogEspecie = new AlertDialog.Builder(view.getContext());
            dialogEspecie.setTitle(getString(R.string.selecione_especie));

            Log.d("INFO50", "filtrando por especie");

            //configura o spinner
            @SuppressLint("InflateParams")
            View viewSpinnerEspecie = getLayoutInflater().inflate(R.layout.dialog_spinner_especie, null);
            final Spinner spinnerEspecie = viewSpinnerEspecie.findViewById(R.id.spinnerFiltroEspecie);

            ArrayList<String> especiesLista = Util.getEspeciesLista(view.getContext());

            //especies
            ArrayAdapter<String> adapterEspecies = new ArrayAdapter<>(view.getContext(), simple_spinner_item, especiesLista);
            adapterEspecies.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEspecie.setAdapter(adapterEspecies);

            dialogEspecie.setView(viewSpinnerEspecie
            );

            dialogEspecie.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String filtroEspecie = spinnerEspecie.getSelectedItem().toString();
                    String cidadeBotao = btnCidade.getText().toString();

                    recuperarAnunciosFiltro(null, cidadeBotao, filtroEspecie);
                }
            });

            dialogEspecie.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogEspecie.create();
            dialog.show();
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     *
     * @param view view
     */
    public void filtraPorCidade(View view){

        try {
            AlertDialog.Builder dialogCidade = new AlertDialog.Builder(view.getContext());
            dialogCidade.setTitle(getString(R.string.selecione_cidade));

            Log.d("INFO50", "filtrando por cidade");

            //configura o spinner
            @SuppressLint("InflateParams")
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner_cidade, null);

            ArrayList<String> estadosLista = Util.getEstadosLista(view.getContext());

            spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltroEstado);
            spinnerCidade = viewSpinner.findViewById(R.id.spinnerFiltroCidade);

            spinnerCidade.setVisibility(View.VISIBLE);
            ArrayList<String> cidadesLista = Util.getCidadesLista("AC", view.getContext());

            //cidades
            adapterCidades = new ArrayAdapter<>(view.getContext(), simple_spinner_item, cidadesLista);
            ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(view.getContext(), simple_spinner_item, estadosLista);
            adapterEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstado.setAdapter(adapterEstados);
            spinnerCidade.setAdapter(adapterCidades);

            dialogCidade.setView(viewSpinner);
            dialogCidade.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String filtroEstado = getString(R.string.todos);
                    String filtroCidade = getString(R.string.todas);
                    String filtroEspecie;

                    if(spinnerEstado.getSelectedItem()!=null) {

                        filtroEstado = spinnerEstado.getSelectedItem().toString();
                    }
                    if(spinnerCidade.getSelectedItem()!=null) {

                        filtroCidade = spinnerCidade.getSelectedItem().toString();
                    }

                    filtroEspecie = btnEspecie.getText().toString();
                    recuperarAnunciosFiltro(filtroEstado, filtroCidade, filtroEspecie);
                }
            });

            dialogCidade.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCidade.create();
            dialog.show();

            spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        setAdapterSpinnerCidade();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } catch (Exception e) {e.printStackTrace();}
    }

    private void setAdapterSpinnerCidade(){

        try {
            ArrayList<String> cidadesLista = new ArrayList<>();
            cidadesLista.add(getString(R.string.todas));
            String estadoSelecionado = spinnerEstado.getSelectedItem().toString();

            if(!estadoSelecionado.equalsIgnoreCase(getString(R.string.todos))) {
                cidadesLista = Util.getCidadesLista(estadoSelecionado, view.getContext());
            }
            adapterCidades = new ArrayAdapter<>(view.getContext(), simple_spinner_item, cidadesLista);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();

        } catch (Exception e){e.printStackTrace();}
    }
}