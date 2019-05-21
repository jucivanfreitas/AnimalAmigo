package com.celvansystems.projetoamigoanimal.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.celvansystems.projetoamigoanimal.R;

public class MensagensFragment extends Fragment {

    View view;

    public MensagensFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mensagens, container, false);

        inializaComponentes();

        return view;
    }

    private void inializaComponentes() {

    }
}
