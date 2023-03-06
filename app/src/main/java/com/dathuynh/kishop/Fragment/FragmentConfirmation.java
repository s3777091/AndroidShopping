package com.dathuynh.kishop.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dathuynh.kishop.Adapter.OrderAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentConfirmation extends Fragment {

    TextView userName, userAddress, totalOrder;


    double stas = 0;

    RecyclerView recycleViewCart;
    Context context;
    DBApp dbApp;


    List<OrderModel> OrderList;
    OrderAdapter mAdapter;


    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    public FragmentConfirmation() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_confirm, container, false);

        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);


        userName = root.findViewById(R.id.user_name_confirm);
        userAddress = root.findViewById(R.id.user_address_confirm);
        recycleViewCart = root.findViewById(R.id.recycle_view_cart_list);
        totalOrder = root.findViewById(R.id.total_all_order);


        context = this.getContext();

        dbApp = new DBApp(context);

        try {
            UserModel user = dbApp.getUserProfile();
            getUserProfile(user.getUser_email());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getData(user.getUser_email());
            }


        } catch (Exception e) {
            Toast.makeText(context, "pls login", Toast.LENGTH_SHORT).show();
        }

        recycleViewCart = root.findViewById(R.id.recycle_view_cart_list);

        recycleViewCart.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recycleViewCart.setLayoutManager(layoutManager);


        return root;
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    private void getUserProfile(String email) {
        compositeDisposable.add(retrofitAPI.getUserProfile(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                userName.setText(results.getUsername());
                                userAddress.setText(results.getAddress());
                            }
                        },
                        throwable -> Toast.makeText(context, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getData(String email) {
        compositeDisposable.add(retrofitAPI.getViewPayment(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                OrderList = results.getResult().stream().filter(s -> s.getStatus().equals("confirm")).collect(Collectors.toList());
                                OrderList.forEach(e -> stas += Double.parseDouble(e.price) * Double.parseDouble(e.amount));
                                if (!OrderList.isEmpty()) {
                                    mAdapter = new OrderAdapter(context.getApplicationContext(),
                                            OrderList, retrofitAPI, compositeDisposable, dbApp);
                                    recycleViewCart.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    OrderModel st = new OrderModel();
                                    st.setProductName("Empty product");
                                    OrderList.add(st);
                                }

                                totalOrder.setText(String.valueOf(stas));
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show()
                ));
    }


}
