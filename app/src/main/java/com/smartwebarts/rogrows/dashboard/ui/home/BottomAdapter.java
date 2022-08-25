package com.smartwebarts.rogrows.dashboard.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.CategoryModel;
import com.smartwebarts.rogrows.models.HomeProductsModel;
import com.smartwebarts.rogrows.productlist.ProductListActivity;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
import com.smartwebarts.rogrows.utils.MyGlide;


public class BottomAdapter extends RecyclerView.Adapter<BottomAdapter.MyViewHolder> {

    private Context context;
    private List<CategoryModel> list;


    public BottomAdapter(Context context, List<CategoryModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_bottom_categories, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        MyGlide.with(context, ApplicationConstants.INSTANCE.CATEGORY_IMAGES+list.get(position).getImage(), holder.imageView);
        holder.categoryName.setText(list.get(position).getName());

        holder.tvViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductListActivity.class);
            intent.putExtra("category", list.get(position));
            context.startActivity(intent);
        });

        holder.imageView.setOnClickListener(v -> holder.tvViewAll.performClick());

        holder.categoryName.setOnClickListener(v -> holder.tvViewAll.performClick());

        UtilMethods.INSTANCE.homeProducts(context, list.get(position).getId(), new mCallBackResponse() {
            @Override
            public void success(String from, String message) {
                Type listType = new TypeToken<ArrayList<HomeProductsModel>>(){}.getType();
                List<HomeProductsModel> sublist = new Gson().fromJson(message, listType);
                setCategoryRecycler(holder.recyclerViewCategory,sublist, list.get(position));
            }
            @Override
            public void fail(String from) {
            }
        });
    }

    private void setCategoryRecycler(RecyclerView recyclerView, List<HomeProductsModel> list, CategoryModel categoryModel) {
        GridAdapter adapter = new GridAdapter(context,list, categoryModel);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView categoryName, tvViewAll;
        RecyclerView recyclerViewCategory, recyclerViewBanner;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.tvName);
            categoryName = (TextView) itemView.findViewById(R.id.categoryName);
            tvViewAll = (TextView) itemView.findViewById(R.id.tv_viewAll);
            recyclerViewCategory = (RecyclerView) itemView.findViewById(R.id.recyclerViewCategory);
            recyclerViewBanner = (RecyclerView) itemView.findViewById(R.id.recyclerViewBanner);
        }
    }
}
