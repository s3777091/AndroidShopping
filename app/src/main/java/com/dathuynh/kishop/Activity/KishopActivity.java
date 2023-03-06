package com.dathuynh.kishop.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dathuynh.kishop.Adapter.SpecialAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.Tool.AppTool;
import com.dathuynh.kishop.Tool.SpacingItemDecoration;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class KishopActivity extends AppCompatActivity {

    RetrofitAPI retrofitAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    RecyclerView recyclerViewRecommend;
    List<ProductDetail> spList;

    SpecialAdapter kiAdapter;

    int page = 1;
    Handler handler = new Handler();
    boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kishop);

        initComponent();
    }


    private void initComponent() {
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);
        recyclerViewRecommend = findViewById(R.id.recycle_view_ki_shop);

        recyclerViewRecommend.setLayoutManager(new GridLayoutManager(this,2));
        recyclerViewRecommend.addItemDecoration(new SpacingItemDecoration(2, AppTool.dpToPx(this, 8), true));
        recyclerViewRecommend.setHasFixedSize(true);

        spList = new ArrayList<>();

        getData(page);
        addEventLoad();
    }

    private void addEventLoad() {
        recyclerViewRecommend.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if(!isLoading){

                    int visibleItemCount = Objects.requireNonNull(linearLayoutManager).getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore() {
        if(!isLoading){
            handler.post(() -> {
                spList.add(null);
                kiAdapter.notifyItemInserted(spList.size()-1);
            });
        } else {
            handler.postDelayed(() -> {
                spList.remove(spList.size()-1);
                kiAdapter.notifyItemRemoved(spList.size());
                page = page+1;
                getData(page);
                isLoading = false;
            }, 2000);
        }

    }

    private void getData(int page) {
        compositeDisposable.add(retrofitAPI.getProductByType(page, 20, "Laptop")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            if(results.isSuccess()){
                                if(kiAdapter == null) {
                                    spList = results.getResults();
                                    kiAdapter = new SpecialAdapter(this, spList);
                                    recyclerViewRecommend.setAdapter(kiAdapter);
                                } else {
                                    int position = spList.size()-1;
                                    int amount = results.getResults().size();
                                    for (int i=0; i <amount; i++){
                                        spList.add(results.getResults().get(i));
                                    }
                                    kiAdapter.notifyItemRangeInserted(position, amount);
                                }
                            } else {
                                Toast.makeText(this, "End of content", Toast.LENGTH_SHORT).show();
                                isLoading = true;
                            }
                        },
                        throwable -> Toast.makeText(this, "Can't connect to sever", Toast.LENGTH_LONG).show()
                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }




}