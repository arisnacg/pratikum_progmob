package com.example.gusarisna.pratikum.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {

    @BindView(R.id.nama)
    EditText etNama;
    @BindView(R.id.email)
    EditText etEmail;
    @BindView(R.id.password)
    EditText etPassword;
    @BindView(R.id.c_password)
    EditText etConfirmPassword;
    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    APIService mAPIService;
    SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        ButterKnife.bind(this);
        mAPIService = ApiUtils.getAPIService();
        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @OnClick(R.id.btn_regis)
    public void btnRegisClicked(){
        final String nama = etNama.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        final String firebaseToken = userPrefs.getString("firebaseToken", "");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAPIService.sendRegisterReq(nama, email, password, confirmPassword, firebaseToken).enqueue(new Callback<AuthRes>() {
            @Override
            public void onResponse(Call<AuthRes> call, final Response<AuthRes> response) {
                Toast.makeText(Register.this, firebaseToken, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().isStatus()){
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Register.this);
                        alertBuilder.setTitle("Selamat");
                        alertBuilder.setMessage("Registrasi akun berhasil")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        registerBerhasil(response.body());
                                    }
                                });
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    } else {
                        tamplikanSnackbar(response.body().getPesan());
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout,"Terjadi kesalahan jaringan",
                            Snackbar.LENGTH_LONG
                    ).setAction("Coba Lagi", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btnRegisClicked();
                        }
                    });
                    snackbar.show();
                }
            }

            @Override
            public void onFailure(Call<AuthRes> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar snackbar = Snackbar.make(coordinatorLayout,"Terjadi kesalahan jaringan",
                        Snackbar.LENGTH_LONG
                ).setAction("Coba Lagi", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnRegisClicked();
                    }
                });
                snackbar.show();
            }
        });
    }

    public void tamplikanSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    @OnClick(R.id.link_login)
    public void linkToLogin(){
        finish();
    }

    public void registerBerhasil(AuthRes res){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt("userId", res.getUserId());
        editor.putString("userNama", res.getUserNama());
        editor.putString("userEmail", res.getUserEmail());
        editor.putString("userFotoProfil", res.getFotoProfil());
        editor.putString("apiToken", res.getApiToken());
        editor.apply();
        Intent i = new Intent(this, Home.class);
        startActivity(i);
        finish();
    }

}
