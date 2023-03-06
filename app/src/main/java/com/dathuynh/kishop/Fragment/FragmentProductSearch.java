package com.dathuynh.kishop.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dathuynh.kishop.Adapter.SearchAdapter;
import com.dathuynh.kishop.Config.Config;
import com.dathuynh.kishop.Database.DBApp;
import com.dathuynh.kishop.Model.ProductDetail;
import com.dathuynh.kishop.R;
import com.dathuynh.kishop.retrofit.RetrofitAPI;
import com.dathuynh.kishop.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FragmentProductSearch extends Fragment {

    ImageView button_search;
    EditText edtsearch;
    RecyclerView recyclerView;
    List<ProductDetail> ListSearchProduct;
    RetrofitAPI retrofitAPI;


    String email;
    DBApp dbApp;

    SearchAdapter searchAdapter;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentProductSearch() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        SetupFragment(view);
        return view;
    }

    private void SetupFragment(View view) {
        ListSearchProduct = new ArrayList<>();
        edtsearch = view.findViewById(R.id.edtseach);
        recyclerView = view.findViewById(R.id.recycle_view_search);
        button_search = view.findViewById(R.id.button_search);

        dbApp = new DBApp(view.getContext());
        retrofitAPI = RetrofitClient.getInstance(Config.PRODUCER_BASE_URL).create(RetrofitAPI.class);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                email = dbApp.getUserProfile().getUser_email();
            }

        } catch (Exception e) {
            Toast.makeText(view.getContext(), "Pls Login user", Toast.LENGTH_SHORT).show();
        }

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataSearch(view, edtsearch.getText().toString());
            }
        });
    }

    private void getDataSearch(View view, String s) {
        ListSearchProduct.clear();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("searchValue", s);
        compositeDisposable.add(retrofitAPI.getListSearch(hashMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        data -> {
                            ListSearchProduct = data.getResults();
                            searchAdapter = new SearchAdapter(view.getContext(), ListSearchProduct, retrofitAPI, email);
                            recyclerView.setAdapter(searchAdapter);
                        },
                        throwable -> {
                            Toast.makeText(view.getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}