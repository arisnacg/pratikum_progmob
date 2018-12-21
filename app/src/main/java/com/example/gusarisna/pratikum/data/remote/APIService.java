package com.example.gusarisna.pratikum.data.remote;

import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.Coba;
import com.example.gusarisna.pratikum.data.model.Follower;
import com.example.gusarisna.pratikum.data.model.GantiProfilRes;
import com.example.gusarisna.pratikum.data.model.Komentar;
import com.example.gusarisna.pratikum.data.model.LikeRes;
import com.example.gusarisna.pratikum.data.model.Notif;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    @POST("login")
    @FormUrlEncoded
    Call<AuthRes> sendLoginReq(
            @Field("email") String email,
            @Field("password") String password,
            @Field("firebase_token") String firebaseToken
    );

    @POST("register")
    @FormUrlEncoded
    Call<AuthRes> sendRegisterReq(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password,
            @Field("c_password") String confirmPassword,
            @Field("firebase_token") String firebaseToken
    );

    @GET("user")
    Call<User> getUser(@Header("Authorization") String authHeader);

    @POST("logout")
    Call<AuthRes> logoutUser(@Header("Authorization") String authHeader);

    @GET("user/all")
    Call<List<User>> getAllUser(@Header("Authorization") String authHeader);


    @PUT("user")
    @FormUrlEncoded
    Call<BasicRes> updateUser(
            @Header("Authorization") String authHeader,
            @Field("nama") String nama,
            @Field("email") String email
    );

    @GET("postingan")
    Call<List<Postingan>> getAllPostingan(@Header("Authorization") String authHeader);

    @GET("user/postingan")
    Call<List<Postingan>> getUserPostingan(@Header("Authorization") String authHeader);

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

    @POST("follow/{id}")
    Call<BasicRes> toggleFollow(
            @Header("Authorization") String authHeader,
            @Path("id") int id
    );

    @GET("follower")
    Call<List<Follower>> getFollower(
            @Header("Authorization") String authHeader
    );

    @GET("postingan/{id}/komentar")
    Call<List<Komentar>> getKomentar(
            @Header("Authorization") String authHeader,
            @Path("id") int id
    );

    @POST("komentar")
    @FormUrlEncoded
    Call<BasicRes> tambahKomentar(
            @Header("Authorization") String authHeader,
            @Field("postingan_id") int postinganId,
            @Field("isi") String isi
    );

    @DELETE("komentar/{id}")
    Call<BasicRes> hapusKomentar(
            @Header("Authorization") String authHeader,
            @Path("id") int komentarId
    );

    @Multipart
    @POST("user/fotoprofil")
    Call<GantiProfilRes> gantiFotoProfil(
            @Header("Authorization") String authHeader,
            @PartMap Map<String, RequestBody> map
    );

    @GET("notif")
    Call<List<Notif>> getNotif(@Header("Authorization") String authHeader);

}
