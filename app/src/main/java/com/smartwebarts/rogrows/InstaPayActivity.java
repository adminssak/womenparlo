package com.smartwebarts.rogrows;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.smartwebarts.rogrows.retrofit.EndPointInterface;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;


public class InstaPayActivity extends AppCompatActivity /*implements Instamojo.InstamojoPaymentCallback*/ {

    AppSharedPreferences preferences;
    private TextInputEditText mobile, fullname, email, amount;
    private EndPointInterface myBackendService;
    private static final String TAG = InstaPayActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_pay);

        preferences = new AppSharedPreferences(getApplication());

        mobile = findViewById(R.id.mobile);
        fullname = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        amount = findViewById(R.id.amount);

        mobile.setText(preferences.getLoginMobile().trim());
        email.setText(preferences.getLoginEmail().trim());
        fullname.setText(preferences.getLoginUserName().trim());
    }

    /*http://nauyju.com/newsainik/Welcome/payNow?
    name=tuktuk+sharma
    &email=sharmaranjana912%40gmail.com
    &mobile_no=9120397819
    &discription=djhd&amount=100*/

    public void proceedtopay(View view) {
        String url = "https://leoon.in/Welcome/payNow?"
                + "name="+preferences.getLoginUserName().trim()
                + "&email="+preferences.getLoginEmail().trim()
                + "&mobile_no="+preferences.getLoginMobile().trim()
                + "&discription=Description"
                + "&amount=250.00";

        Intent intent = new Intent(this, InstaPayWebViewActivity.class);
        intent.putExtra(InstaPayWebViewActivity.KEY_URL, url);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

//    private void createOrderOnServer() {
//        GetOrderIDRequest request = new GetOrderIDRequest();
//        request.setEnv(Instamojo.Environment.TEST.name());
//        request.setBuyerName(fullname.getText().toString().trim());
//        request.setBuyerEmail(email.getText().toString().trim());
//        request.setBuyerPhone(mobile.getText().toString().trim());
//        request.setDescription("Subscription Payment");
//        request.setAmount("250");
//
//        Call<GetOrderIDResponse> getOrderIDCall = myBackendService.createOrder(request);
//        getOrderIDCall.enqueue(new Callback<GetOrderIDResponse>() {
//            @Override
//            public void onResponse(Call<GetOrderIDResponse> call, Response<GetOrderIDResponse> response) {
//                if (response.isSuccessful()) {
//                    String orderId = response.body().getOrderID();
//
//                    initiateSDKPayment(orderId);
//
//                } else {
//                    // Handle api errors
//                    try {
//                        JSONObject jObjError = new JSONObject(response.errorBody().string());
//                        Log.d(TAG, "Error in response" + jObjError.toString());
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetOrderIDResponse> call, Throwable t) {
//                Log.d(TAG, "Failure");
//
//            }
//        });
//    }
//
//    private void initiateSDKPayment(String orderId) {
//        Instamojo.getInstance().initiatePayment(this, orderId, this);
//    }
//
//    @Override
//    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
//        String result = "Payment complete. Order ID: " + orderID + ", Transaction ID: " + transactionID
//                + ", Payment ID:" + paymentID + ", Status: " + paymentStatus;
//
//        Log.e(TAG, "Payment complete " +result);
//
//        showToast(result);
//    }
//
//    @Override
//    public void onPaymentCancelled() {
//        Log.e(TAG, "Payment cancelled");
//        showToast("Payment cancelled by user");
//    }
//
//    @Override
//    public void onInitiatePaymentFailure(String errorMessage) {
//        Log.e(TAG, "Initiate payment failed");
//        showToast("Initiating payment failed. Error: " + errorMessage);
//    }
//
//    private void showToast(String s) {
//        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
//    }
}