package com.smartwebarts.rogrows.dashboard.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.CategoryModel;
import com.smartwebarts.rogrows.models.HomeProductsModel;
import com.smartwebarts.rogrows.productlist.ProductDetailActivity;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
import com.smartwebarts.rogrows.utils.MyGlide;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private Context context;
    private List<HomeProductsModel> list;
    private CategoryModel categoryModel;

    public GridAdapter(Context context, List<HomeProductsModel> list) {
        this.context = context;
        this.list = list;
    }

    public GridAdapter(Context context, List<HomeProductsModel> list, CategoryModel categoryModel) {
        this.context = context;
        this.list = list;
        this.categoryModel = categoryModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_view3, parent, false));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {



//        try {
//            if (position == getItemCount()-1) {
//                holder.textView.setTextColor(Color.RED);
//                holder.textView.setText(context.getString(R.string.view_all));
//                holder.itemView.findViewById(R.id.rlmain).setBackground(null);
//                holder.itemView.findViewById(R.id.rlmain).setElevation(100);
//                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//                MyGlide.with(context, context.getDrawable(R.drawable.view_all_pigeon), holder.imageView);
//            } else {
//                MyGlide.with(context, ApplicationConstants.INSTANCE.PRODUCT_IMAGES + list.get(position).getThumbnail(), holder.imageView);
//                holder.textView.setText(list.get(position).getName());
//            }
//        } catch (Exception ignored){
//        }

        MyGlide.with(context, ApplicationConstants.INSTANCE.PRODUCT_IMAGES + list.get(position).getThumbnail(), holder.imageView);

        holder.textView.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(v -> {
//            if (position == getItemCount()-1){
//                Intent intent = new Intent(context, ProductListActivity.class);
//                intent.putExtra("category", categoryModel);
//                context.startActivity(intent);
//                return;
//            }
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.ID, list.get(position).getId());
            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return list!=null? Math.min(list.size(), 6) :0;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.tvName);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}

