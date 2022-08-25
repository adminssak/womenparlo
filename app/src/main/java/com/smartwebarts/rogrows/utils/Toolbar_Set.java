package com.smartwebarts.rogrows.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.smartwebarts.rogrows.ContactUsActivity;
import com.smartwebarts.rogrows.LoginActivity;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.cart.CartActivity;
import com.smartwebarts.rogrows.category.Category;
import com.smartwebarts.rogrows.category.CategoryActivity;
import com.smartwebarts.rogrows.dashboard.DashboardActivity;
import com.smartwebarts.rogrows.database.DatabaseClient;
import com.smartwebarts.rogrows.database.Task;
import com.smartwebarts.rogrows.history.MyHistoryActivity;
import com.smartwebarts.rogrows.models.AmountModel;
import com.smartwebarts.rogrows.profile.ProfileActivity;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.search.SearchActivity;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.shopbycategory.ShopByCategoryActivity;
import com.smartwebarts.rogrows.wallet.WalletActivity;



public enum Toolbar_Set {

    INSTANCE;
    public void setToolbar(final Activity activity){
        ImageView back = activity.findViewById(R.id.back);
        ImageView refresh = activity.findViewById(R.id.refresh);
        TextView searchuser = activity.findViewById(R.id.searchuser);
        ImageView profile = activity.findViewById(R.id.person);
        FrameLayout showCart = activity.findViewById(R.id.showCart);
        searchuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(activity,SearchActivity.class);
              activity.startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh(activity);
            }
        });

        showCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCart(activity);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(activity);
            }
        });

        AppSharedPreferences preferences = new AppSharedPreferences(activity.getApplication());
        String tempname = preferences.getLoginUserName();

        if (tempname.isEmpty()) {
            profile.setVisibility(View.GONE);
        }
        getCartList(activity);
    }

    private void openProfile(Activity activity) {


        AppSharedPreferences preferences = new AppSharedPreferences(activity.getApplication());
        String user;

        user = preferences.getLoginDetails();
        if (user == null || user.isEmpty()) {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
            activity.startActivity(intent);
        }
        else {
            Intent intent=new Intent(activity, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            activity.startActivity(intent);
        }

    }
    private void openSearch(Activity activity) {
        Intent intent=new Intent(activity, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public void setToolbar(final Activity activity, String name){
        ImageView back = activity.findViewById(R.id.back);
        TextView searchuser = activity.findViewById(R.id.searchuser);
        ImageView profile = activity.findViewById(R.id.person);
        FrameLayout showCart = activity.findViewById(R.id.showCart);
        TextView tvName = activity.findViewById(R.id.tv_name);
        tvName.setText(name);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProfile(activity);
            }
        });

//        refresh.setOnClickListener(v -> refresh(activity));
        showCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toolbar_Set.this.showCart(activity);
            }
        });

        AppSharedPreferences preferences = new AppSharedPreferences(activity.getApplication());
        String tempname = preferences.getLoginUserName();

        if (tempname.isEmpty()) {
            profile.setVisibility(View.GONE);
        }

        searchuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch(activity);
            }
        });
        getCartList(activity);
    }

    public void getCartList(final Activity activity) {

        class GetTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

            @Override
            protected ArrayList<Task> doInBackground(Void... voids) {
                List<Task> tasks= DatabaseClient.getmInstance(activity.getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return new ArrayList<>(tasks);
            }

            @Override
            protected void onPostExecute(ArrayList<Task> tasks) {
                int size = tasks!=null?tasks.size():0;
                TextView cartItemsCount = activity.findViewById(R.id.cartItemsCount);
                if (cartItemsCount != null) {
                    cartItemsCount.setText(""+size);
                }

                TextView cart_badge = activity.findViewById(R.id.cart_badge);
                if (cart_badge !=null) {
                    cart_badge.setText(""+size);
                }

                try{

                    double sum = 0;
                    for (Task task: tasks) {
                        double price = Double.parseDouble("0"+(task.getCurrentprice()==null?"":task.getCurrentprice()));
                        double qty = Double.parseDouble("0"+task.getQuantity());
                        double total = price*qty;
                        sum+=total;
                    }

                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(2);
                    TextView utility = activity.findViewById(R.id.utility);

                    if (activity instanceof CartActivity) {
                        if (utility != null) {
                            utility.setVisibility(View.GONE);
                        }
                        return;
                    }
                    if (utility!=null) {
                        utility.setText(activity.getString(R.string.currency) + " "+ nf.format(sum));
                        utility.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                userWallet(activity);

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    public void showCart(Activity activity){
        Intent intent = new Intent(activity, CartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
    }

    public void refresh(Activity activity) {
        Toolbar_Set.INSTANCE.getCartList(activity);
    }


    public void delete(final Activity activity, final Task task) {
        class GetTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

            @Override
            protected ArrayList<Task> doInBackground(Void... voids) {
                        DatabaseClient.getmInstance(activity.getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .delete(task);
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Task> tasks) {
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    public void setBottomNav(final Activity activity) {
        final BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);

        BottomNavigationMenuView mbottomNavigationMenuView =
                (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        View view = mbottomNavigationMenuView.getChildAt(3);

        BottomNavigationItemView itemView = (BottomNavigationItemView) view;

     /*   View cart_badge = LayoutInflater.from(activity)
                .inflate(R.layout.view_alertsbadge,
                        mbottomNavigationMenuView, false);

        itemView.addView(cart_badge);*/

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.home) {
                    if (!(activity instanceof DashboardActivity)) {
                        activity.startActivity(new Intent(activity, DashboardActivity.class));
                        activity.finishAffinity();
                    } else {
                        NavController graph = Navigation.findNavController(activity.findViewById(R.id.nav_host_fragment));
                        if (graph.getGraph().getId() == R.id.mobile_navigation) {
                            ((DashboardActivity) activity).onBackPressed();
                        }
                    }
                } else if (item.getItemId() == R.id.searchh) {
                    try {
//                    String url = "https://chat.whatsapp.com/";
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        String url = "https://api.whatsapp.com/send?phone="+"+91";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                      activity.startActivity(i);
                    } catch (Exception e) {
                        Toast.makeText(activity, "Unable to open your whatsapp", Toast.LENGTH_SHORT).show();
                    }

                   /* if (!(activity instanceof SearchActivity)) {
                        activity.startActivity(new Intent(activity, SearchActivity.class));
                    }*/
                } else if (item.getItemId() == R.id.cartt) {
                   /* if (!(activity instanceof CartActivity)) {
                        activity.startActivity(new Intent(activity, MyHistoryActivity.class));*/
                    openProfile(activity);

                } else if (item.getItemId() == R.id.category) {
                    if (!(activity instanceof ContactUsActivity)) {
                        activity.startActivity(new Intent(activity, ShopByCategoryActivity.class));
                    }
                }

                bottomNavigationView.setSelected(false);
                return false;
            }
        });
    }


    public void userWallet(Activity activity) {
        AppSharedPreferences preferences = new AppSharedPreferences(activity.getApplication());
        if (preferences.getLoginUserLoginId() !=null ){
            TextView utility = activity.findViewById(R.id.utility);
            if (UtilMethods.INSTANCE.isNetworkAvialable(activity)) {
                UtilMethods.INSTANCE.userWallet(activity, preferences.getLoginUserLoginId(), new mCallBackResponse() {
                    @Override
                    public void success(String from, String message) {

                        AmountModel amountModel = new Gson().fromJson(message, AmountModel.class);

//                        if (utility!=null) {
//                            utility.setText(activity.getString(R.string.currency) + " "+ amountModel.getAmount());
//                            utility.setVisibility(View.VISIBLE);
//                        }

                        if (activity instanceof WalletActivity) {
                            TextView Wallet_Ammount = (TextView) activity.findViewById(R.id.wallet_ammount);
                            Wallet_Ammount.setText(activity.getString(R.string.currency) + " "+ amountModel.getAmount());
                        }
                    }

                    @Override
                    public void fail(String from) {

                    }
                });
            }
        }
    }
}
