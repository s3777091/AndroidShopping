package com.dathuynh.kishop.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Interface.ImageClickListenner;
import com.dathuynh.kishop.Model.CartModel;
import com.dathuynh.kishop.Model.EnventBus.TotallEvent;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.Tool.AppTool;
import com.dathuynh.kishop.retrofit.RetrofitAPI;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewholder> {

    Context context;
    List<OrderModel> cartList;
    RetrofitAPI retrofitAPI;
    String userMail;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CartAdapter(Context context, List<OrderModel> cartList, RetrofitAPI retrofitAPI, String user) {
        this.context = context;
        this.cartList = cartList;
        this.retrofitAPI = retrofitAPI;
        this.userMail = user;
    }

    @NonNull
    @Override
    public CartViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_adapter, parent, false);
        return new CartViewholder(view);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull CartViewholder holder, int position) {
        OrderModel carts = cartList.get(position);
        holder.itemTitleProduct.setText(carts.getProductName().trim());
        Glide.with(context).load(carts.getProductImage()).into(holder.itemImageProduct);

        holder.itemAmountProduct.setText(carts.getAmount() + "");
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.itemPriceProduct.setText(decimalFormat.format((Double.parseDouble(carts.getPrice()))) + "đ");

        holder.setListenner((view, pos, value) -> {
            // Remove Product Amount
            if (value == 1) {
                double amount = Double.parseDouble(cartList.get(pos).getAmount());
                //Amount have more than 1 product
                if (amount > 1.0) {
                    String pos_name = cartList.get(pos).getProductName();
                    double new_amount = amount - 1;
                    UpdateCart(userMail, pos_name, "-");

                    cartList.get(pos).setAmount(String.valueOf(new_amount));
                    holder.itemAmountProduct.setText(cartList.get(pos).getAmount() + "");

                    EventBus.getDefault().postSticky(new TotallEvent());


                } else if (amount == 1) {
                    //If Cart only 1 product show dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getRootView().getContext());
                    builder.setTitle("Notification");
                    builder.setMessage("Do you want to delete this product");

                    builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                        String pos_name = cartList.get(pos).getProductName();
                        cartList.remove(cartList.get(pos)); // remove from cartList
                        deleteCart(userMail, pos_name);
                        EventBus.getDefault().postSticky(new TotallEvent());
                    });

                    builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                    builder.show();
                }
            } else if (value == 2) {
                String pos_name = cartList.get(pos).getProductName();
                double amount = Double.parseDouble(cartList.get(pos).getAmount());
                double new_amount = amount + 1;

                UpdateCart(userMail, pos_name, "+");
                cartList.get(pos).setAmount(String.valueOf(new_amount));
                holder.itemAmountProduct.setText(cartList.get(pos).getAmount() + "");
                EventBus.getDefault().postSticky(new TotallEvent());
            }
        });
    }

    private void deleteCart(String userMail, String productName) {
        HashMap<String, String> st = new HashMap<>();
        st.put("email", userMail);
        st.put("product_name", productName);
        compositeDisposable.add(retrofitAPI.deletePayment(st)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            Log.e("Delete_cart", "Success delete cart");
                        },
                        throwable -> {
                            Log.e("Delete_cart", "Fail delete");
                        }
                ));
    }

    private void UpdateCart(String userMail, String productName, String control) {
        HashMap<String, String> st = new HashMap<>();
        st.put("mail", userMail);
        st.put("product", productName);
        st.put("controll", control);
        compositeDisposable.add(retrofitAPI.updatePayment(st)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            Log.e("Update_cart", "Success update cart");
                        },
                        throwable -> {
                            Log.e("Update_cart", "Fail update");
                        }
                ));
    }


    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView itemImageProduct;

        ImageButton itemMinusProduct, itemAddProduct;

        TextView itemTitleProduct, itemPriceProduct, itemAmountProduct;
        ImageClickListenner listenner;

        public CartViewholder(@NonNull View itemView) {
            super(itemView);

            itemTitleProduct = itemView.findViewById(R.id.items_title_product);
            itemImageProduct = itemView.findViewById(R.id.items_image_product);
            itemPriceProduct = itemView.findViewById(R.id.items_price_product);
            itemAmountProduct = itemView.findViewById(R.id.items_amount_product);

            itemMinusProduct = itemView.findViewById(R.id.items_minus_product);
            itemMinusProduct.setOnClickListener(this);
            itemAddProduct = itemView.findViewById(R.id.items_add_product);
            itemAddProduct.setOnClickListener(this);
        }

        public void setListenner(ImageClickListenner listenner) {
            this.listenner = listenner;
        }

        @Override
        public void onClick(View view) {
            if (itemMinusProduct.equals(view)) {
                listenner.onImageClick(view, getAdapterPosition(), 1); // 1 for negative
            } else if (itemAddProduct.equals(view)) {
                listenner.onImageClick(view, getAdapterPosition(), 2); // 2 là plus
            }
        }
    }


}
