package com.example.gusarisna.pratikum.data.remote;

import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.Coba;
import com.example.gusarisna.pratikum.data.model.LikeRes;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("user")
    Call<User> getUser(@Header("Authorization") String authHeader);

    @POST("logout")
    Call<AuthRes> logoutUser(@Header("Authorization") String authHeader);

    @GET("user/all")
    Call<List<User>> getAllUser();


    @PUT("user")
    @FormUrlEncoded
    Call<BasicRes> updateUser(
            @Header("Authorization") String authHeader,
            @Field("nama") String nama,
            @Field("email") String email
    );

    @GET("postingan")
    Call<List<Postingan>> getAllPostingan(@Header("Authorization") String authHeader);

    @POST("postingan")
    @FormUrlEncoded
    Call<BasicRes> postPostingan(
            @Header("Authorization") String authHeader,
            @Field("konten") String konten
    );

    @PUT("postingan/{id}")
    @FormUrlEncoded
    Call<BasicRes> updatePostingan(
            @Header("Authorization") String authHeader,
            @Path("id") int id,
            @Field("konten") String konten
    );

    @DELETE("postingan/{id}")
    Call<BasicRes> hapusPostingan(
            @Header("Authorization") String authHeader,
            @Path("id") int id
    );

    @POST("like/toggle/{postingan_id}")
    Call<LikeRes> toggleLike(
            @Header("Authorization") String authHeader,
            @Path("postingan_id") int postinganId
    );


}
