package com.celvansystems.projetoamigoanimal.activity;

import android.app.Activity;

public class SomeActivity extends Activity {


    //implements BillingProcessor.IBillingHandler {

    /*BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_some);

        Button btnPurchase = findViewById(R.id.btnPurchase);

        // TODO: 16/05/2019   colocar "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE" (segundo parametro)
        bp = new BillingProcessor(this, null, this);
        bp.initialize();
        // or bp = BillingProcessor.newBillingProcessor(this, "YOUR LICENSE KEY FROM GOOGLE PLAY CONSOLE HERE", this);
        // See below on why this is a useful alternative

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //boolean isAvailable = BillingProcessor.isIabServiceAvailable(SomeActivity.this);
                //if(!isAvailable) {
                // continue

                // TODO: 16/05/2019  "YOUR PRODUCT ID FROM GOOGLE PLAY CONSOLE HERE"
                bp.purchase(SomeActivity.this, "com.android.blabla");
                //}
            }
        });
    }

    // IBillingHandler implementation

    @Override
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
