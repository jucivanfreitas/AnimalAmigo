package com.celvansystems.projetoamigoanimal.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celvansystems.projetoamigoanimal.R;



public class NotificacoesFragment extends Fragment {



    private View view;

    public NotificacoesFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notificacoes, container, false);

        inializaComponentes();

        return view;
    }

    private void inializaComponentes() {

    }
}
