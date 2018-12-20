package com.example.gusarisna.pratikum.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Pengaturan extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    APIService mAPIService;
    SharedPreferences userPrefs;
    Postingan postingan;
    User user;
    String apiToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pengaturan);
        setUserAndToken();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Pengaturan");
        mAPIService = ApiUtils.getAPIService();
    }

    @OnClick(R.id.menu_edit_profil)
    public void editProfil() {
        Intent i = new Intent(this, EditProfile.class);
        startActivity(i);
    }

    @OnClick(R.id.menu_ganti_foto)
    public void gantiFotoProfil(){
        Intent i = new Intent(this, GantiFotoProfil.class);
        startActivity(i);
    }

    @OnClick(R.id.menu_logout)
    public void btnLogoutClicked(){
        mAPIService.logoutUser("Bearer " + apiToken).enqueue(new Callback<AuthRes>() {
            @Override
            public void onResponse(Call<AuthRes> call, Response<AuthRes> response) {
                if(response.isSuccessful()){
                    logOutUser();
                } else {
                    Toast.makeText(getBaseContext(), "Logout gagal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthRes> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logOutUser(){
        clearPref();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        Home.fa.finish();
        finish();
    }

    public void clearPref(){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt("userId", 0);
        editor.putString("userNama", "");
        editor.putString("userEmail", "");
        editor.putString("userFotoProfil", "");
        editor.putString("apiToken", "");
        editor.apply();
    }

    public void setUserAndToken(){
        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        user = new User();
        user.setId(userPrefs.getInt("userId", 0));
        user.setNama(userPrefs.getString("userNama", ""));
        user.setEmail(userPrefs.getString("userEmail", ""));
        user.setFotoProfil(userPrefs.getString("userFotoProfil", ""));
        apiToken = userPrefs.getString("apiToken", "");
    }
}
