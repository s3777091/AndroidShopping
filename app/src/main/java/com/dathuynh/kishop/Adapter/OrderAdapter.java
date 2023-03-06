package com.dathuynh.kishop.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Activity.DetailActivity;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Interface.ItemClickListener;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<OrderModel> orderList;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable;

    DBApp dbApp;

    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    public OrderAdapter(Context context,
                        List<OrderModel> orderList,
                        RetrofitAPI retrofitAPI,
                        CompositeDisposable compositeDisposable,
                        DBApp dbApp
    ) {
        this.context = context;
        this.orderList = orderList;
        this.retrofitAPI = retrofitAPI;
        this.compositeDisposable = compositeDisposable;
        this.dbApp = dbApp;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_order_adapter, parent, false);
            return new OrderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_processing_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return orderList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof OrderViewHolder){

            OrderViewHolder sts = (OrderViewHolder) holder;
            OrderModel pr = orderList.get(position);
            sts.pr_title.setText(pr.getProductName().trim()); // Title

            sts.pr_type.setText(pr.getType());
            sts.pr_amount.setText(pr.getAmount());

            Glide.with(context).load(pr.getProductImage()).into(sts.pr_image); // Image

            sts.remotePayment.setOnClickListener(es -> {
                GetDataTypePhone(pr.getProductName());
                orderList.remove(position);
                notifyItemRemoved(position);
            });

            sts.setItemClickListener((view, pos, isLongClick) -> {
                if(!isLongClick){

                }
            });


        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageButton remotePayment;
        TextView pr_title, pr_type, pr_amount;
        /*        TextView pr_price;*/
        ImageView pr_image;
        private ItemClickListener itemClickListener;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            pr_title = itemView.findViewById(R.id.order_product_name);
            pr_image = itemView.findViewById(R.id.order_image_product);
            pr_type = itemView.findViewById(R.id.order_type_product);
            pr_amount = itemView.findViewById(R.id.order_amount_product);
            remotePayment = itemView.findViewById(R.id.items_remove);

            itemView.setOnClickListener(this);

        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getAdapterPosition(), false);
        }


    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }





    private void GetDataTypePhone(String name){
        UserModel st = dbApp.getUserProfile();
        try {
            HashMap<String, String> deleteType = new HashMap<>();
            deleteType.put("email", st.getUser_email());
            deleteType.put("product_name", name);
            compositeDisposable.add(retrofitAPI.deletePayment(deleteType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            results -> {
                                if(results.isSuccess()){
                                    Toast.makeText(context.getApplicationContext(), results.getStatus(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), "SomeThing went wrong", Toast.LENGTH_SHORT).show();
                                }
                            },
                            throwable -> Toast.makeText(context.getApplicationContext(), "Can't connect to sever", Toast.LENGTH_LONG).show()
                    ));

        } catch (Exception ex){
            //some code to handle your exceptions
            Toast.makeText(context.getApplicationContext(), "Pls login your account", Toast.LENGTH_SHORT).show();
        }
    }


}
