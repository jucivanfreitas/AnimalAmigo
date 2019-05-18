package com.celvansystems.projetoamigoanimal.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.celvansystems.projetoamigoanimal.R;

import java.util.ArrayList;
import java.util.List;

public class InAppBillingActivity extends AppCompatActivity implements
        PurchasesUpdatedListener, SkuDetailsResponseListener,
        BillingClientStateListener, View.OnClickListener
{

    private static final String TAG = "InAppBilling";

    //In APP Produkter
    static final String ITEM_SKU_ADREMOVAL = "remove_ads_salary1";

    private Button            mButton;
    private String            mAdRemovalPrice;
    private SharedPreferences mSharedPreferences;

    private BillingClient mBillingClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        mBillingClient.startConnection(this);

        findViewById(R.id.mBuyButton).setOnClickListener(this);
    }


    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.mBuyButton)
        {
            /*BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(null)//////////////////////////////////////////
                    .build();

            BillingResult responseCode = mBillingClient.launchBillingFlow(this, flowParams);*/
        }
    }


    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
        {
            List skuList = new ArrayList<>();
            skuList.add(ITEM_SKU_ADREMOVAL);
            SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
            mBillingClient.querySkuDetailsAsync(params.build(), this);
        }
    }

    @Override
    public void onBillingServiceDisconnected()
    {
        // IMPLEMENT RETRY POLICY - TRY TO RESTART ON NEXT REQUEST BY CALLING startConnection()
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

    }

    @Override
    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null)
        {
            for (SkuDetails skuDetails : skuDetailsList)
            {
                String sku   = skuDetails.getSku();
                String price = skuDetails.getPrice();

                if (ITEM_SKU_ADREMOVAL.equals(sku))
                {
                    mAdRemovalPrice = price;
                }
            }
        }
    }
}
