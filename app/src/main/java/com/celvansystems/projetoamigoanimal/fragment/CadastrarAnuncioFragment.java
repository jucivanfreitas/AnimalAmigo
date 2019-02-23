package com.celvansystems.projetoamigoanimal.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celvansystems.projetoamigoanimal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CadastrarAnuncioFragment extends Fragment {


    public CadastrarAnuncioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cadastrar_anuncio, container, false);
    }

}
