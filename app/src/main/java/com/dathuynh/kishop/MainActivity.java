package com.dathuynh.kishop;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Activity.GammingActivity;
import com.dathuynh.kishop.Activity.LoginActivity;
import com.dathuynh.kishop.Activity.ProfileActivity;
import com.dathuynh.kishop.Activity.TechnologyActivity;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.UserModel;
import com.dathuynh.kishop.helpers.BottomNavigationBehavior;
import com.dathuynh.kishop.helpers.DarkModePrefManager;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.transition.ChangeBounds;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.dathuynh.kishop.databinding.ActivityMainBinding;

import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    NavHostFragment navHostFragment;
    private BottomNavigationView bottomNavigationView;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    View view;

    DBApp dbApp;

    UserModel user = new UserModel();

    TextView userLoginView, userEmailView;

    ImageView userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbApp = new DBApp(this);

        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setExitTransition(new ChangeBounds());

        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);
        super.onCreate(savedInstanceState);

        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        user = dbApp.getUserProfile();
        binding = ActivityMainBinding.inflate(getLayoutInflater());


        view = binding.getRoot();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(view);

        setSupportActionBar(binding.appBarMain.toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarMain.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);
        setupNavigation();
    }

    @SuppressLint("SetTextI18n")
    private void setupNavigation() {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) binding.appBarMain.bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        userLoginView = headerView.findViewById(R.id.user_name_login_success);
        userEmailView = headerView.findViewById(R.id.user_email_login_success);
        userProfile = headerView.findViewById(R.id.image_view_user);

        if (user != null) {
            getUserProfile(user.getUser_email());
        } else {
            userLoginView.setText("hello new user");
            userEmailView.setText("needtologin@gmail.com");
        }

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(bottomNavigationView, navHostFragment.getNavController());
        }
    }

    private void getUserProfile(String email) {
        compositeDisposable.add(retrofitAPI.getUserProfile(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if (results.isSuccess()) {
                                userLoginView.setText(results.getUsername());
                                userEmailView.setText(results.getEmail());
                                Glide.with(view).load(results.getAvatar()).into(userProfile); // Image
                            }
                        },
                        throwable -> Toast.makeText(this, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            Intent user = new Intent(this, ProfileActivity.class);
            startActivity(user, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.nav_user) {
            Intent userActivity = new Intent(this, LoginActivity.class);
            startActivity(userActivity,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else if (id == R.id.nav_teach) {
            Intent recommend = new Intent(this, TechnologyActivity.class);
            startActivity(recommend);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_dark_mode) {
            DarkModePrefManager darkModePrefManager = new DarkModePrefManager(this);
            darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

}