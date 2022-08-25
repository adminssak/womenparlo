package com.smartwebarts.rogrows.history;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.OrderListModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.MyGlide;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.util.Comparator.comparing;

public class MyHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ImageView noitem;
    public static String status = "Recieved";
    Spinner spinStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_history);

        Toolbar_Set.INSTANCE.setToolbar(this, "My Orders");
        Toolbar_Set.INSTANCE.setBottomNav(this);

        recyclerView = findViewById(R.id.recyclerView);
        spinStatus = findViewById(R.id.spinStatus);
        noitem = findViewById(R.id.imageView);

        spinStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                status = spinStatus.getSelectedItem().toString();
                setOrderData(status);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void setOrderData(String status) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

            AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
            String userid = preferences.getLoginUserLoginId();

            if (userid!=null && !userid.isEmpty()) {

                UtilMethods.INSTANCE.orderhistory(this, userid, status, new mCallBackResponse() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void success(String from, String message) {
                        Type type = new TypeToken<List<OrderListModel>>(){}.getType();
                        List<OrderListModel> list = new Gson().fromJson(message, type);

                        if (list.size() <= 0) {
                            recyclerView.setVisibility(View.GONE);
                            noitem.setVisibility(View.VISIBLE);
                            MyGlide.with(MyHistoryActivity.this, getDrawable(R.drawable.noitem), noitem);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            noitem.setVisibility(View.GONE);
                        }

                        parseList(list);
                    }

                    @Override
                    public void fail(String from) {
                        recyclerView.setVisibility(View.GONE);
                        noitem.setVisibility(View.VISIBLE);
                    }
                });

            }

        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void parseList(List<OrderListModel> list) {

        if (list!=null) {

            HashSet<String> orderid = new HashSet<>();
            HashSet<String> orderdate = new HashSet<>();
            HashMap<String, List<OrderListModel>> map = new HashMap<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Collections.sort(list, comparing(OrderListModel::getOrderdate));
            }

            for (OrderListModel o: list) {
                orderid.add(o.getOrderId());
                orderdate.add(o.getOrderdate());
            }
            Log.e("TOTAL ORDER IDS", orderid.size()+"");

            for (int i=0; i<orderdate.size(); i++) {
                List<OrderListModel> list1 = new ArrayList<>();
                for (OrderListModel o1 : list) {
                    if (o1.getOrderdate().equalsIgnoreCase(orderdate.toArray()[i].toString())) {

                        boolean isAdd = true;
                        for(OrderListModel o2 : list1){
                            if (o2.getProductId().equalsIgnoreCase(o1.getProductId())) {
                                isAdd = false;
                                break;
                            }
                        }
                        if (isAdd) {
                            list1.add(o1);
                        }
                    }
                }
                map.put(orderdate.toArray()[i].toString(), list1);
            }

            System.out.println(map);

            List<String> orderDateList = new ArrayList<>(orderdate);
            Collections.sort(orderDateList);
            Collections.reverse(orderDateList);

            HistoryAdapter2 adapter = new HistoryAdapter2(this, map, orderDateList);
            recyclerView.setAdapter(adapter);

            recyclerView.setVisibility(View.VISIBLE);
            noitem.setVisibility(View.GONE);

        }
        else {
            recyclerView.setVisibility(View.GONE);
            noitem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }
}