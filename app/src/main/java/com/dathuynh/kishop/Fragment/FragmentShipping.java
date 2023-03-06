package com.dathuynh.kishop.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Adapter.OrderAdapter;
import com.dathuynh.kishop.Adapter.RecommendAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.EnventBus.TotallEvent;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentShipping extends Fragment {



    EditText ShippingName, ShippingEmail, ShippingAddress, ShippingPhone;
    Context context;
    DBApp dbApp;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();



    public FragmentShipping() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shipping, container, false);
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);

        context = this.getContext();
        dbApp = new DBApp(context);

        UserModel userModel = dbApp.getUserProfile();
        ShippingName = root.findViewById(R.id.shipping_name);
        ShippingEmail = root.findViewById(R.id.shipping_email);
        ShippingAddress = root.findViewById(R.id.shipping_address);
        ShippingPhone = root.findViewById(R.id.shipping_phone);

        try {
            getUserProfile(userModel.getUser_email());
        } catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "Pls login", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void getUserProfile(String email){
        compositeDisposable.add(retrofitAPI.getUserProfile(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){

                                if (Objects.equals(results.getAddress(), "Nothing Here")){
                                    ShippingAddress.setText("Pls update new address");
                                }

                                ShippingName.setText(results.getUsername());
                                ShippingEmail.setText(results.getEmail());
                                ShippingPhone.setText(results.getPhone());
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}