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
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
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

public class EditPostingan extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_konten_postingan)
    EditText etKonten;
    @BindView(R.id.edit_postingan_cl)
    CoordinatorLayout coordinatorLayout;

    APIService mAPIService;
    SharedPreferences userPrefs;
    Postingan postingan;
    User user;
    String apiToken;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_postingan);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setTitle("Edit Postingan");

        setUserAndToken();

        mAPIService = ApiUtils.getAPIService();

        postingan = new Postingan();
        postingan.setId(
                getIntent().getIntExtra("POSTINGAN_ID", -1)
        );
        postingan.setKonten(
                getIntent().getStringExtra("POSTINGAN_KONTEN")
        );
        etKonten.setText(postingan.getKonten());
        db = new DatabaseHelper(this);
    }

    @OnClick(R.id.btn_update_post)
    public void updatePostingan(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        //progressDialog.show();
        final String konten = etKonten.getText().toString();
        mAPIService.updatePostingan("Bearer " + apiToken, postingan.getId(), konten)
                .enqueue(new Callback<BasicRes>() {
                    @Override
                    public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                        //progressDialog.dismiss();
                        if(response.isSuccessful()){
                            if(response.body().isStatus()){
                                postingan.setKonten(konten);
                                db.updateKontenPostingan(postingan);
                                finish();
                            } else {
                                Snackbar.make(coordinatorLayout, response.body().getPesan(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BasicRes> call, Throwable t) {
                        //progressDialog.dismiss();
                        Snackbar snackbar = Snackbar.make(
                                coordinatorLayout,
                                "Terjadi kesalahan jaringan",
                                Snackbar.LENGTH_LONG
                        ).setAction("Coba Lagi", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updatePostingan();
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
