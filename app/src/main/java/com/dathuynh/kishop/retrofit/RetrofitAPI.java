package com.dathuynh.kishop.retrofit;


import com.dathuynh.kishop.Model.CheckOutModel;
import com.dathuynh.kishop.Model.ListProduct;
import com.dathuynh.kishop.Model.MessageModels;
import com.dathuynh.kishop.Model.OrderModelList;
import com.dathuynh.kishop.Model.UserLogin;

import java.util.HashMap;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @POST("api/v1/search_product")
    Observable<ListProduct> getListSearch(@Body HashMap<String, String> Data);

    @GET("api/v1/view/all/product")
    Observable<ListProduct> getProduct(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize);

    @GET("api/v1/detail/product")
    Observable<ListProduct> viewProductDetail(@Query("id") long id);

    @POST("api/v1/user/register")
    Observable<MessageModels> registerNewuser(@Body HashMap<String, String> Data);

    @POST("api/v1/login_android")
    Observable<UserLogin> loginUser(@Body HashMap<String, String> Data);

    @GET("api/v1/view/payment")
    Observable<OrderModelList> getViewPayment(@Query("email") String email);

    @GET("api/v1/view/product")
    Observable<ListProduct> getProductByType(
            @Query("pageNo") int pageNo,
            @Query("pageSize") int pageSize,
            @Query("type") String type);


    @POST("/api/v1/update/payment")
    Observable<MessageModels> updatePayment(@Body HashMap<String, String> Data);

    @POST("api/v1/payment/product")
    Observable<MessageModels> postToCartApi(@Body HashMap<String, String> Data);

    @POST("api/v1/delete/payment")
    Observable<MessageModels> deletePayment(@Body HashMap<String, String> Data);

    @POST("api/v1/update/user_adress")
    Observable<MessageModels> UpdateNewAdress(@Body HashMap<String, String> Data);

    @GET("api/v1/view/user_profile")
    Observable<UserLogin> getUserProfile(@Query("email") String email);

    @POST("api/v1/checkout")
    Observable<CheckOutModel> checkOut(@Body HashMap<String, String> Data);

    @POST("value/v1/add_search_product")
    Observable<MessageModels> PostSearch(@Body HashMap<String, String> Data);


    @POST("api/v1/payment/product_by_name")
    Observable<MessageModels> PostProductByName(@Body HashMap<String, String> Data);


}

