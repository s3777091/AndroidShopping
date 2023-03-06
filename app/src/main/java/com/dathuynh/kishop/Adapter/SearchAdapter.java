package com.dathuynh.kishop.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Activity.DetailActivity;
import com.dathuynh.kishop.Interface.ItemClickListener;
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<ProductDetail> listProduct;

    String email;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;


    public SearchAdapter(Context context, List<ProductDetail> listProduct, RetrofitAPI retrofitAPI, String email) {
        this.context = context;
        this.listProduct = listProduct;
        this.retrofitAPI = retrofitAPI;
        this.email = email;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            return new ProductHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_processing_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductHolder) {
            ProductHolder ProductHolder = (ProductHolder) holder;
            ProductDetail pr = listProduct.get(position);
            ProductHolder.pr_title.setText(pr.getTitle().trim()); // Title

            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            ProductHolder.pr_price.setText("Price: ".concat(
                    decimalFormat.format(Double.parseDouble(String.valueOf(pr.getPrice()))) + "đ")); // Price

            Glide.with(context).load(pr.getImage()).into(ProductHolder.pr_image); // Image


            ProductHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int pos, boolean isLongClick) {
                    if (!isLongClick) {
                        postDataSearch(pr.getTitle(), pr.getImage_adding(), pr.getSlug(), String.valueOf(pr.getPrice()));
                    }
                }
            });

        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    private void postDataSearch(String name, String image, String link, String price) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("productName", name);
        hashMap.put("productImage", image);
        hashMap.put("productLink", link);
        hashMap.put("price", price);

        compositeDisposable.add(retrofitAPI.PostSearch(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            if (data.isSuccess()) {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        CreateOrder(email, name);
                                        Toast.makeText(context, "Success add your order", Toast.LENGTH_LONG).show();
                                    }
                                }, 1500);
                            }
                        },
                        throwable -> {
                            try {
                                if (((HttpException) throwable).code() == 403) {
                                    CreateOrder(email, name);
                                }
                            } catch (Exception e) {
                                Log.e("Controller search", String.valueOf(e));
                            }
                        }

                ));
    }


    private void CreateOrder(String userMail, String pro_name) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("email", userMail);
        hashMap.put("pro", pro_name);

        compositeDisposable.add(retrofitAPI.PostProductByName(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            Toast.makeText(context, data.getStatus(), Toast.LENGTH_LONG).show();
                        },
                        throwable -> {
                            try {
                                if (((HttpException) throwable).code() == 200) {
                                    Toast.makeText(context, "Success create search order", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                Log.e("Controller search", String.valueOf(e));
                            }

                        }
                ));
    }

    @Override
    public int getItemViewType(int position) {
        return listProduct.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView pr_title, pr_price;
        ImageView pr_image;
        private ItemClickListener itemClickListener;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            pr_title = itemView.findViewById(R.id.itemdt_ten);
            pr_price = itemView.findViewById(R.id.itemdt_gia);
            pr_image = itemView.findViewById(R.id.itemdt_image);
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

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressbar);
        }
    }
}
