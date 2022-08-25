package com.smartwebarts.rogrows.productlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.smartwebarts.rogrows.MyApplication;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.CategoryModel;
import com.smartwebarts.rogrows.models.ProductDetailImagesModel;
import com.smartwebarts.rogrows.models.ProductModel;
import com.smartwebarts.rogrows.models.SubSubCategoryModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
import com.smartwebarts.rogrows.utils.CustomSlider;
import com.smartwebarts.rogrows.utils.FragmentActivityMessage;
import com.smartwebarts.rogrows.utils.GlobalBus;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ProductListActivity extends AppCompatActivity {
    RecyclerView rvProductList, rvProductGrid;
    CategoryModel category;
    TextView tv_subsubCategory;
    private ProductListGridAdapter adapter;
    private ProductListAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        Log.e("ProductListActivity", "onCreate: ");
        rvProductList = findViewById(R.id.rvProductList);
        rvProductGrid = findViewById(R.id.rvProductGrid);
        tv_subsubCategory = findViewById(R.id.subsubCategory);
//        subCategory = (SubCategoryModel) getIntent().getSerializableExtra("subCategory");
        category = (CategoryModel) getIntent().getSerializableExtra("category");
//        subSubCategory = (SubSubCategoryModel) getIntent().getSerializableExtra("subsubcategory");
        tv_subsubCategory.setText(category.getName());

        if((category.getId()).isEmpty()){
            Log.e("ProductListActivity", "onCreate: NOT");
        }else{
            Log.e("ProductListActivity", "onCreate: "+ApplicationConstants.INSTANCE.CATEGORY_IMAGES + category.getImage());
            setup_baner(ApplicationConstants.INSTANCE.CATEGORY_IMAGES + category.getImage());
        }



        MyApplication application = (MyApplication) getApplication();
        AppSharedPreferences preferences = new AppSharedPreferences(application);
        application.logLeonEvent("Category", "Category Viewed " + category.getName() + " by " + preferences.getLoginMobile(), 0);
        Toolbar_Set.INSTANCE.setToolbar(this, category.getName());
        Toolbar_Set.INSTANCE.setBottomNav(this);
        getProducts();
    }

    private void setup_baner(String pid) {
        ImageView imageView = (ImageView) findViewById(R.id.viewPager);
        Glide.with(this).load(pid).into(imageView);

//        UtilMethods.INSTANCE.getProductImages(this, pid, new mCallBackResponse() {
//            @Override
//            public void success(String from, String message) {
//                if (!message.isEmpty()){
//                    Type listType = new TypeToken<ArrayList<ProductDetailImagesModel>>(){}.getType();
//                    List<ProductDetailImagesModel> list = new Gson().fromJson(message, listType);
//                    setUpImageSlider(list);
//                }
//            }
//
//            @Override
//            public void fail(String from) {
//
//            }
//        });
    }

    private void getProducts() {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.products(this, category.getId(), "", ""/*subCategory.getId(), subSubCategory.getId()*/, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<ProductModel>>() {
                    }.getType();
                    List<ProductModel> list = new Gson().fromJson(message, listType);
                    setProduct(list);
                }
                @Override
                public void fail(String from) {
                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void setProduct(List<ProductModel> list) {
        adapter = new ProductListGridAdapter(this, list);
        rvProductGrid.setLayoutManager(new GridLayoutManager(this, 2));
        rvProductGrid.setAdapter(adapter);

        adapter2 = new ProductListAdapter(this, list);
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
        if (obj.getFrom().equalsIgnoreCase("updatelist")) {
            adapter.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
            getProducts();
        }
    }

}
