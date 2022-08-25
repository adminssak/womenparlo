package com.smartwebarts.rogrows.retrofit;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.rampo.updatechecker.UpdateChecker;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.address.DeliveryProductDetails;
import com.smartwebarts.rogrows.dashboard.ui.home.SliderImageData;
import com.smartwebarts.rogrows.models.AddWishListResponse;
import com.smartwebarts.rogrows.models.AddressModel;
import com.smartwebarts.rogrows.models.AmountModel;
import com.smartwebarts.rogrows.models.CategoryModel;
import com.smartwebarts.rogrows.models.CouponModels;
import com.smartwebarts.rogrows.models.HomeProductsModel;
import com.smartwebarts.rogrows.models.MessageModel;
import com.smartwebarts.rogrows.models.MyOrderModel;
import com.smartwebarts.rogrows.models.NewsModel;
import com.smartwebarts.rogrows.models.OTPModel;
import com.smartwebarts.rogrows.models.OrderDetailModel;
import com.smartwebarts.rogrows.models.OrderIdModel;
import com.smartwebarts.rogrows.models.OrderListModel;
import com.smartwebarts.rogrows.models.OrderUpdateModel;
import com.smartwebarts.rogrows.models.OrderedResponse;
import com.smartwebarts.rogrows.models.PincodeModel;
import com.smartwebarts.rogrows.models.ProductDetailImagesModel;
import com.smartwebarts.rogrows.models.ProductDetailModel;
import com.smartwebarts.rogrows.models.ProductModel;
import com.smartwebarts.rogrows.models.RegisterSocialModel;
import com.smartwebarts.rogrows.models.SignUpModel;
import com.smartwebarts.rogrows.models.SocialDataCheckModel;
import com.smartwebarts.rogrows.models.SubCategoryModel;
import com.smartwebarts.rogrows.models.SubSubCategoryModel;
import com.smartwebarts.rogrows.models.VendorModel;
import com.smartwebarts.rogrows.models.VersionModel;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.shared_preference.LoginData;
import com.smartwebarts.rogrows.utils.ApplicationConstants;

import org.jetbrains.annotations.NotNull;

public enum UtilMethods {

    INSTANCE;

