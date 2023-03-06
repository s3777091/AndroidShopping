package com.dathuynh.kishop.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;


import java.util.HashMap;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {


    EditText emailUserProfile, addressUserProfile, PhoneUserProfile;
    Button btnUpdate;

    DBApp dbApp;
    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        dbApp = new DBApp(this);
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);
        initComponent();

        try {
            UserModel user = dbApp.getUserProfile();
            getUserProfile(user.getUser_email());

            btnUpdate.setOnClickListener(vies -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Notification");
                builder.setMessage("Do you want to Update New user profile");

                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    UpdateNewAddress(
                            emailUserProfile.getText().toString(),
                            addressUserProfile.getText().toString(),
                            PhoneUserProfile.getText().toString()
                    );
                    Toast.makeText(this, "Success update new profile", Toast.LENGTH_SHORT).show();
                });
                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.show();
            });

        } catch (Exception e) {
            Toast.makeText(this, "Pls Login user", Toast.LENGTH_SHORT).show();
        }

    }

    private void getUserProfile(String email) {
        compositeDisposable.add(retrofitAPI.getUserProfile(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                emailUserProfile.setText(results.getEmail());
                                addressUserProfile.setText(results.getAddress());
                                PhoneUserProfile.setText(results.getPhone());
                            }
                        },
                        throwable -> Toast.makeText(this, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    private void UpdateNewAddress(String email, String location, String phone) {
        HashMap<String, String> st = new HashMap<>();
        st.put("emailUser", email);
        st.put("location", location);
        st.put("phone", phone);

        compositeDisposable.add(retrofitAPI.UpdateNewAdress(st)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                Toast.makeText(this, results.getStatus(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "End of content", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> Toast.makeText(this, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    private void initComponent() {
        emailUserProfile = findViewById(R.id.email_user_profile);
        addressUserProfile = findViewById(R.id.address_user_profile);
        PhoneUserProfile = findViewById(R.id.phone_user_profile);
        btnUpdate = findViewById(R.id.update_user);
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}
