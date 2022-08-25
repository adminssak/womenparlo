package com.smartwebarts.rogrows.history;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwebarts.rogrows.R;
import com.smartwebarts.rogrows.models.OrderListModel;
import com.smartwebarts.rogrows.retrofit.UtilMethods;
import com.smartwebarts.rogrows.retrofit.mCallBackResponse;
import com.smartwebarts.rogrows.shared_preference.AppSharedPreferences;
import com.smartwebarts.rogrows.utils.MyGlide;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HistoryAdapter2 extends RecyclerView.Adapter<HistoryAdapter2.MyViewHolder> {
    private Context context;
    private HashMap<String, List<OrderListModel>> map;
    private List<String> list;

    public HistoryAdapter2(Context context, HashMap<String, List<OrderListModel>> map, List<String> list) {
        this.context = context;
        this.map = map;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.order_item_new, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.orderadte.setText(String.format("Order Date :\n%s", list.get(position).trim()));

            if (map != null && map.get(list.get(position)) != null && Objects.requireNonNull(map.get(list.get(position))).get(0) != null) {
                holder.paymentMethod.setText(String.format("Payment Method :%s", Objects.requireNonNull(map.get(list.get(position))).get(0).getPaymentmethod()));
                holder.orderID.setText(String.format("Order ID :\n%s", Objects.requireNonNull(map.get(list.get(position))).get(0).getOrderId()));
                holder.totalAmount.setText(String.format("Total Amount :%s", Objects.requireNonNull(map.get(list.get(position))).get(0).getTotalamount()));
                holder.userdatetime.setText(String.format("Reaching Date & Time :%s %s", Objects.requireNonNull(map.get(list.get(position))).get(0).getUserdate(), map.get(list.get(position)).get(0).getUsertime()));
                String status = Objects.requireNonNull(map.get(list.get(position))).get(0).getStatus();
                holder.status.setText(String.format("%s", status));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (status.equalsIgnoreCase(context.getString(R.string.accepted)) || status.equalsIgnoreCase("Recieved")) {
                        holder.status.setBackgroundColor(context.getColor(R.color.accepted));
                    } else if (status.equalsIgnoreCase(context.getString(R.string.completed))) {
                        holder.status.setBackgroundColor(context.getColor(R.color.completed));
                    } else if (status.equalsIgnoreCase(context.getString(R.string.delivered))) {
                        holder.status.setBackgroundColor(context.getColor(R.color.delivered));
                        holder.cancel.setText("Return");
                        MyGlide.with(context, context.getDrawable(R.drawable.return_product), holder.delete);
                    } else if (status.equalsIgnoreCase(context.getString(R.string.pending))) {
                        holder.status.setBackgroundColor(context.getColor(R.color.pending));
                    } else if (status.equalsIgnoreCase(context.getString(R.string.rejected))) {
                        holder.status.setBackgroundColor(context.getColor(R.color.cancelled));
                    } else if (status.equalsIgnoreCase(context.getString(R.string.returned))) {
                        holder.status.setBackgroundColor(context.getColor(R.color.returned));
                        holder.cancel.setVisibility(View.GONE);
                        holder.delete.setVisibility(View.GONE);
                    }
                }

            }

            holder.imghide.setOnClickListener(view -> {
                hide(Objects.requireNonNull(map.get(list.get(position))).get(0).getOrderId());
            });
        OrderDetailAdapter adapter = new OrderDetailAdapter(context, map.get(list.get(position)));
        holder.recyclerView.setAdapter(adapter);
        holder.delete.setOnClickListener(v -> showDialog(Objects.requireNonNull(map.get(list.get(position))).get(0).getOrderId(),
                Objects.requireNonNull(map.get(list.get(position))).get(0).getStatus()));
    }

    private void hide(String orderId) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to see this order!")
                .setConfirmText("Yes,Hide it!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        AppSharedPreferences preferences = new AppSharedPreferences(((MyHistoryActivity) context).getApplication());
                        String mobile = preferences.getLoginMobile();
                        UtilMethods.INSTANCE.hideorder(context, orderId, "", mobile, new mCallBackResponse() {
                            @Override
                            public void success(String from, String message) {
                                sDialog.dismissWithAnimation();
                                ((MyHistoryActivity) context).setOrderData(MyHistoryActivity.status);
                            }

                            @Override
                            public void fail(String from) {

                            }
                        });
                    }
                })
                .show();
    }

    private void showDialog(String orderListModel, String status) {

        SweetAlertDialog sad = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sad.setTitleText(status.equalsIgnoreCase(context.getString(R.string.delivered))?"Are you sure to return order":"Are you sure to cancel order?");
        sad.setContentText(status.equalsIgnoreCase(context.getString(R.string.delivered))?"Order will be returned":"Order will be cancelled");
        sad.setConfirmButton("OK", sweetAlertDialog -> {
            showReasonDialog(orderListModel, status);
            sad.dismiss();
        });
        sad.setCancelButton("Not Now", sweetAlertDialog -> sad.dismiss());
        sad.show();
    }

    private void showReasonDialog(String orderListModel, String status) {

        AlertDialog dialog = new AlertDialog.Builder(context).create();
        View dialogview = LayoutInflater.from(context).inflate(R.layout.dialog_request, null);
        Spinner spinReasons = dialogview.findViewById(R.id.spinReasons);
        AppCompatTextView txtCancel = dialogview.findViewById(R.id.txtCancel);
        EditText etReasons = dialogview.findViewById(R.id.etReasons);

        if (status.equalsIgnoreCase(context.getString(R.string.delivered))) {
            txtCancel.setText("I want to return order beacause");
        }

        spinReasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    etReasons.setText("");
                } else {
                    etReasons.setText(spinReasons.getSelectedItem().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog1, which) -> {
        });
        dialog.setView(dialogview);
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
            String reason = spinReasons.getSelectedItem().toString();
            if (reason.equalsIgnoreCase(context.getResources().getStringArray(R.array.reasonsforcancellation)[0])) {
                reason = etReasons.getText().toString();
                if (reason.isEmpty()) {
                    etReasons.setError(status.equalsIgnoreCase(context.getString(R.string.delivered))?"Enter a reason for returning": "Enter a reason for cancellation");
                } else {
                    cancelOrder(orderListModel, reason, status);
                }
            } else {
                cancelOrder(orderListModel, spinReasons.getSelectedItem().toString(), status);
            }

            dialog.dismiss();
        });
    }

    private void cancelOrder(String orderListModel, String reason, String status) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(context)) {

            AppSharedPreferences preferences = new AppSharedPreferences(((MyHistoryActivity) context).getApplication());
            String mobile = preferences.getLoginMobile();

            UtilMethods.INSTANCE.cancelOrder(context, orderListModel, reason, mobile, status, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    ((MyHistoryActivity) context).setOrderData(MyHistoryActivity.status);
                }

                @Override
                public void fail(String from) {

                }
            });

        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(context);
        }
    }


    @Override
    public int getItemCount() {
        return map!=null?map.size():0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView delete, imghide;
        private TextView paymentMethod, orderadte, orderID, totalAmount, userdatetime, status, cancel;
        private RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            delete = itemView.findViewById(R.id.delete);
            cancel = itemView.findViewById(R.id.cancel);
            imghide = itemView.findViewById(R.id.imghide);
            orderID = itemView.findViewById(R.id.orderId);
            paymentMethod = itemView.findViewById(R.id.paymentmethod);
            totalAmount = itemView.findViewById(R.id.totalamount);
            orderadte = itemView.findViewById(R.id.orderdate);
            status = itemView.findViewById(R.id.status);
            userdatetime = itemView.findViewById(R.id.userdatetime);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

    public void setList(HashMap<String, List<OrderListModel>> map) {
        this.map = map;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
