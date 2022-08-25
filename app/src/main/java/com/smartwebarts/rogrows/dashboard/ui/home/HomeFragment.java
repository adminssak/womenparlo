package com.smartwebarts.rogrows.dashboard.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.smartwebarts.rogrows.MyApplication;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.CategoryModel;
import com.smartwebarts.rogrows.models.NewsModel;
import com.smartwebarts.rogrows.models.PincodeModel;
import com.smartwebarts.rogrows.models.ProductModel;
import com.smartwebarts.rogrows.productlist.BestSellerAdapter;
import com.smartwebarts.rogrows.productlist.TrendingNowAdapter;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shopbycategory.ShopByCategoryActivity;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
import com.smartwebarts.rogrows.utils.CustomSlider;
import com.smartwebarts.rogrows.utils.FragmentActivityMessage;
import com.smartwebarts.rogrows.utils.GlobalBus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private HomeViewModel homeViewModel;
    private SliderLayout home_list_banner;
    private RecyclerView recyclerView, recyclerViewBottom,recyclerViewTrand,recyclerViewbestseller;
    private Button shopbycategory;
    private List<ProductModel>trandinglist=new ArrayList<>();
    private TrendingNowAdapter trendingNowAdapter;
    private BestSellerAdapter bestSellerAdapter;
    private SwipeRefreshLayout swiperefresh;
    private LinearLayout Search_linearLayout;
    private List<ProductModel> trendingList = new ArrayList<>();
    private List<ProductModel> bestsellerlist = new ArrayList<>();
    public static HomeFragment homeFragment;
    private TopAdapter topAdapter;
    private BottomAdapter bottomAdapter;
    private WebView webView;
    public static HomeFragment getInstance() {
        return homeFragment;
    }
    private TextView deliverLocation,pincode;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        homeFragment = this;
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
//        pincode = view.findViewById(R.id.pincode);
        Search_linearLayout.setOnClickListener(HomeFragment::openSearchFragment);
        shopbycategory.setOnClickListener(this::shopbycategory);
        setTrendingRecycler(trandinglist);
        setBestSellerRecycler(bestsellerlist);
        if (UtilMethods.INSTANCE.isNetworkAvialable(requireActivity()))
        {
           /* UtilMethods.INSTANCE.getPincode(getActivity(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<List<PincodeModel>>(){}.getType();
                    List<PincodeModel> list = new Gson().fromJson(message, listType);
                    PincodeModel pincodeModel = list.get(0);

                    String showPin = String.format("%s", pincodeModel.getPin_code());
                    System.out.println("pin---from---getPincode()"+showPin);
                    pincode.setText(showPin);

                pincode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
                }

                @Override
                public void fail(String from) {
                }
            });*/




            UtilMethods.INSTANCE.categories(getActivity(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<CategoryModel>>(){}.getType();
                    List<CategoryModel> list = new Gson().fromJson(message, listType);
                    SharedPreferences sharedpreferences = requireActivity().getSharedPreferences(ApplicationConstants.INSTANCE.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(ApplicationConstants.INSTANCE.PRODUCT_LIST, message);
                    editor.apply();
                    setTopRecycler(list);
                    setBottomRecycler(list);
                }

                @Override
                public void fail(String from) {
                }
            });

            UtilMethods.INSTANCE.imageSlider(getActivity(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<SliderImageData>>(){}.getType();
                    List<SliderImageData> list = new Gson().fromJson(message, listType);
                    setSlider(list);
                }

                @Override
                public void fail(String from) {
                }
            });

           UtilMethods.INSTANCE.trending(requireActivity(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                    List<ProductModel> list = new Gson().fromJson(message, listType);
                    trendingNowAdapter.setList(list);
                }

                @Override
                public void fail(String from) {
                    Toast.makeText(requireActivity(), from, Toast.LENGTH_SHORT).show();

                }
            });

           UtilMethods.INSTANCE.bestseller(requireActivity(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                    List<ProductModel> list = new Gson().fromJson(message, listType);
                    bestSellerAdapter.setList(list);
                }

                @Override
                public void fail(String from) {
                    Toast.makeText(requireActivity(), from, Toast.LENGTH_SHORT).show();

                }
            });

        } else {

            UtilMethods.INSTANCE.internetNotAvailableMessage(getActivity());
        }

        homeViewModel.getText().observe(requireActivity(), s -> {
        });

        swiperefresh.setOnRefreshListener(() -> {
            trendingList = new ArrayList<>();
            bestsellerlist = new ArrayList<>();
            trendingNowAdapter.setList(trendingList);
            bestSellerAdapter.setList(bestsellerlist);
            UtilMethods.INSTANCE.trending(requireActivity(),new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                    trendingList = new Gson().fromJson(message, listType);
                    trendingNowAdapter.setList(trendingList);
                }

                @Override
                public void fail(String from) {
                    Toast.makeText(requireActivity(), from, Toast.LENGTH_SHORT).show();

                }
            });
            UtilMethods.INSTANCE.bestseller(requireActivity(),new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                    bestsellerlist = new Gson().fromJson(message, listType);
                    bestSellerAdapter.setList(bestsellerlist);
                    swiperefresh.setRefreshing(false);
                }

                @Override
                public void fail(String from) {
                    Toast.makeText(requireActivity(), from, Toast.LENGTH_SHORT).show();
                    swiperefresh.setRefreshing(false);
                }
            });

        });
        return view;
    }

    public static void openSearchFragment(View v) {
        if (homeFragment!=null) {
            Navigation.findNavController(homeFragment.Search_linearLayout).navigate(R.id.action_nav_home_to_searchFragment);


        }
    }

    private void setBottomRecycler(List<CategoryModel> list) {
        bottomAdapter = new BottomAdapter(requireActivity(), list);
        recyclerViewBottom.setAdapter(bottomAdapter);
    }

    private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerViewBottom = (RecyclerView) view.findViewById(R.id.recyclerViewBottom);
        recyclerViewTrand = (RecyclerView) view.findViewById(R.id.recyclerViewTrand);
        recyclerViewbestseller = (RecyclerView) view.findViewById(R.id.recyclerViewbestseller);
        home_list_banner =  (SliderLayout) view.findViewById(R.id.home_img_slider);
        shopbycategory =  (Button) view.findViewById(R.id.shopbycategory);
        Search_linearLayout =view.findViewById(R.id.search_layout);
        swiperefresh = view.findViewById(R.id.swiperefresh);
        webView = view.findViewById(R.id.news);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);


        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //setSliderNews();
            }
        });
    }

    private void setTopRecycler(List<CategoryModel> list) {
        topAdapter = new TopAdapter(requireActivity(), list);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));
        recyclerView.setAdapter(topAdapter);
    }

    private void setTrendingRecycler(List<ProductModel> trandinglist) {
        trendingNowAdapter = new TrendingNowAdapter(requireActivity(), trandinglist);
        recyclerViewTrand.setAdapter(trendingNowAdapter);
    }

    private void setBestSellerRecycler(List<ProductModel> bestsellerlist) {
        bestSellerAdapter = new BestSellerAdapter(requireActivity(), bestsellerlist);
        recyclerViewbestseller.setAdapter(bestSellerAdapter);
    }

    public void shopbycategory(View view) {
        startActivity(new Intent(requireActivity(), ShopByCategoryActivity.class));
    }

    private void setSlider(List<SliderImageData> list) {

        for (SliderImageData data : list) {
            CustomSlider textSliderView = new CustomSlider(requireActivity());
            // initialize a SliderLayout
            textSliderView
                    .image(ApplicationConstants.INSTANCE.OFFER_IMAGES + data.getImage())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            home_list_banner.addSlider(textSliderView);
        }

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeFragment = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeFragment = this;
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
        homeFragment = null;
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void onFragmentActivityMessage(FragmentActivityMessage obj) {
        if (obj.getFrom().equalsIgnoreCase("updatelist")) {
            trendingNowAdapter.notifyDataSetChanged();
            bestSellerAdapter.notifyDataSetChanged();
        }
    }

    private void setSliderNews() {

        if (MyApplication.getNews(requireActivity()).isEmpty()) {

            if (UtilMethods.INSTANCE.isNetworkAvialable(requireActivity())) {
                UtilMethods.INSTANCE.news(requireActivity(), new mCallBackResponse() {
                    @Override
                    public void success(String from, String message) {

                        Type type = new TypeToken<List<NewsModel>>(){}.getType();
                        List<NewsModel> newsModels = new Gson().fromJson(message, type);

                        StringBuilder messageBuilder = new StringBuilder();
                        for (NewsModel newsModel: newsModels) {
                            messageBuilder.append(newsModel.getNote());
                        }
                        message = messageBuilder.toString();

                        Log.e("TAG", "success: " +message );
                        setNews(message);
                    }

                    @Override
                    public void fail(String from) {
                        Toast.makeText(requireActivity(), from, Toast.LENGTH_SHORT).show();

                    }
                });
            }

        } else {
            setNews(MyApplication.getNews(requireActivity()));
        }
    }

    private void setNews(String message) {
        String summary = "<html><body><MARQUEE behavior='scroll' direction='left' scrollamount=5>"+message+"</MARQUEE></body></html>";
        webView.loadDataWithBaseURL(null, summary, "text/html", "utf-8", null);
    }
}