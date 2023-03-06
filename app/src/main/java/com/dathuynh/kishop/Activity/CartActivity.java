package com.dathuynh.kishop.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dathuynh.kishop.Adapter.CartAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.EnventBus.TotallEvent;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    TextView totalMoney;
    Toolbar toolbar;
    RecyclerView recyclerView;

    AppCompatButton paymentGo;
    CartAdapter adapter;
    double stas = 0;
    List<OrderModel> carlist = new ArrayList<>();
    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    DBApp dbApp;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new ChangeBounds());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);
        dbApp = new DBApp(this);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                email = dbApp.getUserProfile().getUser_email();
                getData(email);
            }

        } catch (Exception e) {
            Toast.makeText(this, "Pls Login user", Toast.LENGTH_SHORT).show();
        }

        initView();
        initControl();
        totalMoney();
    }

    private void totalMoney() {
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        totalMoney.setText(decimalFormat.format(stas));
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        paymentGo.setOnClickListener(view -> {
            Intent payment = new Intent(getApplicationContext(), PaymentActivity.class);
            startActivity(payment,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toobar);

        totalMoney = findViewById(R.id.total_money);

        recyclerView = findViewById(R.id.recycle_view_cart);
        paymentGo = findViewById(R.id.payment);
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
                                carlist = results.getResult().stream().filter(s -> s.getStatus().equals("confirm")).collect(Collectors.toList());
                                adapter = new CartAdapter(this, carlist, retrofitAPI, email);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                results.getResult().forEach(e -> stas += Double.parseDouble(e.price) * Double.parseDouble(e.amount));
                            }
                        },
                        throwable -> Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
                ));
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void eventTinhTien(TotallEvent event) {
        if (event != null) {
            totalMoney();
        }
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}
