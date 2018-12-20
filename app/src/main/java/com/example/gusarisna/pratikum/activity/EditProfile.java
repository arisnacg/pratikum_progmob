package com.example.gusarisna.pratikum.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity {

    @BindView(R.id.user_fotoprofil)
    CircleImageView ivUser;
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
    String apiToken;
    User user;
    String BASE_URL_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_profile);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Edit Profil");

        setUserAndToken();

        userNama.setText(user.getNama());
        userEmail.setText(user.getEmail());

        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
        if(!user.getFotoProfil().equals("default.png")){
            String url = BASE_URL_IMAGE + user.getFotoProfil();
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher_round).centerCrop())
                    .into(ivUser);
        }

        mAPIService = ApiUtils.getAPIService();
    }

    @OnClick(R.id.btn_update_profil)
    public void updateProfil(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();


        user.setNama(userNama.getText().toString());
        user.setEmail(userEmail.getText().toString());

        mAPIService.updateUser("Bearer " + apiToken, user.getNama(), user.getEmail())
                .enqueue(new Callback<BasicRes>() {
                    @Override
                    public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                        progressDialog.dismiss();
                        if(response.isSuccessful()){
                            setUserPref(user.getId(), user.getNama(), user.getEmail());
                            Intent returnIntent = new Intent();
                            setResult(RESULT_OK, returnIntent);
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

    public void setUserAndToken(){
        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);
        user = new User();
        user.setId(userPrefs.getInt("userId", 0));
        user.setNama(userPrefs.getString("userNama", ""));
        user.setEmail(userPrefs.getString("userEmail", ""));
        user.setFotoProfil(userPrefs.getString("userFotoProfil", ""));
        apiToken = userPrefs.getString("apiToken", "");
    }

    public void setUserPref(int id, String nama, String email){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt("userId", id);
        editor.putString("userNama", nama);
        editor.putString("userEmail", email);
        editor.apply();
    }
}
