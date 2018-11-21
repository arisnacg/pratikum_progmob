package com.example.gusarisna.pratikum.data.remote;

import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.Coba;
import com.example.gusarisna.pratikum.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("login")
    @FormUrlEncoded
    Call<AuthRes> sendLoginReq(@Field("email") String email, @Field("password") String password);

    @POST("register")
    @FormUrlEncoded
    Call<AuthRes> sendRegisterReq(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password,
            @Field("c_password") String confirmPassword
    );

    @GET("login_user/{id}/{api_token}")
    Call<User> getUser(@Path("id") int id, @Path("api_token") String apiToken);

    @POST("logout")
    Call<AuthRes> logoutUser(@Header("Authorization") String authHeader);

    @GET("user/all")
    Call<List<User>> getAllUser();

}
