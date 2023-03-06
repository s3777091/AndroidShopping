package com.dathuynh.kishop.Fragment;

import static com.dathuynh.kishop.Config.Config.WEBSHOP;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dathuynh.kishop.Activity.GammingActivity;
import com.dathuynh.kishop.Activity.HomeAndLifeActivity;
import com.dathuynh.kishop.Activity.KishopActivity;
import com.dathuynh.kishop.Activity.RecommendActivity;
import com.dathuynh.kishop.Adapter.RecommendAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.Tool.AppTool;
import com.dathuynh.kishop.Tool.SpacingItemDecoration;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentMainActivity extends Fragment {


    Context context;
    RecyclerView recyclerViewRecommend, recycleViewFalava, recyclerViewGaming, recyclerViewSpecial;

    RelativeLayout goWeb;
    ViewFlipper viewFlipper;

    RecommendAdapter recommendAdapter;
    TextView seeMoreRecommend, seeMoreKiShop, seeMoreHome, seeMoreGaming;

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    List<ProductDetail> spList;
    int page = 1;

    public FragmentMainActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);

        context = this.getContext();
        viewFlipper = view.findViewById(R.id.vewlipper);
        recyclerViewRecommend = view.findViewById(R.id.recycle_view_recommend);
        recycleViewFalava = view.findViewById(R.id.recycle_view_falava);

        recyclerViewSpecial = view.findViewById(R.id.recycle_view_special);
        recyclerViewGaming = view.findViewById(R.id.recycle_view_gamming);

        goWeb = view.findViewById(R.id.click_web);


        seeMoreRecommend = view.findViewById(R.id.see_more_recommend);
        seeMoreKiShop = view.findViewById(R.id.see_more_ki_shop);
        seeMoreHome = view.findViewById(R.id.see_more_home);
        seeMoreGaming = view.findViewById(R.id.see_more_gamming);


        recyclerViewSpecial.setLayoutManager(new GridLayoutManager(context.getApplicationContext(),2));
        recyclerViewSpecial.addItemDecoration(new SpacingItemDecoration(2, AppTool.dpToPx(context.getApplicationContext(), 8), true));
        recyclerViewSpecial.setNestedScrollingEnabled(false);
        recyclerViewSpecial.setHasFixedSize(true);

        spList = new ArrayList<>();

        if(isConnected(context.getApplicationContext())) {
            ActionViewFlipper();
            GetDataTypePhone();
            GetDataKiShop();
            GetDataHomeAndLife();
            getGamming();

        } else {
            Toast.makeText(context.getApplicationContext(), "Fail to connect internet", Toast.LENGTH_LONG).show();
        }

        seeMoreRecommend.setOnClickListener(view1 -> {
            Intent recommend = new Intent(context, RecommendActivity.class);
            startActivity(recommend);
        });

        seeMoreKiShop.setOnClickListener(viewC -> {
            Intent recommend = new Intent(context, KishopActivity.class);
            startActivity(recommend);
        });

        seeMoreHome.setOnClickListener(v -> {
            Intent recommend = new Intent(context, HomeAndLifeActivity.class);
            startActivity(recommend);
        });

        seeMoreGaming.setOnClickListener(v -> {
            Intent recommend = new Intent(context, GammingActivity.class);
            startActivity(recommend);
        });

        goWeb.setOnClickListener(v -> {
            Uri uri = Uri.parse(WEBSHOP); // missing 'http://' will cause crashed
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        return view;
    }

    @SuppressLint("ResourceType")
    private void ActionViewFlipper() {
        List<String> ads = new ArrayList<>();

        ads.add("https://cf.shopee.vn/file/c433f40e9fc218b0f96b9ed0243bd01c_xxhdpi");
        ads.add("https://cf.shopee.vn/file/a4c94acbe2facdc761e59289fa4d3d68_xxhdpi");
        ads.add("https://cf.shopee.vn/file/2c7ab4a8b863ba26f0500986c7ab6074_xxhdpi");
        ads.add("https://cf.shopee.vn/file/f67d7dafa22186390fc37daaaa7f08d3_xxhdpi");

        for (int i = 0; i < ads.size(); i++) {
            ImageView imageView = new ImageView(context.getApplicationContext());
            Glide.with(context.getApplicationContext()).load(ads.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }

        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);

        Animation slide_in = AnimationUtils.loadAnimation(context.getApplicationContext(), R.drawable.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(context.getApplicationContext(), R.drawable.slide_out_right);

        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }


    private void GetDataTypePhone(){
        compositeDisposable.add(retrofitAPI.getProductByType(page, 20, "Phone")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){
                                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(context.getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
                                recyclerViewRecommend.setLayoutManager(horizontalLayoutManagaer);
                                recyclerViewRecommend.setClipToPadding(false);
                                recyclerViewRecommend.setHasFixedSize(true);
                                recommendAdapter = new RecommendAdapter(context.getApplicationContext(), results.getResults());
                                recyclerViewRecommend.setAdapter(recommendAdapter);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "Wrong content", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    private void GetDataKiShop(){
        compositeDisposable.add(retrofitAPI.getProductByType(page, 20, "Laptop")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){
                                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(context.getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
                                recycleViewFalava.setLayoutManager(horizontalLayoutManagaer);
                                recycleViewFalava.setClipToPadding(false);
                                recycleViewFalava.setHasFixedSize(true);
                                recommendAdapter = new RecommendAdapter(context.getApplicationContext(), results.getResults());
                                recycleViewFalava.setAdapter(recommendAdapter);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "End of content", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    private void getGamming(){
        compositeDisposable.add(retrofitAPI.getProductByType(page, 20, "Game")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){
                                LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(context.getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
                                recyclerViewGaming.setLayoutManager(horizontalLayoutManagaer);
                                recyclerViewGaming.setClipToPadding(false);
                                recyclerViewGaming.setHasFixedSize(true);
                                recommendAdapter = new RecommendAdapter(context.getApplicationContext(), results.getResults());
                                recyclerViewGaming.setAdapter(recommendAdapter);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "End of content", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    private void GetDataHomeAndLife(){
        compositeDisposable.add(retrofitAPI.getProductByType(page, 20, "Home and life")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){
                                recommendAdapter = new RecommendAdapter(context.getApplicationContext(), results.getResults());
                                recyclerViewSpecial.setAdapter(recommendAdapter);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "End of content", Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> Toast.makeText(context.getApplicationContext(), "Can't connect to sever", Toast.LENGTH_LONG).show()
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