package com.smartwebarts.rogrows.productlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.smartwebarts.rogrows.MyApplication;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.database.SaveProductList;
import com.smartwebarts.rogrows.database.Task;
import com.smartwebarts.rogrows.models.ProductDetailImagesModel;
import com.smartwebarts.rogrows.models.ProductDetailModel;
import com.smartwebarts.rogrows.models.ProductModel;
import com.smartwebarts.rogrows.models.UnitModel;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProductDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {

    private static final String TAG = ProductDetailActivity.class.getSimpleName();
    private com.daimajia.slider.library.SliderLayout viewPager;
    private int currentPage = 0;
    private ArrayList<String> sliderImage= new ArrayList<String>();
    private ProductModel addToCartProductItem;
    private TextView tvName, txt_vName, tvDescription2, tvPrice,tvPricen, tvCurrentPrice, tvDiscount, tvOffer;
    private CardView cvoffer;
    private ImageView ivVeg;
    public static final String ID = "id";
    private String pid;
    private RecyclerView recyclerView,also_like;
    private String unit="", unitIn="", currentPrice="0", buingPrice = "0", discount = "0";
    List<ProductModel> likelist = new ArrayList<>();
    private YouMayLikeAdapter adapter;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Log.e(TAG, "onCreate: ");
        Toolbar_Set.INSTANCE.setToolbar(this);
        viewPager = findViewById(R.id.viewPager);
        tvName = findViewById(R.id.txt_pName);
        txt_vName = findViewById(R.id.txt_vName);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        tvDescription2 = findViewById(R.id.tvDescription);
        tvDiscount = findViewById(R.id.txt_discount);
        ivVeg = findViewById(R.id.iv_veg);
        tvPrice = findViewById(R.id.txt_price);
        tvPricen = findViewById(R.id.txt_pricen);
        tvCurrentPrice = findViewById(R.id.txt_current_price);
        recyclerView = findViewById(R.id.recyclerView);
        also_like = findViewById(R.id.also_like);
        pid = getIntent().getExtras().getString(ID);

        getDetails();
    }

    private void setUpImageSlider(List<ProductDetailImagesModel> list) {

        for ( ProductDetailImagesModel data : list) {
            CustomSlider textSliderView = new CustomSlider(this);
            // initialize a SliderLayout
            textSliderView
                    .description("")
                    .image(ApplicationConstants.INSTANCE.PRODUCT_IMAGES + data.getThumbnail())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            viewPager.addSlider(textSliderView);
        }

        viewPager.getPagerIndicator().setDefaultIndicatorColor (0xff68B936, 0xffD0D0D0);
        viewPager.startAutoCycle(10000, 10000, true);
        viewPager.setCurrentPosition(0);
    }

    public void addToBag(View view) {

        if (addToCartProductItem !=null){
            if (Integer.parseInt(addToCartProductItem.getUnits().get(0).getQuantity())>0) {
                List<Task> items = new ArrayList<>();
                Task task = new Task(addToCartProductItem, "1", unit, unitIn, currentPrice, currentPrice);
                items.add(task);
                SaveProductList savepro = new SaveProductList(this, items);
                savepro.isShowSuccessMsg=true;
                savepro.execute();
                Toolbar_Set.INSTANCE.getCartList(this);
            } else {
                SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                dialog .setContentText("Out Of Stock");
                dialog.show();
            }
        }

    }

     public  void getDetails() {

         if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

             UtilMethods.INSTANCE.getProductDetails(this, pid, new mCallBackResponse() {
                 @Override
                 public void success(String from, String message) {
                     if (!message.isEmpty()){
                         Type listType = new TypeToken<ArrayList<ProductDetailModel>>(){}.getType();
                         List<ProductDetailModel> list = new Gson().fromJson(message, listType);

                         addToCartProductItem = new ProductModel(list.get(0));

                         if (addToCartProductItem.getUnits() == null || Integer.parseInt("0" + addToCartProductItem.getUnits().get(0).getQuantity()) <= 0) {
                             btnAddToCart.setText(R.string.out_of_stock);
                             btnAddToCart.setEnabled(false);
                         }
                         tvName.setText(addToCartProductItem.getName().trim());

                         MyApplication application = (MyApplication) getApplication();
                         AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                         application.logLeonEvent("View content","Product Viewed " + addToCartProductItem.getName().trim() + " by "+ preferences.getLoginMobile(), 0);

                         if (list.get(0)!=null && list.get(0).getVendorName()!=null) {
                             txt_vName.setText("("+list.get(0).getVendorName().trim()+")");
                         }

                         tvDescription2.setText(addToCartProductItem.getDescription().trim());
                         if (addToCartProductItem.getUnits()!=null && addToCartProductItem.getUnits().size()>0) {
                             unit = addToCartProductItem.getUnits().get(0).getUnit().trim();
                             unitIn = addToCartProductItem.getUnits().get(0).getUnitIn().trim();
                             currentPrice = addToCartProductItem.getUnits().get(0).getBuingprice().trim();
                             buingPrice = addToCartProductItem.getUnits().get(0).getCurrentprice().trim();
                         }

                         try {
                             int a = (int) Double.parseDouble("0"+currentPrice);
                             int b = (int) Double.parseDouble("0"+buingPrice);
                             int c = b-a;
                             discount = ""+c;

                             try {
                                 int p = c*100/b;
                                 tvOffer.setText(p + "%");
                             } catch (Exception ignored){
                                 cvoffer.setVisibility(View.GONE);
                             }
                         } catch (Exception ignored) {}


                         tvCurrentPrice.setText(String.format("%s %s", getString(R.string.currency), currentPrice));
                         tvPrice.setText(String.format("%s%s", unit, unitIn));

                         tvPricen.setText(String.format("%s %s", getString(R.string.currency), buingPrice));
                         tvPricen.setPaintFlags(tvPricen.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                         tvDiscount.setText(String.format("%s%s", getString(R.string.currency), discount));

                         if (addToCartProductItem.getProductType().equalsIgnoreCase("Non-Vegetarian")) {
                             ivVeg.setImageDrawable(getDrawable(R.drawable.nonveg));
                             ivVeg.setVisibility(View.VISIBLE);
                         } else if (addToCartProductItem.getProductType().equalsIgnoreCase("Vegetarian")){
                             ivVeg.setImageDrawable(getDrawable(R.drawable.veg));
                             ivVeg.setVisibility(View.VISIBLE);
                         } else {
                             ivVeg.setVisibility(View.GONE);
                         }

                         setRecycler(addToCartProductItem.getUnits());

                         UtilMethods.INSTANCE.products(ProductDetailActivity.this, list.get(0).getCat_id(),"",""/*subCategory.getId(), subSubCategory.getId()*/,new mCallBackResponse() {
                             @Override
                             public void success(String from, String message) {
                                 Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                                 likelist = new Gson().fromJson(message, listType);
                                 adapter = new YouMayLikeAdapter(ProductDetailActivity.this, likelist);
                                 also_like.setAdapter(adapter);
                             }

                             @Override
                             public void fail(String from) {
                                 Toast.makeText(ProductDetailActivity.this, from, Toast.LENGTH_SHORT).show();

                             }
                         });

                     }

                 }

                 @Override
                 public void fail(String from) {

                 }
             });

             UtilMethods.INSTANCE.getProductImages(this, pid, new mCallBackResponse() {
                 @Override
                 public void success(String from, String message) {
                     if (!message.isEmpty()){
                         Type listType = new TypeToken<ArrayList<ProductDetailImagesModel>>(){}.getType();
                         List<ProductDetailImagesModel> list = new Gson().fromJson(message, listType);
                         setUpImageSlider(list);
                     }
                 }

                 @Override
                 public void fail(String from) {

                 }
             });
         } else {
             UtilMethods.INSTANCE.internetNotAvailableMessage(this);
         }
     }

    private void setRecycler(List<UnitModel> list) {
        UnitListAdapter adapter = new UnitListAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    public void onClick(int position) {

        if (addToCartProductItem.getUnits().get(position) !=null) {
            UnitModel temp = addToCartProductItem.getUnits().get(position);
            unit = temp.getUnit();
            unitIn = temp.getUnitIn();
            currentPrice = temp.getBuingprice();
            buingPrice = temp.getCurrentprice();
            tvCurrentPrice.setText(getString(R.string.currency) + " " + currentPrice);
            tvPricen.setText(getString(R.string.currency) + " " + buingPrice);
            tvPricen.setPaintFlags(tvPricen.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvPrice.setText(  unit + unitIn);

            try {
                int a = (int) Double.parseDouble("0"+currentPrice);
                int b = (int) Double.parseDouble("0"+buingPrice);
                int c = b-a;
                discount = ""+c;
            } catch (Exception ignored) {}

            tvDiscount.setText(getString(R.string.currency) + discount);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

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
        Log.e(TAG, "onFragmentActivityMessage: " + obj.getFrom() );
        if (obj.getFrom().equalsIgnoreCase("updatelist")) {
            adapter.notifyDataSetChanged();
            getDetails();
        }
    }
}
