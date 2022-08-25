package com.smartwebarts.rogrows.wishlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.ProductModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.FragmentActivityMessage;
import com.smartwebarts.rogrows.utils.GlobalBus;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class WishListActivity extends AppCompatActivity {
    private static final String TAG = WishListActivity.class.getSimpleName();
    RecyclerView rvProductList, rvProductGrid;
    TextView tv_subsubCategory;
    TextView clear;
    Context context;
    private List<ProductModel>list;
    private WishlistGridAdapter adapter;
    private WishlistAdapter adapter2;
    private SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        rvProductList = findViewById(R.id.rvProductList);
        rvProductGrid = findViewById(R.id.rvProductGrid);
        swiperefresh = findViewById(R.id.swiperefresh);

        tv_subsubCategory = findViewById(R.id.subsubCategory);
        clear = findViewById(R.id.clear);

        Toolbar_Set.INSTANCE.setToolbar(this, "My Wishlist");
        Toolbar_Set.INSTANCE.setBottomNav(this);
        getWishList();


        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWishList();
            }
        });
    }

    private void getWishList() {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
            if (!preferences.getLoginUserLoginId().isEmpty()) {

                UtilMethods.INSTANCE.getWishlist(this, preferences.getLoginUserLoginId(), new mCallBackResponse() {
                    @Override
                    public void success(String from, String message) {
                        Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                        List<ProductModel> list = new Gson().fromJson(message, listType);
                        setProduct(list);
                        clear.setVisibility(View.VISIBLE);
                        clear.setOnClickListener(v -> clearAll());
                        swiperefresh.setRefreshing(false);
                    }

                    @Override
                    public void fail(String from) {
                        swiperefresh.setRefreshing(false);
                    }
                });
            } else {
                Toast.makeText(this, "Login to see your wishlist", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void clearAll() {
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        UtilMethods.INSTANCE.removewishlistall(this, preferences.getLoginUserLoginId(), new mCallBackResponse() {
            @Override
            public void success(String from, String message) {
                adapter.setList(null);
                adapter2.setList(null);
            }
            @Override
            public void fail(String from) {
            }
        });
    }

    private void setProduct(List<ProductModel> list) {
        adapter = new WishlistGridAdapter(this, list);
        rvProductGrid.setLayoutManager(new GridLayoutManager(this, 2));
        rvProductGrid.setAdapter(adapter);

        adapter2 = new WishlistAdapter(this, list);
        rvProductList.setLayoutManager(new GridLayoutManager(this, 1));
        rvProductList.setAdapter(adapter2);
    }

    public void changeLayout(View view) {
        if (rvProductList.getVisibility() == View.VISIBLE) {
            rvProductList.setVisibility(View.GONE);
            rvProductGrid.setVisibility(View.VISIBLE);
            ((ImageView) view).setImageDrawable(getDrawable(R.drawable.ic_grid));
        } else {
            rvProductList.setVisibility(View.VISIBLE);
            rvProductGrid.setVisibility(View.GONE);
            ((ImageView) view).setImageDrawable(getDrawable(R.drawable.ic_view_list));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            GlobalBus.getBus().register(this);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void onFragmentActivityMessage(FragmentActivityMessage obj) {

        Log.e(TAG, "onFragmentActivityMessage: " + obj.getFrom() + " " +obj.getMessage() );

        if (obj.getFrom().equalsIgnoreCase("updatelist")) {
            getWishList();
        }
    }
}
