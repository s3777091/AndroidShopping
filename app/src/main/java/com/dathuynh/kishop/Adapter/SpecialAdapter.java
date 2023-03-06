package com.dathuynh.kishop.Adapter;

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
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;

import java.text.DecimalFormat;
import java.util.List;

public class SpecialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<ProductDetail> listProduct;

    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_LOADING = 1;


    public SpecialAdapter(Context context, List<ProductDetail> listProduct) {
        this.context = context;
        this.listProduct = listProduct;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATA){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_main, parent, false);
            return new SpecialProductHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_processing_bar, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public int getItemCount() {
        return listProduct == null ? 0 : listProduct.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listProduct.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SpecialProductHolder){
            SpecialProductHolder specialProductHolder = (SpecialProductHolder) holder;
            ProductDetail pr = listProduct.get(position);
            specialProductHolder.pr_title.setText(pr.getTitle().trim()); // Title

            Glide.with(context).load(pr.getImage()).into(specialProductHolder.pr_image); // Image

            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
            specialProductHolder.pr_price.setText("Price: ".concat(
                    decimalFormat.format(Double.parseDouble(String.valueOf(pr.getPrice())))+ " đ")); // Price

            specialProductHolder.setItemClickListener((view, pos, isLongClick) -> {
                if(!isLongClick){

                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("detail", pr.id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });


        } else {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


    public static class SpecialProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView pr_title;
        TextView pr_price;
        ImageView pr_image;
        private ItemClickListener itemClickListener;

        public SpecialProductHolder(@NonNull View itemView) {
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