    public boolean isNetworkAvialable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void internetNotAvailableMessage(Context context) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        dialog .setContentText("Internet Not Available");
        dialog.show();
    }

    public boolean isValidMobile(String mobile) {

//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String mobilePattern = "^(?:0091|\\\\+91|0)[7-9][0-9]{9}$";
        String mobileSecPattern = "[7-9][0-9]{9}$";

        if (mobile.matches(mobilePattern) || mobile.matches(mobileSecPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidEmail(String email) {

        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public Dialog getProgressDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_progress_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.progress);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        return dialog;
    }

    public void Login(final Context context, String mobile, String password , final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<LoginData> call = git.login( mobile, password);
            call.enqueue(new Callback<LoginData>() {
                @Override
                public void onResponse(@NotNull Call<LoginData> call, @NotNull Response<LoginData> response) {
                    dialog.dismiss();
                   String strResponse = new Gson().toJson(response.body());
                   Log.e("LoginResponse",strResponse);
                   if (response.body()!=null) {
                       if (response.body().getId()!=null && !response.body().getId().isEmpty()) {
                           callBackResponse.success("", strResponse);
                       }
                       else {
                           callBackResponse.fail("Invalid UserId or Password");
                       }
                   } else {
                       callBackResponse.fail("Invalid UserId or Password");
                   }
                }

                @Override
                public void onFailure(@NotNull Call<LoginData> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }


    public void socialcheck(final Context context, String email , final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<SocialDataCheckModel> call = git.checkssocialuser( "1", email);
            call.enqueue(new Callback<SocialDataCheckModel>() {
                @Override
                public void onResponse(@NotNull Call<SocialDataCheckModel> call, @NotNull Response<SocialDataCheckModel> response) {
                    dialog.dismiss();
                   String strResponse = new Gson().toJson(response.body());
                   Log.e("strResponse",strResponse);
                   if (response.body()!=null) {
                       if (response.body().getMessage()!=null && response.body().getMessage().equalsIgnoreCase("success")) {
                           callBackResponse.success("", strResponse);
                       }
                       else {
                           callBackResponse.fail(""+response.body().getMessage());
                       }
                   } else {
                       callBackResponse.fail("Invalid UserId");
                   }
                }

                @Override
                public void onFailure(@NotNull Call<SocialDataCheckModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void signupsocialuser(final Context context, String email, String mobile, String name, String custid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<RegisterSocialModel> call = git.signupsocialuser("1", email, mobile, custid, name);
            call.enqueue(new Callback<RegisterSocialModel>() {
                @Override
                public void onResponse(@NotNull Call<RegisterSocialModel> call, @NotNull Response<RegisterSocialModel> response) {
                    dialog.dismiss();
                   String strResponse = new Gson().toJson(response.body());
                   Log.e("strResponse",strResponse);
                   if (response.body()!=null) {
                       if (response.body().getMessage()!=null && !response.body().getMessage().equalsIgnoreCase("success")) {
                           callBackResponse.success("", strResponse);
                       }
                       else {
                           callBackResponse.fail(""+response.body().getMessage());
                       }
                   } else {
                       callBackResponse.fail("Invalid UserId");
                   }
                }

                @Override
                public void onFailure(@NotNull Call<RegisterSocialModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail("Unable to signin. Try after some time");
            dialog.dismiss();
        }

    }

    public void otp(final Context context,  String mobile, String sms, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<OTPModel> call = git.otp( mobile, sms);
            call.enqueue(new Callback<OTPModel>() {
                @Override
                public void onResponse(@NotNull Call<OTPModel> call, @NotNull Response<OTPModel> response) {
                    dialog.dismiss();
                   String strResponse = new Gson().toJson(response.body());
                   Log.e("strResponse",strResponse);
                   if (response.body()!=null) {
                       if (response.body().getMessage().equalsIgnoreCase("success")) {
                           callBackResponse.success("", strResponse);
                       }
                       else {
                           callBackResponse.fail(response.body().getMessage());
                       }
                   } else {
                       callBackResponse.fail("Invalid UserId or Password");
                   }
                }

                @Override
                public void onFailure(@NotNull Call<OTPModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }
    public void returnProductByProductID(final Context context, String productID, String mob, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<MessageModel> call = git.returnProductByProId( productID, mob);
            call.enqueue(new Callback<MessageModel>() {
                @Override
                public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getMsg()!=null && response.body().getMsg().equalsIgnoreCase("sucess") ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
//                            callBackResponse.fail("No orders");
                            callBackResponse.fail(response.body().getMsg());
                        }
                    } else {
//                        callBackResponse.fail("No orders");
                        if (response.body() != null) {
                            callBackResponse.fail(response.body().getMsg());
                        } else {
                            callBackResponse.fail("No Response");
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }
    public void order(final Context context, DeliveryProductDetails data, String totalamount, String discount, String deliverycharge, int i, String message, final mCallBackResponse callBackResponse) {
        final Dialog dialog = getProgressDialog(context);
        dialog.show();
        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<OrderedResponse> call = git.order(data.getId(),
                     data.getQty(), data.getProId(), data.getAmount(), data.getName(), data.getUnit(), data.getUnit_in()
            ,data.getThumbnail(), data.getMobile(), data.getOrderid(), "1", data.getPaymentmethod(), data.getAddress(),data.getCity(), data.getState(),
                    data.getLandmark(), data.getPincode(), data.getUserdate(), data.getUsertime(), totalamount, discount, deliverycharge, message);
            call.enqueue(new Callback<OrderedResponse>() {
                @Override
                public void onResponse(@NotNull Call<OrderedResponse> call, @NotNull Response<OrderedResponse> response) {
                    dialog.dismiss();
                   String strResponse = new Gson().toJson(response.body());
                   Log.e("strResponse",strResponse);
                   if (response.body()!=null) {
                       if (response.body().getMessage().equalsIgnoreCase("success")) {
                           callBackResponse.success(""+i, strResponse);
                       }
                       else {
                           callBackResponse.success(""+i, strResponse);
                       }
                   } else {
                       callBackResponse.success(""+i, "No Response From Server");
                   }

                   dialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<OrderedResponse> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void signup(final Context context,  String fullname, String email,String mobile, String password, String custid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<SignUpModel> call = git.signup(fullname, email, mobile, custid, password);
            call.enqueue(new Callback<SignUpModel>() {
                @Override
                public void onResponse(@NotNull Call<SignUpModel> call, @NotNull Response<SignUpModel> response) {
                    dialog.dismiss();
                   String strResponse = new Gson().toJson(response.body());
                   Log.e("strResponse",strResponse);
                   if (response.body()!=null) {
                       if (response.body().getMsg().equalsIgnoreCase("success")) {
                           callBackResponse.success("", strResponse);
                       }
                       else {
                           callBackResponse.fail(response.body().getMsg());
                       }
                   } else {
                       callBackResponse.fail("Invalid UserId or Password");
                   }
                }

                @Override
                public void onFailure(@NotNull Call<SignUpModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void TodayOrder(final Context context, String userid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<MyOrderModel>> call = git.TodayOrder("1", userid);
            call.enqueue(new Callback<List<MyOrderModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<MyOrderModel>> call, @NotNull Response<List<MyOrderModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No orders");
                        }
                    } else {
                        callBackResponse.fail("No orders");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<MyOrderModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }


    public void orderhistory(final Context context, String userid, String status, final mCallBackResponse callBackResponse) {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.default_progress_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.progress);
        DoubleBounce doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        dialog.show();

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<OrderListModel>> call = git.orderhistory(userid, status);
            call.enqueue(new Callback<List<OrderListModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<OrderListModel>> call, @NotNull Response<List<OrderListModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No orders");
                        }
                    } else {
                        callBackResponse.fail("No orders");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<OrderListModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void OrderDetails(final Context context, String orderid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<OrderDetailModel>> call = git.OrderDetails("1", orderid);
            call.enqueue(new Callback<List<OrderDetailModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<OrderDetailModel>> call, @NotNull Response<List<OrderDetailModel>> response) {

                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No orders");
                        }
                    } else {
                        callBackResponse.fail("No orders");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<OrderDetailModel>> call, @NotNull Throwable t) {
                    dialog.dismiss();
                    callBackResponse.fail(t.getMessage());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
        }

    }
    public void OrderUpdate(final Context context, String orderid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<OrderUpdateModel> call = git.OrderUpdate("1", orderid);
            call.enqueue(new Callback<OrderUpdateModel>() {
                @Override
                public void onResponse(@NotNull Call<OrderUpdateModel> call, @NotNull Response<OrderUpdateModel> response) {

                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getMessage() != null && response.body().getMessage().equalsIgnoreCase("success")) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No orders");
                        }
                    } else {
                        callBackResponse.fail("No orders");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OrderUpdateModel> call, @NotNull Throwable t) {
                    dialog.dismiss();
                    callBackResponse.fail(t.getMessage());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
        }

    }
    public void OrderHistory(final Context context, String userid, final mCallBackResponse callBackResponse) {
        final Dialog dialog = getProgressDialog(context);
        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<MyOrderModel>> call = git.OrderHistory("1", userid);
            call.enqueue(new Callback<List<MyOrderModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<MyOrderModel>> call, @NotNull Response<List<MyOrderModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No orders");
                        }
                    } else {
                        callBackResponse.fail("No orders");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<MyOrderModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }
    public void addtowishlist(final Context context, String productid, String userid, String unitin, String unit, final mCallBackResponse callBackResponse) {
        /*      final Dialog dialog = getProgressDialog(context);*/

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<AddWishListResponse> call = git.addtowishlist(productid, userid, unitin, unit, "1");
            call.enqueue(new Callback<AddWishListResponse>() {
                @Override
                public void onResponse(@NotNull Call<AddWishListResponse> call, @NotNull Response<AddWishListResponse> response) {
                   // dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.isSuccessful()) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No Response");
                        }
                    } else {
                        callBackResponse.fail("No Response");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddWishListResponse> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                   // dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
           // dialog.dismiss();
        }

    }

    public void removewishlist(final Context context, String productid, String userid, String unitin, String unit, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<AddWishListResponse> call = git.removewishlist(productid, userid, unitin, unit, "1");
            call.enqueue(new Callback<AddWishListResponse>() {
                @Override
                public void onResponse(@NotNull Call<AddWishListResponse> call, @NotNull Response<AddWishListResponse> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.isSuccessful()) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No Response");
                        }
                    } else {
                        callBackResponse.fail("No Response");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddWishListResponse> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }
    public void removewishlistall(final Context context, String userid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<AddWishListResponse> call = git.removewishlistall("1",userid);
            call.enqueue(new Callback<AddWishListResponse>() {
                @Override
                public void onResponse(@NotNull Call<AddWishListResponse> call, @NotNull Response<AddWishListResponse> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.isSuccessful()) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No Response");
                        }
                    } else {
                        callBackResponse.fail("No Response");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddWishListResponse> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }
    public void categories(Context context, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<CategoryModel>> call = git.categories();
            call.enqueue(new Callback<List<CategoryModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<CategoryModel>> call, @NotNull Response<List<CategoryModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<CategoryModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }
    public void subCategories(Context context, String id, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<SubCategoryModel>> call = git.subCategory(id);
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<SubCategoryModel>> call, @NotNull Response<List<SubCategoryModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<SubCategoryModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }
    public void subCategories2(Context context, String id, final mCallBackResponse callBackResponse) {

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<SubCategoryModel>> call = git.subCategory(id);
            call.enqueue(new Callback<List<SubCategoryModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<SubCategoryModel>> call, @NotNull Response<List<SubCategoryModel>> response) {
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<SubCategoryModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
        }
    }
    public void imageSlider(Context context, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<SliderImageData>> call = git.imageSlider();
            call.enqueue(new Callback<List<SliderImageData>>() {
                @Override
                public void onResponse(@NotNull Call<List<SliderImageData>> call, @NotNull Response<List<SliderImageData>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<SliderImageData>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void homeProducts(Context context, String id, final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<HomeProductsModel>> call = git.homeProducts(id);
            call.enqueue(new Callback<List<HomeProductsModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<HomeProductsModel>> call, @NotNull Response<List<HomeProductsModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<HomeProductsModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }

    public void subSubCategory(Context context, String id, String subid, final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<SubSubCategoryModel>> call = git.subsubCategory(id, subid);
            call.enqueue(new Callback<List<SubSubCategoryModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<SubSubCategoryModel>> call, @NotNull Response<List<SubSubCategoryModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<SubSubCategoryModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }

    public void products(Context context, String id, String subid, String subsubid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductModel>> call = git.products(id, subid, subsubid);
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductModel>> call, @NotNull Response<List<ProductModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void trending(Context context, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductModel>> call = git.trendingnow();
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductModel>> call, @NotNull Response<List<ProductModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void bestseller(Context context, final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductModel>> call = git.bestseller();
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductModel>> call, @NotNull Response<List<ProductModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }


    public void products(Context context, String id, String subid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductModel>> call = git.products(id, subid);
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductModel>> call, @NotNull Response<List<ProductModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }
    public void getProductDetails(Context context, String id, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductDetailModel>> call = git.getProductDetails(id);
            call.enqueue(new Callback<List<ProductDetailModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductDetailModel>> call, @NotNull Response<List<ProductDetailModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductDetailModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void getProductImages(Context context, String id, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductDetailImagesModel>> call = git.getProductImages(id);
            call.enqueue(new Callback<List<ProductDetailImagesModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductDetailImagesModel>> call, @NotNull Response<List<ProductDetailImagesModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductDetailImagesModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }


    public void getCatImages(Context context, String id, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductDetailImagesModel>> call = git.getProductImages(id);
            call.enqueue(new Callback<List<ProductDetailImagesModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductDetailImagesModel>> call, @NotNull Response<List<ProductDetailImagesModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductDetailImagesModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }


    public void orderstatus_updateapp(Context context, String orderid, String type, String reason,  final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductDetailImagesModel>> call = git.orderstatus_updateapp(orderid, type, reason);
            call.enqueue(new Callback<List<ProductDetailImagesModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductDetailImagesModel>> call, @NotNull Response<List<ProductDetailImagesModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
//                    Log.e("strResponse",strResponse);
//                    if (response.body()!=null) {
//                        if (response.body().size()>0 ) {
//                            callBackResponse.success("", strResponse);
//                        }
//                        else {
//                            callBackResponse.fail("No data");
//                        }
//                    } else {
//                        callBackResponse.fail("No data");
//                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductDetailImagesModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }


    public void getWishlist(Context context, String id, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductModel>> call = git.apiGetwishlist(id);
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductModel>> call, @NotNull Response<List<ProductModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void search(Context context, String search, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<SearchModel>> call = git.search(search);
            call.enqueue(new Callback<List<SearchModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<SearchModel>> call, @NotNull Response<List<SearchModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<SearchModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }


    public void coupons(Context context, String uid, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<CouponModels>> call = git.getCouponCodes(uid);
            call.enqueue(new Callback<List<CouponModels>>() {
                @Override
                public void onResponse(@NotNull Call<List<CouponModels>> call, @NotNull Response<List<CouponModels>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No Coupons");
                        }
                    } else {
                        callBackResponse.fail("No Coupons");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<CouponModels>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void sendMail(Context context, String uid, String orderid, final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);
        if(isNetworkAvialable(context)) {
        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<CouponModels>> call = git.sendMail(uid, orderid);
            call.enqueue(new Callback<List<CouponModels>>() {
                @Override
                public void onResponse(@NotNull Call<List<CouponModels>> call, @NotNull Response<List<CouponModels>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No Coupons");
                        }
                    } else {
                        callBackResponse.fail("No Coupons");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<CouponModels>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
        }
        else {internetNotAvailableMessage(context);}
    }


    public void orderid(Context context, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<OrderIdModel> call = git.orderid("1");
            call.enqueue(new Callback<OrderIdModel>() {
                @Override
                public void onResponse(@NotNull Call<OrderIdModel> call, @NotNull Response<OrderIdModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getOrderid()!=null && !response.body().getOrderid().isEmpty()) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("Unable to get order id");
                        }
                    } else {
                        callBackResponse.fail("Unable to get order id");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OrderIdModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void cancelOrder(Context context, String orderId, String reason, String mobile, String status, mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<OrderIdModel> call;
            String type = "";
            if (status.equalsIgnoreCase(context.getString(R.string.delivered))) {
                call = git.statusreturn("1", orderId, reason, mobile);
                type = "Returned";
            } else {
                call = git.cancelorder("1", orderId, reason, mobile);
                type = "Cancelled";
            }
            call.enqueue(new Callback<OrderIdModel>() {
                @Override
                public void onResponse(@NotNull Call<OrderIdModel> call, @NotNull Response<OrderIdModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getOrderid()!=null && !response.body().getOrderid().isEmpty()) {

                            orderstatus_updateapp(context, orderId, "Cancelled", reason, new mCallBackResponse() {
                                @Override
                                public void success(String from, String message) {

                                }

                                @Override
                                public void fail(String from) {

                                }
                            });

                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("Unable to get order id");
                        }
                    } else {
                        callBackResponse.fail("Unable to get order id");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OrderIdModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void hideorder(Context context, String orderId, String reason, String mobile, mCallBackResponse callBackResponse) {

        if (isNetworkAvialable(context)) {

            try {
                EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
                Call<OrderIdModel> call = git.hideorder("1", orderId, reason, mobile);
                call.enqueue(new Callback<OrderIdModel>() {
                    @Override
                    public void onResponse(@NotNull Call<OrderIdModel> call, @NotNull Response<OrderIdModel> response) {
//                        dialog.dismiss();
                        String strResponse = new Gson().toJson(response.body());
                        Log.e("strResponse",strResponse);
                        if (response.body()!=null) {
                            if (response.body().getOrderid()!=null && !response.body().getOrderid().isEmpty()) {
                                callBackResponse.success("", strResponse);
                            }
                            else {
                                callBackResponse.fail("Unable to get order id");
                            }
                        } else {
                            callBackResponse.fail("Unable to get order id");
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<OrderIdModel> call, @NotNull Throwable t) {
                        callBackResponse.fail(t.getMessage());
//                        dialog.dismiss();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                callBackResponse.fail(e.getMessage());
//                dialog.dismiss();
            }
        } else {
            internetNotAvailableMessage(context);
        }
    }

    public void addwallet(Context context, String userid, String amount, String rpayid, mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<MessageModel> call = git.addWallet("1", userid, amount, rpayid);
            call.enqueue(new Callback<MessageModel>() {
                @Override
                public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getMsg()!=null && response.body().getMsg().equalsIgnoreCase("success")) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("Failed");
                        }
                    } else {
                        callBackResponse.fail("Failed");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void userWallet(Context context, String id, final mCallBackResponse callBackResponse) {

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<AmountModel>> call = git.userWallet(id);
            call.enqueue(new Callback<List<AmountModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<AmountModel>> call, @NotNull Response<List<AmountModel>> response) {
//                    dialog.dismiss();
                    if (response.body()!=null) {
                        if (response.body().size()>0 && !response.body().get(0).getAmount().isEmpty() ) {

                            String strResponse = new Gson().toJson(response.body().get(0));
                            Log.e("strResponse",strResponse);
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("0.00");
                        }
                    } else {
                        callBackResponse.fail("0.00");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<AmountModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail("0.00");
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail("0.00");
//            dialog.dismiss();
        }
    }

    public void addWallet(Context context, String transactionid, String amount, String userid, String usermobile, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<MessageModel> call = git.addwallet(transactionid, amount, userid, usermobile);
            call.enqueue(new Callback<MessageModel>() {
                @Override
                public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getMsg() !=null && !response.body().getMsg().isEmpty() ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("0.00");
                        }
                    } else {
                        callBackResponse.fail("0.00");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                    callBackResponse.fail("0.00");
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail("0.00");
            dialog.dismiss();
        }
    }

    public void vendor(Context context, String newText, mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<VendorModel>> call = git.getVendors();
            call.enqueue(new Callback<List<VendorModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<VendorModel>> call, @NotNull Response<List<VendorModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<VendorModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }


    public void getTimeSlot(Context context, String pincode, mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<TimeModel>> call = git.getTimeSlot(pincode );
            call.enqueue(new Callback<List<TimeModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<TimeModel>> call, @NotNull Response<List<TimeModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {

                            if (response.body().get(0).getMessage()==null) {
                                callBackResponse.success("", strResponse);
                            }
                            else if (response.body().get(0).getMessage()!=null && response.body().get(0).getMessage().equalsIgnoreCase("Our service is not avaiable in this pincode.")) {
                                callBackResponse.fail("Our service is not avaiable in this pincode.");
                            } else {
                                callBackResponse.success("", strResponse);
                            }
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<TimeModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void getDeliveryCharges(Context context,  mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<DeliveryChargesModel>> call = git.getDeliveryCharges();
            call.enqueue(new Callback<List<DeliveryChargesModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<DeliveryChargesModel>> call, @NotNull Response<List<DeliveryChargesModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {

                            SharedPreferences sharedpreferences = context.getSharedPreferences(ApplicationConstants.INSTANCE.DELIVERY_PREFS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(ApplicationConstants.INSTANCE.DELIVERY_CHARGES, strResponse);
                            editor.apply();
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<DeliveryChargesModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void vendorwiseproducts(Context context, String id, mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<ProductModel>> call = git.vendorwiseproduct(id);
            call.enqueue(new Callback<List<ProductModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<ProductModel>> call, @NotNull Response<List<ProductModel>> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<ProductModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }
    }

    public void changePassword(final Context context, String number, String newpassword, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<OTPModel> call = git.changePassword( number,newpassword, "1");
            call.enqueue(new Callback<OTPModel>() {
                @Override
                public void onResponse(@NotNull Call<OTPModel> call, @NotNull Response<OTPModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body()!=null && response.body().getMessage().equalsIgnoreCase("success")) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail(response.body().getMessage());
                        }
                    } else {
                        callBackResponse.fail("Invalid Mobile Number");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<OTPModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void getaddress(final Context context, String number, String newpassword, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<AddressModel> call = git.getaddress( number,newpassword);
            call.enqueue(new Callback<AddressModel>() {
                @Override
                public void onResponse(@NotNull Call<AddressModel> call, @NotNull Response<AddressModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getMessage().equalsIgnoreCase("success")) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail(response.body().getMessage());
                        }
                    } else {
                        callBackResponse.fail("");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddressModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void setAddress(final Context context, String id, String area, String city, String state, String house, String pin, String landmark, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<AddressModel> call = git.setAddress("1", id, house+", "+ area, ""+city, ""+state, landmark, pin);
            call.enqueue(new Callback<AddressModel>() {
                @Override
                public void onResponse(@NotNull Call<AddressModel> call, @NotNull Response<AddressModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body()!=null && response.body().getMessage().equalsIgnoreCase("success")) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail(response.body().getMessage());
                        }
                    } else {
                        callBackResponse.fail("");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddressModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

  /*  public void version(Context context, final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<VersionModel>> call = git.version();
            call.enqueue(new Callback<List<VersionModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<VersionModel>> call, @NotNull Response<List<VersionModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0) {
//                            callbackresponse.success("", strresponse);
                            if (response.body().get(0).getVcode() > BuildConfig.VERSION_CODE) {
                                UpdateDialog(context);
                            }
                        }
                        else {
//                            callBackResponse.fail("No data");
                        }
                    } else {
//                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<VersionModel>> call, @NotNull Throwable t) {
//                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }*/

    public void UpdateDialog(final Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.update_available, null);

        TextView tvLater = (TextView) view.findViewById(R.id.tv_later);
        Button tvOk = (Button) view.findViewById(R.id.tv_ok);

        final Dialog dialog = new Dialog(context);

        dialog.setCancelable(false);
        dialog.setContentView(view);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMarket(context);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static void goToMarket(Context mContext) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(UpdateChecker.ROOT_PLAY_STORE_DEVICE + mContext.getPackageName())));
    }

    public void setUserDetails(final Context context, String id, String name, String email, final mCallBackResponse callBackResponse) {

        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<AddressModel> call = git.setUserDetails("1", id, name, email);
            call.enqueue(new Callback<AddressModel>() {
                @Override
                public void onResponse(@NotNull Call<AddressModel> call, @NotNull Response<AddressModel> response) {
                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().getMessage().equalsIgnoreCase("success")) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail(response.body().getMessage());
                        }
                    } else {
                        callBackResponse.fail("");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<AddressModel> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
            dialog.dismiss();
        }

    }

    public void uploadImage(Context context, File file, String table, String colName, final mCallBackResponse callBackResponse) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(context)) {
            final Dialog dialog = getProgressDialog(context);
            dialog.show();
            try {

                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), file);
                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part partfile = MultipartBody.Part.createFormData(colName, file.getName(), requestFile);

                AppSharedPreferences preferences = new AppSharedPreferences(((Activity) context).getApplication());
                EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
                Call<MessageModel> call = git.updateImage(
                        RequestBody.create(MediaType.parse("multipart/form-data"), preferences.getLoginUserLoginId() ),
                        RequestBody.create(MediaType.parse("multipart/form-data"), "1"),
                        RequestBody.create(MediaType.parse("multipart/form-data"), file.getName() ),
                        partfile);
                call.enqueue(new Callback<MessageModel>() {
                    @Override
                    public void onResponse(@NotNull Call<MessageModel> call, @NotNull Response<MessageModel> response) {
                        dialog.dismiss();
                        String strResponse = new Gson().toJson(response.body());
                        Log.e("strResponse",strResponse);
                        if (response.body()!=null && response.body().getMsg()!=null && response.body().getMsg().equalsIgnoreCase("success")) {
                            callBackResponse.success("", response.body().getStatus());
                        } else {
                            if (response.body() != null) {
                                callBackResponse.fail(""+response.body().getError());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<MessageModel> call, @NotNull Throwable t) {
                        callBackResponse.fail(t.getMessage());
                        dialog.dismiss();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                callBackResponse.fail(e.getMessage());
                dialog.dismiss();
            }
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(context);
        }
    }


    public void news(Context context, final mCallBackResponse callBackResponse) {

//        final Dialog dialog = getProgressDialog(context);

        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);
            Call<List<NewsModel>> call = git.news();
            call.enqueue(new Callback<List<NewsModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<NewsModel>> call, @NotNull Response<List<NewsModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strResponse",strResponse);
                    if (response.body()!=null) {
                        if (response.body().size()>0 ) {
                            callBackResponse.success("", strResponse);
                        }
                        else {
                            callBackResponse.fail("No data");
                        }
                    } else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<NewsModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }

    public void getPincode(Context context, final mCallBackResponse callBackResponse) {


        try {
            EndPointInterface git = APIClient.getClient().create(EndPointInterface.class);

            Call<List<PincodeModel>> call = git.getPincode();

            call.enqueue(new Callback<List<PincodeModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<PincodeModel>> call, @NotNull Response<List<PincodeModel>> response) {
//                    dialog.dismiss();
                    String strResponse = new Gson().toJson(response.body());
                    Log.e("strRepincodesponse",strResponse);

                    if (response.body()!=null) {

                            callBackResponse.success("", strResponse);
                        }
                   else {
                        callBackResponse.fail("No data");
                    }
                }

                @Override
                public void onFailure(@NotNull Call<List<PincodeModel>> call, @NotNull Throwable t) {
                    callBackResponse.fail(t.getMessage());
//                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callBackResponse.fail(e.getMessage());
//            dialog.dismiss();
        }
    }

}
