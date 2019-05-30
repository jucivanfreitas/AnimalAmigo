package com.celvansystems.projetoamigoanimal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.celvansystems.projetoamigoanimal.R;
import com.celvansystems.projetoamigoanimal.helper.Constantes;
import com.celvansystems.projetoamigoanimal.helper.Util;

import java.util.Random;

public class DoacaoFragment extends Fragment implements BillingProcessor.IBillingHandler {

    private View view;
    private BillingProcessor bp;
    private View layout;
    private ImageView imvDoacao;


    public DoacaoFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_doacao, container, false);

        inializaComponentes();

        carregarFotoMarketing();

        return view;
    }

    private void carregarFotoMarketing() {

        try {

            int aleatorio = aleatoriar(1, 3);

            for (int i = 1; i <= 3; i++) {

                if(aleatorio == i) {
                    if (i == 1) {
                        imvDoacao.setImageResource(R.drawable.marketing1);
                    } else if (i == 2) {
                        imvDoacao.setImageResource(R.drawable.marketing2);
                    } else {
                        imvDoacao.setImageResource(R.drawable.marketing3);
                    }
                } else {
                    imvDoacao.setImageResource(R.drawable.marketing1);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int aleatoriar(int minimo, int maximo) {
        Random random = new Random();
        return random.nextInt((maximo - minimo) + 1) + minimo;
    }

    private void inializaComponentes() {

        bp = new BillingProcessor(view.getContext(), null, this);
        //bp = new BillingProcessor(view.getContext(), "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAloCy1BSGqDHUo7EeRa+27DhgB46GSVjj5RwPt7ABMayFpdh0vbe7gOUPWqWWugebdoI3Tnmm7NghevAgDTKcqhqNxUBMpyWML5My25ipnHzca/zpGpb3f3aPFG4vf8udIP07osQzHA8lCzYsMfGJtVdXv1bcXNDP2WQJWeXzqnDAyXb+rRQgLcvWI58OFk8co9t1URjcAKRk0j8wyChB55T+TmhhTN2bU9HQ5I3U6m76ph4nc3XSmyk4kww3/bkrErXI3d9/6woEX0DwGmHghB1q3xPVDQ0883J3b0YUcAZQj6xvlzS9tWjzSILb3dAjEpzEbVMtKejH3cEvtrxGsQIDAQAB", this);

        layout = view.findViewById(R.id.frame_layout_doacao);

        imvDoacao = view.findViewById(R.id.imv_doacao);

        Button btnDoar10 = view.findViewById(R.id.btn_doar10_reais);
        Button btnDoar5 = view.findViewById(R.id.btn_doar5_reais);
        Button btnDoar2 = view.findViewById(R.id.btn_doar2_reais);

        btnDoar10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), Constantes.PRODUCT_ID_10_REAIS);
            }
        });
        btnDoar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), Constantes.PRODUCT_ID_5_REAIS);
            }
        });
        btnDoar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bp.purchase(getActivity(), Constantes.PRODUCT_ID_2_REAIS);
            }
        });
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
        //Util.setSnackBar(layout, "Purchased!");
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Util.setSnackBar(layout, "History restored!");
    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {
        //Util.setSnackBar(layout, "Billing Error!");
    }

    @Override
    public void onBillingInitialized() {

        //Util.setSnackBar(layout, "Pagamento iniciado!");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }
}
