package com.dathuynh.kishop.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Activity.DetailActivity;
import com.dathuynh.kishop.Interface.ItemClickListener;
import com.dathuynh.kishop.Model.Product;
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;

import java.util.List;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<ProductDetail> listProduct;


    public RecommendAdapter(Context context, List<ProductDetail> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_main, parent, false);
        return new MainProductHolder(view);
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MainProductHolder){
            MainProductHolder MainProductHolder = (MainProductHolder) holder;
            ProductDetail pr = listProduct.get(position);


            MainProductHolder.pr_title.setText(pr.getTitle().trim()); // Title
            MainProductHolder.pr_price.setText(String.valueOf(pr.getPrice()));
            Glide.with(context).load(pr.image).into(MainProductHolder.pr_image); // Image

            MainProductHolder.setItemClickListener((view, pos, isLongClick) -> {
                if(!isLongClick){
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("detail", pr.id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

        }else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return listProduct.size();
    }

    public static class MainProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView pr_title, pr_price;
        ImageView pr_image;
        private ItemClickListener itemClickListener;

        public MainProductHolder(@NonNull View itemView) {
            super(itemView);

            pr_title = itemView.findViewById(R.id.main_pr_title);
            pr_image = itemView.findViewById(R.id.main_pr_image);
            pr_price = itemView.findViewById(R.id.main_pr_price);
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


}
