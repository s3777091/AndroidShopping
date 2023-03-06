package com.dathuynh.kishop.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.MainActivity;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    DBApp dbApp;

    TextView signUp;

    EditText UserEmail, UserPassword;

    Button LoginUser;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new ChangeBounds());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        dbApp = new DBApp(this);
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);

        initComponent();

    }

    private void LoginUser(EditText addEmail, EditText addPasword){

        String emailAdd = addEmail.getText().toString();
        String password = addPasword.getText().toString();

        if(TextUtils.isEmpty(emailAdd)){
            Toast.makeText(this, "Email can't null", Toast.LENGTH_LONG).show();
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "password can't null", Toast.LENGTH_LONG).show();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("email", emailAdd);
        data.put("password", password);
        compositeDisposable.add(retrofitAPI.loginUser(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        res -> {
                            UserModel ns = new UserModel();
                            ns.setUser_email(res.getEmail());
                            dbApp.addTemporaryUser(ns);

                            String userN = dbApp.getUserProfile().getUser_email();
                            Toast.makeText(this, "Login success with user " + userN
                                    ,Toast.LENGTH_LONG).show();
                        },
                        throwable -> Log.e("error_login", throwable.toString())
                ));


    }

    private void initComponent() {
        signUp = findViewById(R.id.sign_up);
        LoginUser = findViewById(R.id.login_user);

        UserEmail = findViewById(R.id.email_user);
        UserPassword = findViewById(R.id.password_user_login);


        LoginUser.setOnClickListener(view -> {
            LoginUser(UserEmail, UserPassword);
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

        signUp.setOnClickListener(view -> {
            Intent signUp = new Intent(this, RegisterActivity.class);
            startActivity(signUp,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });

    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
