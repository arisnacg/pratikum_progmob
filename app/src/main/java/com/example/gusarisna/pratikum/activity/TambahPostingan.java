package com.example.gusarisna.pratikum.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahPostingan extends AppCompatActivity {

    @BindView(R.id.et_konten_postingan)
    EditText kontenPostingan;
    @BindView(R.id.tambah_postingan_cl)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    APIService mAPIService;
    SharedPreferences userPrefs;
    User user;
    String apiToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_postingan);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Postingan Baru");
        setUserAndToken();
        mAPIService = ApiUtils.getAPIService();
    }

    @OnClick(R.id.btn_kirim_post)
    public void btnKirimPostClicked(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String konten = kontenPostingan.getText().toString();

        mAPIService.postPostingan("Bearer " + apiToken, konten).enqueue(new Callback<BasicRes>() {
            @Override
            public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    if(response.body().isStatus()){
                        finish();
                    } else {
                        Snackbar.make(coordinatorLayout, response.body().getPesan(), Snackbar.LENGTH_LONG).show();
                    }
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
                        btnKirimPostClicked();
                    }
                });
                snackbar.show();
            }
        });
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
