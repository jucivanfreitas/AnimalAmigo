package com.celvansystems.projetoamigoanimal.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.celvansystems.projetoamigoanimal.R;

import java.util.ArrayList;
import java.util.List;

public class SomeActivity extends Activity implements
        PurchasesUpdatedListener {

    private BillingClient billingClient;
    private ConsumeResponseListener listener;
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    //implements BillingProcessor.IBillingHandler {

    //BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_some);

        Button btnPurchase = findViewById(R.id.btnPurchase);
        // TODO: 16/05/2019   colocar "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE" (segundo parametro)
        //bp = new BillingProcessor(this, null, this);
        //bp.initialize();
        // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
        // See below on why this is a useful alternative

        final Context ctx = this;

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

            }
        };
        // TODO: 16/05/2019  "YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE"
        //bp.purchase(SomeActivity.this, "com.android.blabla");
        //}

        listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                Log.d("INFO40", "acknowledge listender criado");

            }
        };

        PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                Log.d("INFO40", "purchased updated");
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                        && purchases != null) {
                    for (Purchase purchase : purchases) {
                        handlePurchase(purchase);
                        Log.d("INFO40", "purchased updated - acao apos purchase");
                    }
                } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                    Log.d("INFO40", "usuario cancelou");
                } else {
                    // Handle any other error codes.
                    Log.d("INFO40", "purchased outro");
                }
            }
        };

        billingClient = BillingClient.newBuilder(SomeActivity.this).enablePendingPurchases().setListener(purchasesUpdatedListener).build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d("INFO40", "setup finalizado");

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("INFO40", "servico desconectado");

            }
        });

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //boolean isAvailable = BillingProcessor.isIabServiceAvailable(SomeActivity.this);
                //if(!isAvailable) {
                // continue
                Log.d("INFO40", "botao clicado");

                ////////////////

                List<String> skuList = new ArrayList<>();
                skuList.add("doar_5_reais");
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

                billingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(BillingResult billingResult,
                                                             List<SkuDetails> skuDetailsList) {
                                // Process the result.
                                Log.d("INFO40", "onSkuDetails");

                                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {

                                    Log.d("INFO40", "response OK");

                                    for (SkuDetails skuDetails : skuDetailsList) {
                                        Log.d("INFO40", "entrou no FOR");

                                        String sku = skuDetails.getSku();
                                        String price = skuDetails.getPrice();
                                        if ("doar_5_reais".equalsIgnoreCase(sku)) {

                                            Log.d("INFO40", "doado 5 reais");
                                            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().

                                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(skuDetails)
                                                    .build();
                                            BillingResult responseCode = billingClient.launchBillingFlow(SomeActivity.this, flowParams);

                                        } else {
                                            Log.d("INFO40", "nada doado");
                                        }
                                    }
                                } else {
                                    Log.d("INFO40", "result nao OK");

                                }
                            }
                        });
            }
        });


    }

    void handlePurchase(Purchase purchase) {

        Log.d("INFO40", "handle purchase");

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            Log.d("INFO40", "Purchased " + purchase.getSku());

            // Grant entitlement to the user.
            //...

            // Acknowledge the purchase if it hasn't already been acknowledged.

            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                Log.d("INFO40", "purchase acknowledge");

            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            Log.d("INFO40", "purchase pending");

            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            Log.d("INFO40", "On Purchases Updated");

            for (Purchase purchase : purchases) {
                Log.d("INFO40", "entrou no for do purchasesUpdated");

                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            Log.d("INFO40", "canceled");

        } else {
            // Handle any other error codes.
        }
    }


    // IBillingHandler implementation

    /*@Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    /*}

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        /*Toast.makeText(this, "compra realizada!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
//}

    /*@Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    /*}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
*/
}
