package com.dathuynh.kishop.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
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

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class FragmentOrder extends Fragment {

    Context context;
    List<OrderModel> OrderList;
    OrderAdapter mAdapter;
    RecyclerView recyclerViewOrder;
    RelativeLayout rsl;
    Toolbar toolbar;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DBApp dbApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public FragmentOrder() {
    }

    private void initToolbar(View view) {
        toolbar = view.findViewById(R.id.toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        context = this.getContext();
        dbApp = new DBApp(context);

        initToolbar(view);

        recyclerViewOrder = view.findViewById(R.id.recycler_view_order);

        recyclerViewOrder.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewOrder.setLayoutManager(layoutManager);

        rsl = view.findViewById(R.id.relative_cart);


        if(isConnected(context)){
            try {
                UserModel user = dbApp.getUserProfile();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    getData(user.getUser_email());
                }
            } catch (Exception e) {
                Toast.makeText(context, "pls login", Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getData(String email) {
        compositeDisposable.add(retrofitAPI.getViewPayment(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                if (results.getResult().removeIf(item -> item.status.equals("confirm"))) {
                                    OrderList = results.getResult();
                                    if (!OrderList.isEmpty()) {
                                        mAdapter = new OrderAdapter(context.getApplicationContext(),
                                                OrderList, retrofitAPI, compositeDisposable, dbApp);
                                        recyclerViewOrder.setAdapter(mAdapter);
                                    } else {
                                        OrderModel st = new OrderModel();
                                        st.setProductName("Empty product");
                                        OrderList.add(st);
                                    }
                                } else {
                                    OrderList = results.getResult();
                                    if (!OrderList.isEmpty()) {
                                        mAdapter = new OrderAdapter(context.getApplicationContext(),
                                                OrderList, retrofitAPI, compositeDisposable, dbApp);
                                        recyclerViewOrder.setAdapter(mAdapter);
                                    } else {
                                        OrderModel st = new OrderModel();
                                        st.setProductName("Empty product");
                                        OrderList.add(st);
                                    }
                                }
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show()
                ));
    }

    private boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return wifi != null && wifi.isConnected() || (mobile != null && mobile.isConnected());
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
