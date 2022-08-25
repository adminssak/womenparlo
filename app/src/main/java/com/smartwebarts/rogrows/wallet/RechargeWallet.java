package com.smartwebarts.rogrows.wallet;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.smartwebarts.rogrows.InstaPayWebViewActivity;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.MessageModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.android.volley.VolleyLog.TAG;

public class RechargeWallet extends AppCompatActivity implements PaymentResultListener {

    public static final String RECHARGE_AMOUNT = "ra";
    private RechargeWallet activity;
    private EditText Wallet_Ammount;
    private RelativeLayout Recharge_Button;
    private String ammount = "100";
    private String userid, name, email, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_wallet);

        activity = RechargeWallet.this;
        Checkout.preload(getApplicationContext());

        Toolbar_Set.INSTANCE.setToolbar(this, "Recharge Wallet");
        Toolbar_Set.INSTANCE.setBottomNav(this);

        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        name = preferences.getLoginUserName();
        email = preferences.getLoginEmail();
        mobile = preferences.getLoginMobile();
        userid = preferences.getLoginUserLoginId();

        if (getIntent().getExtras()!=null)
            ammount = getIntent().getExtras().getString(RECHARGE_AMOUNT, "100");

        Wallet_Ammount = (EditText) findViewById(R.id.et_wallet_ammount);
        Wallet_Ammount.setText(ammount);
        Recharge_Button = (RelativeLayout) findViewById(R.id.recharge_button);

        Recharge_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ammount = Wallet_Ammount.getText().toString();
                startPayment(name,ammount,email,mobile);
//                Recharge_wallet("ergerbfhffg");
            }
        });
    }

    private void startPayment(String name, String amount, String email, String mobile) {

        /*instamojo*/
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());

        String url = ApplicationConstants.INSTANCE.SITE_URL+"Welcome/payNow?"
                + "name="+preferences.getLoginUserName().trim()
                + "&email="+preferences.getLoginEmail().trim()
                + "&contact="+preferences.getLoginMobile().trim()
                + "&discription=Description"
                + "&amount="+amount;

        Intent intent = new Intent(RechargeWallet.this, InstaPayWebViewActivity.class);
        intent.putExtra(InstaPayWebViewActivity.KEY_URL, url);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, 1234);

        /*Pay U Money*/
//        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
//        Intent intent = new Intent(RechargeWallet.this, InstaPayWebViewActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString("name", preferences.getLoginUserName());
//        bundle.putString("email", preferences.getLoginEmail());
//        bundle.putString("productInfo", "Payment for "+ getString(R.string.app_name));
//        bundle.putString("phone", preferences.getLoginMobile());
//        bundle.putString("amount", amount);
//        bundle.putString("amount", "1");

//        intent.putExtras(bundle);
//        startActivityForResult(intent, 1234);
    }

    /*Razor Pay*/

//    private void startPayment(String name, String amount, String email, String mobile) {
//
//        final Checkout co = new Checkout();
//        co.setKeyID(""+getString(R.string.razor_api_key));
//
//        try {
//
//            JSONObject options = new JSONObject();
//
//            options.put("name", name);
//            options.put("description", "Demoing Charges");
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", R.drawable.logo);
//            options.put("currency", "INR");
//
//            options.put("amount", Integer.parseInt(amount)*100);
//
//            JSONObject preFill = new JSONObject();
//
//            preFill.put("email", email);
//
//            preFill.put("contact", mobile);
//
//            options.put("prefill", preFill);
//
//            co.open(activity, options);
//
//        } catch (Exception e) {
//            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
//                    .show();
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            Recharge_wallet(s);
            Toast.makeText(this, "Wallet Recharge Successful", Toast.LENGTH_SHORT).show();
            overridePendingTransition(0, 0);

//            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Recharge_wallet("failed");
    }

    private void Recharge_wallet(String msg) {
        if (!msg.equalsIgnoreCase("failed")) {
            if (UtilMethods.INSTANCE.isNetworkAvialable(activity)) {
                UtilMethods.INSTANCE.addWallet(RechargeWallet.this, msg, ammount, userid, mobile, new mCallBackResponse() {
                    @Override
                    public void success(String from, String message) {
                        MessageModel messageModel = new Gson().fromJson(message, MessageModel.class);
                        new SweetAlertDialog(RechargeWallet.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Request")
                                .setContentText(""+messageModel.getMsg())
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        Intent intent=new Intent();
                                        setResult(123,intent);
                                        finish();
                                    }
                                })
                                .show();
                    }

                    @Override
                    public void fail(String from) {
                        new SweetAlertDialog(RechargeWallet.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Request")
                                .setContentText(""+from)
                                .show();
                    }
                });

            } else {
                UtilMethods.INSTANCE.internetNotAvailableMessage(activity);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);


        if (requestCode == 1234) {
            if (data != null) {
                String message=data.getStringExtra("MESSAGE");

                if (message != null) {
                    if (message.equalsIgnoreCase("success")) {
                        String s=data.getStringExtra("oid");
                        Recharge_wallet(s);
                        Toast.makeText(this, "Wallet Recharge Successful", Toast.LENGTH_SHORT).show();
//                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(this, "Payment Declined, Try Again", Toast.LENGTH_SHORT).show();
                        Recharge_wallet("failed");
                    }
                }
            }
        }
    }
}