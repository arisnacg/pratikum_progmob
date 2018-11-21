package com.example.gusarisna.pratikum.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.Coba;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    @BindView(R.id.email)
    EditText loginEmail;
    @BindView(R.id.password)
    EditText loginPassword;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    private APIService mAPIService;
    SharedPreferences userPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        if(cekUser()){
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.login);
        ButterKnife.bind(this);
        mAPIService = ApiUtils.getAPIService();
    }

    @OnClick(R.id.btn_login)
    public void buttonLoginClicked(){
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAPIService.sendLoginReq(email, password).enqueue(new Callback<AuthRes>() {
            @Override
            public void onResponse(Call<AuthRes> call, Response<AuthRes> response) {
                progressDialog.dismiss();
                if(response.body().isStatus()){
                    loginBerhasil(response.body());
                } else {
                    Snackbar snackbar = Snackbar.make(
                            coordinatorLayout,
                            response.body().getPesan(),
                            Snackbar.LENGTH_LONG
                    );
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<AuthRes> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(
                        coordinatorLayout,
                        "Terjadi kesalahan jaringan",
                        Snackbar.LENGTH_LONG
                ).setAction("Coba Lagi", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonLoginClicked();
                    }
                });
                snackbar.show();

            }
        });
    }

    public void loginBerhasil(AuthRes res){
        simpanPrefs(res.getUserId(), res.getApiToken());
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

    public boolean cekUser(){
        int id = userPrefs.getInt("userId", 0);
        String apiToken = userPrefs.getString("apiToken", "");
        return (id == 0)? false : true;
    }

    public void simpanPrefs(int userId, String apiToken){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt("userId", userId);
        editor.putString("apiToken", apiToken);
        editor.apply();
    }

    public void tamplikanSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @OnClick(R.id.link_regis)
    public void linkToRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}
