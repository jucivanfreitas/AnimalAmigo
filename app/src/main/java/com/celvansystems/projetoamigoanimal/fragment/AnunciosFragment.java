package com.celvansystems.projetoamigoanimal.fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;

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
import java.util.Objects;

import dmax.dialog.SpotsDialog;

import static android.R.layout.simple_spinner_item;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnunciosFragment extends Fragment {

    private Button btnLocal, btnEspecie;
    private AdapterAnuncios adapterAnuncios;
    private List<Animal> listaAnuncios = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference anunciosPublicosRef;
    private Spinner spinnerEstado;
    private Spinner spinnerCidade;
    private ArrayAdapter<String> adapterCidades;
    private RecyclerView recyclerAnunciosPublicos;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txvSemAnuncios;
    private View view;
    private View layout;

    public AnunciosFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_anuncios, container, false);
        inicializarComponentes();

        return view;
    }

    /**
     * inicializa os componentes da view
     */
    @SuppressLint("RestrictedApi")
    private void inicializarComponentes() {

        layout = view.findViewById(R.id.const_layout_anuncios);

        try {
            anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                    .child("meus_animais");

        } catch (Exception e) {
            e.printStackTrace();
        }

        recyclerAnunciosPublicos = view.findViewById(R.id.recyclerAnuncios);
        recyclerAnunciosPublicos.setItemAnimator(null);


        btnLocal = view.findViewById(R.id.btnCidade);
        btnEspecie = view.findViewById(R.id.btnEspecie);

        txvSemAnuncios = view.findViewById(R.id.txv_sem_anuncios);
        txvSemAnuncios.setText(Objects.requireNonNull(getContext()).getString(R.string.nenhum_pet_encontrado));
        txvSemAnuncios.setTextSize(15);

        btnLocal.setOnClickListener(new View.OnClickListener() {
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

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            dialog = new SpotsDialog.Builder()
                    .setContext(view.getContext())
                    .setMessage(R.string.procurando_anuncios)
                    .setCancelable(true)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshRecyclerAnuncios();
            }
        });
    }

    private boolean isListaAnunciosPopulada() {
        return listaAnuncios.size() > 0;
    }

    /**
     * Refesh dos anuncios
     */
    private void refreshRecyclerAnuncios() {
        // Refresh items
        Util.setSnackBar(layout, getString(R.string.atualizando_pets));

        txvSemAnuncios.setVisibility(View.INVISIBLE);
        listaAnuncios.clear();

        String localTexto = btnLocal.getText().toString();
        String especieTexto = btnEspecie.getText().toString();

        boolean filtrandoEspecie = false, filtrandoEstado = false, filtrandoCidade = false;

        if (!especieTexto.equals("especie") && !especieTexto.equals("Todas")) {
            filtrandoEspecie = true;
        }
        if (localTexto.length() == 2) {
            filtrandoEstado = true;
        } else if ((!localTexto.equalsIgnoreCase("Todas")
                && !localTexto.equalsIgnoreCase("Todos")
                && !localTexto.equalsIgnoreCase("cidade"))) {
            filtrandoCidade = true;
        } else {
            filtrandoCidade = false;
            filtrandoEstado = false;
        }
//filtrando especie
        if (filtrandoEspecie) {
            if (!filtrandoEstado && !filtrandoCidade) {
                recuperarAnunciosFiltro(null, null, especieTexto);
            } else {
                if (filtrandoEstado) {
                    recuperarAnunciosFiltro(localTexto, null, especieTexto);
                } else {
                    recuperarAnunciosFiltro(null, localTexto, especieTexto);
                }
            }
            //sem filtrar especie
        } else {
            if (!filtrandoEstado && !filtrandoCidade) {
                recuperarAnunciosFiltro(null, null, null);
            } else {
                if (filtrandoEstado) {
                    recuperarAnunciosFiltro(localTexto, null, null);
                } else {
                    recuperarAnunciosFiltro(null, localTexto, null);
                }
            }
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Verifica se há conta do google logada.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(view.getContext());
        if (account != null) {
            Util.setSnackBar(layout, Objects.requireNonNull(getContext()).getString(R.string.usuario_google));
        } else {
            // Verifica se há conta do facebook logada
            AccessToken token = AccessToken.getCurrentAccessToken();

            if (token != null) {
                Util.setSnackBar(layout, Objects.requireNonNull(getContext()).getString(R.string.usuario_facebook));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRecyclerAnuncios();
    }

    /**
     * filtro dos anuncioos
     *
     * @param estado  uf
     * @param cidade  cidade
     * @param especie especie
     */
    public void recuperarAnunciosFiltro(final String estado, final String cidade, final String especie) {

        try {
            dialog.show();

            //cidade
            if (cidade != null && !cidade.equalsIgnoreCase(getString(R.string.todas)) && !cidade.equalsIgnoreCase(getString(R.string.todos))) {
                btnLocal.setText(cidade);
            }
            //estado
            else if (estado != null) {
                btnLocal.setText(estado);
            }
            //especie
            if (especie != null) {
                btnEspecie.setText(especie);
            }

            anunciosPublicosRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //se a lista de anuncios estiver vazia
                    if (!isListaAnunciosPopulada()) {

                        listaAnuncios.clear();

                        for (DataSnapshot animais : dataSnapshot.getChildren()) {

                            Animal animal = animais.getValue(Animal.class);

                            if (animal != null) {

                                String textoBotaoCidade = btnLocal.getText().toString();
                                String textoBotaoEspecie = btnEspecie.getText().toString();

                                //sem filtro
                                if (((textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                        textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos)) ||
                                        textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade))) && ((
                                                textoBotaoEspecie.equalsIgnoreCase(getString(R.string.especie)) ||
                                                        textoBotaoEspecie.equalsIgnoreCase(getString(R.string.todas)))))) {
                                    listaAnuncios.add(animal);

                                    Log.d("INFO26", "1");
                                    Log.d("INFO26", "cidade: "+textoBotaoCidade);
                                    Log.d("INFO26", "especie: "+textoBotaoEspecie);
                                } else {
                                    //sem espécie
                                    if (textoBotaoEspecie.equalsIgnoreCase(getString(R.string.especie)) ||
                                            textoBotaoEspecie.equalsIgnoreCase(getString(R.string.todas))) {

                                        if (textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) ||
                                                textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                                textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos))) {

                                            listaAnuncios.add(animal);
                                            Log.d("INFO26", "2");
                                        } else {
                                            if (textoBotaoCidade.equalsIgnoreCase(animal.getCidade()) ||
                                                    textoBotaoCidade.equalsIgnoreCase(animal.getUf())) {
                                                listaAnuncios.add(animal);
                                                Log.d("INFO26", "3");
                                            }
                                        }
                                        //com espécie
                                    } else {
                                        if (textoBotaoEspecie.equalsIgnoreCase(animal.getEspecie())) {

                                            //estado
                                            if (textoBotaoCidade.length() == 2) {

                                                if (textoBotaoCidade.equalsIgnoreCase(animal.getUf())) {
                                                    listaAnuncios.add(animal);
                                                    Log.d("INFO26", "4");
                                                }
                                                //cidade
                                            } else {

                                                if (textoBotaoCidade.equalsIgnoreCase(getString(R.string.cidade)) ||
                                                        textoBotaoCidade.equalsIgnoreCase(getString(R.string.todas)) ||
                                                        textoBotaoCidade.equalsIgnoreCase(getString(R.string.todos))) {
                                                    listaAnuncios.add(animal);
                                                    Log.d("INFO26", "5");
                                                } else if (textoBotaoCidade.equalsIgnoreCase(animal.getCidade())) {
                                                    listaAnuncios.add(animal);
                                                    Log.d("INFO26", "6");
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * açao do botao especie
     */
    public void filtraPorEspecie(View view) {

        try {
            AlertDialog.Builder dialogEspecie = new AlertDialog.Builder(view.getContext());
            dialogEspecie.setTitle(getString(R.string.selecione_especie));

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
                    btnEspecie.setText(filtroEspecie);
                    refreshRecyclerAnuncios();

                    verificaRecyclerZerada();
                }


            });

            dialogEspecie.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogEspecie.create();
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verificaRecyclerZerada() {
        if (getItemsSizeFromRecycler() == 0) {
            /*RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) txvSemAnuncios.getLayoutParams();
            p.leftMargin = 50; // in PX
            p.topMargin = 400; // in PX
            txvSemAnuncios.setLayoutParams(p);*/

            txvSemAnuncios.setVisibility(View.VISIBLE);
        } else {
            txvSemAnuncios.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * açao do botao Local
     *
     * @param view view
     */
    public void filtraPorCidade(View view) {

        try {
            AlertDialog.Builder dialogCidade = new AlertDialog.Builder(view.getContext());
            dialogCidade.setTitle(getString(R.string.selecione_cidade));

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

            spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    try {
                        setAdapterSpinnerCidade();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            dialogCidade.setView(viewSpinner);
            dialogCidade.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String filtroEstado;
                    String filtroCidade;

                    if (spinnerEstado.getSelectedItem() != null) {

                        filtroEstado = spinnerEstado.getSelectedItem().toString();
                        btnLocal.setText(filtroEstado);
                    }
                    if (spinnerCidade.getSelectedItem() != null) {
                        if (!spinnerCidade.getSelectedItem().toString().equalsIgnoreCase("Todas")) {

                            filtroCidade = spinnerCidade.getSelectedItem().toString();
                            btnLocal.setText(filtroCidade);
                        }
                    }
                    refreshRecyclerAnuncios();
                    verificaRecyclerZerada();

                }
            });

            dialogCidade.setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogCidade.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * popula spinner cidades de acordo com o estado
     */
    private void setAdapterSpinnerCidade() {

        try {
            ArrayList<String> cidadesLista = new ArrayList<>();
            cidadesLista.add(getString(R.string.todas));
            String estadoSelecionado = spinnerEstado.getSelectedItem().toString();

            if (!estadoSelecionado.equalsIgnoreCase(getString(R.string.todos))) {
                cidadesLista = Util.getCidadesLista(estadoSelecionado, view.getContext());
            }

            adapterCidades = new ArrayAdapter<>(view.getContext(), simple_spinner_item, cidadesLista);
            adapterCidades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerCidade.setAdapter(adapterCidades);
            adapterCidades.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getItemsSizeFromRecycler() {
        int count = 0;
        if (recyclerAnunciosPublicos.getAdapter() != null) {
            count = recyclerAnunciosPublicos.getAdapter().getItemCount();
        }
        return count;
    }
}