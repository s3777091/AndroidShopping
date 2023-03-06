package com.dathuynh.kishop.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dathuynh.kishop.Adapter.SpecialAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterActivity extends AppCompatActivity {

    TextView backLogin;
    Button registerClick;
    EditText userEmail, Userpassword, UserName;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new ChangeBounds());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);

        initComponent();

    }

    private void RegisterUser(EditText userEmail,EditText fullNameEdit, EditText passwordEdit){

        String email = userEmail.getText().toString();
        String fullName = fullNameEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Email can't null", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(this, "fullName can't null", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "password can't null", Toast.LENGTH_LONG).show();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("fullName", fullName);
        data.put("password", password);

        compositeDisposable.add(retrofitAPI.registerNewuser(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){
                                Toast.makeText(this, "Pls check your email", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Fail to create new user", Toast.LENGTH_LONG).show();
                            }
                        },
                        throwable -> Toast.makeText(this, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }





    private void initComponent() {
        backLogin = findViewById(R.id.back_to_login);
        registerClick = findViewById(R.id.register_user);

        userEmail = findViewById(R.id.user_email);
        Userpassword = findViewById(R.id.password_user_edit);
        UserName = findViewById(R.id.full_name_user);


        registerClick.setOnClickListener(view -> {
            RegisterUser(userEmail,UserName,Userpassword);
        });

        backLogin.setOnClickListener(view -> {
            Intent signIn = new Intent(this, LoginActivity.class);
            startActivity(signIn,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }


}
