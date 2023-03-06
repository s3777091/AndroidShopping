package com.dathuynh.kishop.Activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity {

    String userMail;
    DBApp dbApp;
    TextView pr_name, pr_product;
    ImageView pr_image;

    AppCompatButton addProductBtn;


    Spinner spinner;
    Toolbar toolbar;

    ProductDetail newsProduct;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<OrderModel> cartList = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new ChangeBounds());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);


        dbApp = new DBApp(this);
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);


        try {
            userMail = dbApp.getUserProfile().getUser_email();
        } catch (Exception e) {
            Toast.makeText(this, "Pls Login user", Toast.LENGTH_SHORT).show();
        }

        initView();
        ActionToolBar();
        initData();
        initControl();

    }

    private void initControl() {
        addProductBtn.setOnClickListener(view -> addToCart());
    }


    private void addToCart() {
        int amount = Integer.parseInt(spinner.getSelectedItem().toString());
        if (cartList.size() > 0) {
            boolean flag = false;
            if (!flag) {
                postToCart(newsProduct.getId(), String.valueOf(amount));
            }
        } else {
            postToCart(newsProduct.getId(), String.valueOf(amount));
        }

    }

    private void postToCart(long id, String amount) {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", userMail);
        data.put("product_id", String.valueOf(id));
        data.put("amount", amount);
        compositeDisposable.add(retrofitAPI.postToCartApi(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            Toast.makeText(this, results.getStatus(), Toast.LENGTH_LONG).show();
                        },
                        throwable -> {
                            Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @SuppressLint("SetTextI18n")
    private void ViewProductDetail(long id) {
        compositeDisposable.add(retrofitAPI.viewProductDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        detail -> {
                            newsProduct = detail.getResults().get(0);
                            pr_name.setText(newsProduct.getTitle().trim());
                            Glide.with(this.getApplicationContext()).load(newsProduct.getImage()).into(pr_image);
                            DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                            pr_product.setText("Price: " + decimalFormat.format(Double.parseDouble(String.valueOf(newsProduct.getPrice()))) + "đ");
                            Integer[] so = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                            ArrayAdapter<Integer> adapter_spin = new ArrayAdapter<>(this.getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, so);
                            spinner.setAdapter(adapter_spin);
                        },
                        throwable -> {
                            Log.e("Error Log Api", throwable.toString());
                            Toast.makeText(this.getApplicationContext(), throwable.toString(), Toast.LENGTH_LONG).show();
                        }
                ));

    }

    private void initData() {
        long st = (long) getIntent().getSerializableExtra("detail");
        ViewProductDetail(st);
    }

    private void initView() {
        pr_name = findViewById(R.id.name_product);

        pr_product = findViewById(R.id.price_product);
        pr_image = findViewById(R.id.image_product);
        spinner = findViewById(R.id.spinner);
        toolbar = findViewById(R.id.toobar);
        addProductBtn = findViewById(R.id.add_new_product);

        FrameLayout frameCart = findViewById(R.id.frame_cart);
        frameCart.setOnClickListener(view -> {
            Intent cartActity = new Intent(this, CartActivity.class);
            startActivity(cartActity,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

    }

    private void ActionToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
