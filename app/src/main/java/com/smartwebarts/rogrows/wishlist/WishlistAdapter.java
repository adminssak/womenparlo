package com.smartwebarts.rogrows.wishlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.database.DatabaseClient;
import com.smartwebarts.rogrows.database.SaveProductList;
import com.smartwebarts.rogrows.database.Task;
import com.smartwebarts.rogrows.database.WishItem;
import com.smartwebarts.rogrows.models.AddWishListResponse;
import com.smartwebarts.rogrows.models.ProductModel;
import com.smartwebarts.rogrows.productlist.ProductDetailActivity;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.ApplicationConstants;
import com.smartwebarts.rogrows.utils.FragmentActivityMessage;
import com.smartwebarts.rogrows.utils.GlobalBus;
import com.smartwebarts.rogrows.utils.MyGlide;
import com.smartwebarts.rogrows.utils.Toolbar_Set;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder>{

    private Context context;
    private List<ProductModel> list;

    public WishlistAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WishlistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishlistAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.grid_item_wishlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.MyViewHolder holder, final int position) {

        try {

            MyGlide.with(context,ApplicationConstants.INSTANCE.PRODUCT_IMAGES + list.get(position).getThumbnail(),holder.prodImage);

            holder.txt_pName.setText(list.get(position).getName().trim());
            holder.txt_pInfo.setText(list.get(position).getDescription().trim());
            holder.txt_unit.setText(list.get(position).getUnit().trim() + list.get(position).getUnitIn().trim());
            holder.currentprice.setText( list.get(position).getCurrentprice().trim());
            holder.addToWishList.setImageDrawable(
                    context.getDrawable(list.get(position).isWishlist()?
                            R.drawable.ic_heart_red:
                            R.drawable.ic_heart_black));

            if (list.get(position).getQuantity()!=null && Integer.parseInt("0" + list.get(position).getQuantity())>0) {
                holder.txt_add.setText(context.getString(R.string.add));
                holder.ll_Add.setEnabled(true);
            } else {
                holder.txt_add.setText(context.getString(R.string.out_of_stock));
                holder.txt_add.setPadding(0,0,0,0);
                holder.txt_add.setTextSize(12);
                holder.ll_Add.setEnabled(false);
            }
        } catch ( Exception ignored){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ID, list.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_add;
        ImageView prodImage;
        ImageView addToWishList;
        ImageView deleteall;
        TextView txt_pName, txt_pInfo, txt_unit, currentprice, price, txt_discountOff, txtQuan;
        RelativeLayout rlQuan;
        LinearLayout ll_addQuan, ll_Add;
        TextView plus, minus;
        int minteger = 0;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            prodImage = itemView.findViewById(R.id.prodImage);
            addToWishList = itemView.findViewById(R.id.addToWishList);
            deleteall = itemView.findViewById(R.id.deleteall);

            txt_pName = itemView.findViewById(R.id.txt_pName);
            txt_pInfo = itemView.findViewById(R.id.txt_pInfo);
            txt_unit = itemView.findViewById(R.id.txt_current_price);
            txt_add = itemView.findViewById(R.id.txt_add);
            currentprice = itemView.findViewById(R.id.currentprice);
            price = itemView.findViewById(R.id.txt_price);
            txt_discountOff = itemView.findViewById(R.id.txt_discountOff);

            rlQuan =  itemView.findViewById(R.id.rlQuan);
            ll_Add =  itemView.findViewById(R.id.ll_Add);
            ll_addQuan =  itemView.findViewById(R.id.ll_addQuan);
            txtQuan =  itemView.findViewById(R.id.txtQuan);
            minus =  itemView.findViewById(R.id.minus);
            plus =  itemView.findViewById(R.id.plus);

            ll_Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_Add.setVisibility(View.GONE);
                    ll_addQuan.setVisibility(View.VISIBLE);
                    txtQuan.setText("1");
                    MyViewHolder.this.addToBag("1");
                }
            });
            deleteall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppSharedPreferences preferences = null;

                    if (context instanceof WishListActivity)
                        preferences = new AppSharedPreferences(((WishListActivity) context).getApplication());


                    if (preferences != null) {
                        if (!preferences.getLoginUserLoginId().isEmpty()) {

                            if (UtilMethods.INSTANCE.isNetworkAvialable(context)) {

                                UtilMethods.INSTANCE.removewishlist(context, list.get(WishlistAdapter.MyViewHolder.this.getAdapterPosition()).getId(),
                                        preferences.getLoginUserLoginId(),"",""
                                        , new mCallBackResponse() {
                                            @Override
                                            public void success(String from, String message) {
                                                AddWishListResponse response = new Gson().fromJson(message, AddWishListResponse.class);
                                                if (response != null && response.getMessage() != null && response.getMessage().equalsIgnoreCase("success")) {
                                                    ProductModel temp = list.get(WishlistAdapter.MyViewHolder.this.getAdapterPosition());
                                                    list.remove(temp);
                                                    notifyDataSetChanged();
                                                }
                                            }

                                            @Override
                                            public void fail(String from) {

                                            }
                                        });
                            } else {
                                UtilMethods.INSTANCE.internetNotAvailableMessage(context);
                            }

                        } else {
                            Toast.makeText(context, "Login to create a wishlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            plus.setOnClickListener(v -> MyViewHolder.this.increaseInteger());

            minus.setOnClickListener(v1 -> MyViewHolder.this.decreaseInteger());
        }

        public void increaseInteger() {
            minteger = minteger + 1;
            display(minteger);
        }

        public void decreaseInteger() {
            minteger = minteger - 1;
            if (minteger <= 0) {
                minteger = 0;
                display(minteger);
                ll_addQuan.setVisibility(View.GONE);
                ll_Add.setVisibility(View.VISIBLE);
                delete(context, list.get(getAdapterPosition()));
            } else {
                display(minteger);
            }
        }

        private void display(Integer number) {
            txtQuan.setText("" + number);
            addToBag(""+number);
        }



        public void addToBag(String quantity) {

            int qty = Integer.parseInt("0"+quantity.trim());
            int price = Integer.parseInt(list.get(getAdapterPosition()).getCurrentprice().trim());
            int finalPrice = qty*price;

            List<Task> items = new ArrayList<>();
            Task task = new Task(list.get(getAdapterPosition()), quantity, ""+finalPrice);
            items.add(task);
            new SaveProductList(context,items).execute();
            Toolbar_Set.INSTANCE.getCartList((Activity) context);
        }

    }

    public static void delete(final Context context, final ProductModel task) {

        class GetTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

            @Override
            protected ArrayList<Task> doInBackground(Void... voids) {
                DatabaseClient.getmInstance(context.getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .deleteWithID(task.getId());
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Task> tasks) {
                Toolbar_Set.INSTANCE.getCartList((Activity) context);

                FragmentActivityMessage activityActivityMessage =
                        new FragmentActivityMessage(new Gson().toJson(task), "updatelist");
                GlobalBus.getBus().post(activityActivityMessage);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    public  void delete(final Activity activity, final WishItem task) {

        class GetTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

            @Override
            protected ArrayList<Task> doInBackground(Void... voids) {
                DatabaseClient.getmInstance(activity.getApplicationContext())
                        .getAppDatabase()
                        .wishDao()
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

    public void setList(List<ProductModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
