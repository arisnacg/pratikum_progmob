package com.example.gusarisna.pratikum.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.BasicRes;
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

public class EditProfile extends AppCompatActivity {

    @BindView(R.id.user_nama)
    EditText userNama;
    @BindView(R.id.user_email)
    EditText userEmail;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coodinator_layout)
    CoordinatorLayout coordinatorLayout;

    APIService mAPIService;
    SharedPreferences userPrefs;
    Postingan postingan;
    int userId;
    String apiToken;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Edit Profil");

        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        getUser();

        user.setNama(
                getIntent().getStringExtra("USER_NAMA")
        );
        user.setEmail(
                getIntent().getStringExtra("USER_EMAIL")
        );

        userNama.setText(user.getNama());
        userEmail.setText(user.getEmail());

        mAPIService = ApiUtils.getAPIService();
    }

    @OnClick(R.id.btn_update_profil)
    public void updateProfil(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();


        String nama = userNama.getText().toString();
        String email = userEmail.getText().toString();

        mAPIService.updateUser("Bearer " + apiToken, nama, email)
                .enqueue(new Callback<BasicRes>() {
                    @Override
                    public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicRes> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(
                                coordinatorLayout,
                                "Terjadi kesalahan jaringan",
                                Snackbar.LENGTH_LONG
                        ).setAction("Coba Lagi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateProfil();
                            }
                        });
                        snackbar.show();
                    }
                });

    }

    public void getProfil(){

    }

    public boolean getUser(){
        userId = userPrefs.getInt("userId", 0);
        apiToken = userPrefs.getString("apiToken", "");
        return (userId == 0)? false : true;
    }
}
