package com.dathuynh.kishop.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dathuynh.kishop.Adapter.OrderAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Fragment.FragmentConfirmation;
import com.dathuynh.kishop.Fragment.FragmentPayment;
import com.dathuynh.kishop.Fragment.FragmentShipping;
import com.dathuynh.kishop.MainActivity;
import com.dathuynh.kishop.Model.OrderModel;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.Tool.AppTool;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PaymentActivity extends AppCompatActivity {

    String userName, userMail, userPhone, userAddress;

    double stas = 0;

    private enum State {
        SHIPPING,
        PAYMENT,
        CONFIRMATION,
        CHECKOUT
    }

    State[] array_state = new State[]{State.SHIPPING, State.PAYMENT, State.CONFIRMATION, State.CHECKOUT};

    private View line_first, line_second;
    private ImageView image_shipping, image_payment, image_confirm;
    private TextView tv_shipping, tv_payment, tv_confirm;

    private int idx_state = 0;

    DBApp dbApp;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        initToolbar();
        initComponent();
        displayFragment(State.SHIPPING);
    }


    private void initComponent() {
        line_first = findViewById(R.id.line_first);
        line_second = findViewById(R.id.line_second);
        image_shipping = findViewById(R.id.image_shipping);
        image_payment = findViewById(R.id.image_payment);
        image_confirm = findViewById(R.id.image_confirm);

        tv_shipping = findViewById(R.id.tv_shipping);
        tv_payment = findViewById(R.id.tv_payment);
        tv_confirm = findViewById(R.id.tv_confirm);


        dbApp = new DBApp(this);
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);

        image_payment.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);
        image_confirm.setColorFilter(getResources().getColor(R.color.grey_10), PorterDuff.Mode.SRC_ATOP);

        try {
            UserModel user = dbApp.getUserProfile();
            userMail = user.getUser_email();
            getUserProfile(userMail);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getData(userMail);
            }

        } catch (Exception e) {
            Toast.makeText(this, "pls login", Toast.LENGTH_SHORT).show();
        }

        (findViewById(R.id.lyt_next)).setOnClickListener(v -> {
            if (idx_state == array_state.length - 1) return;
            idx_state++;
            if (array_state[idx_state] == State.CHECKOUT) {
                if(Objects.equals(userAddress, "Nothing Here") || Objects.equals(userPhone, "84")){
                    Toast.makeText(this,"Pls update user profile",Toast.LENGTH_SHORT).show();
                    Intent profile = new Intent(this, ProfileActivity.class);
                    startActivity(profile,
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                } else {
                    CheckOut(userPhone, userMail, userAddress, stas);
                    finish();
                }
            }

            displayFragment(array_state[idx_state]);
        });

        (findViewById(R.id.lyt_previous)).setOnClickListener(v -> {
            if (idx_state < 1) return;
            idx_state--;
            displayFragment(array_state[idx_state]);
        });
    }

    private void CheckOut(String phone, String mail, String address, double total) {
        HashMap<String, String> st = new HashMap<>();
        st.put("phoneNumber", phone);
        st.put("usermail", mail);
        st.put("address", address);
        st.put("total", String.valueOf(total));

        compositeDisposable.add(retrofitAPI.checkOut(st)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            Toast.makeText(this, "Success order Product", Toast.LENGTH_LONG).show();
                        },
                        throwable -> Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
                ));
    }

    private void getUserProfile(String email) {
        compositeDisposable.add(retrofitAPI.getUserProfile(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                userName = results.getUsername();
                                userPhone = results.getPhone();
                                userAddress = results.getAddress();
                            }
                        },
                        throwable -> Toast.makeText(this, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getData(String email) {
        compositeDisposable.add(retrofitAPI.getViewPayment(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                if (!results.getResult().removeIf(item -> item.status.equals("On-going"))) {
                                    results.getResult().forEach(e -> {
                                        stas += Double.parseDouble(e.price) * Double.parseDouble(e.amount);
                                    });
                                }
                            }
                        },
                        throwable -> Toast.makeText(this, throwable.toString(), Toast.LENGTH_LONG).show()
                ));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
//        AppTool.setSystemBarColor(this, android.R.color.white);
        AppTool.setSystemBarLight(this);
    }

    private void displayFragment(State state) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        refreshStepTitle();

        if (state.name().equalsIgnoreCase(State.SHIPPING.name())) {
            fragment = new FragmentShipping();
            tv_shipping.setTextColor(getResources().getColor(R.color.green));
            image_shipping.clearColorFilter();
        } else if (state.name().equalsIgnoreCase(State.PAYMENT.name())) {
            fragment = new FragmentPayment();
            line_first.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            image_shipping.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            image_payment.clearColorFilter();
            tv_payment.setTextColor(getResources().getColor(R.color.green));
        } else if (state.name().equalsIgnoreCase(State.CONFIRMATION.name())) {
            fragment = new FragmentConfirmation();
            line_second.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            image_payment.setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
            image_confirm.clearColorFilter();
            tv_confirm.setTextColor(getResources().getColor(R.color.green));
        }

        if (fragment == null) return;
        fragmentTransaction.replace(R.id.frame_content, fragment);
        fragmentTransaction.commit();
    }

    private void refreshStepTitle() {
        tv_shipping.setTextColor(getResources().getColor(R.color.grey_20));
        tv_payment.setTextColor(getResources().getColor(R.color.grey_20));
        tv_confirm.setTextColor(getResources().getColor(R.color.grey_20));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        AppTool.changeMenuIconColor(menu, getResources().getColor(R.color.bottomNavigationBackground));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
