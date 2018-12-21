package com.example.gusarisna.pratikum.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.adapter.KomentarRecycleViewAdapter;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.Komentar;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahKomentar extends AppCompatActivity {

    @BindView(R.id.komentar_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.recyclerview_komentar)
    RecyclerView komentarRecycleView;
    @BindView(R.id.et_komentar)
    EditText etKomentar;
//    @BindView(R.id.kesalahan_jaringan)
//    LinearLayout kesalahanJaringan;
    @BindView(R.id.postingan_user_fotoprofil)
    CircleImageView userFotoProfil;
    @BindView(R.id.postingan_user_nama)
    TextView userNama;
    @BindView(R.id.postingan_konten)
    TextView postinganKonten;
    @BindView(R.id.postingan_created_at)
    TextView postinganCreatedAt;

    APIService mAPIService;
    SharedPreferences userPrefs;
    int postinganId;
    User user;
    String apiToken;
    List<Komentar> listKomentar;
    DatabaseHelper db;
    Postingan postingan;
    String BASE_URL_IMAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tambah_komentar);
        ButterKnife.bind(this);
        setUserAndToken();
        postinganId = getIntent().getIntExtra("POSTINGAN_ID", 0);

        mAPIService = ApiUtils.getAPIService();
        db = new DatabaseHelper(this);
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";

        postingan = db.getPostingan(postinganId);
        userNama.setText(postingan.getUser().getNama());
        postinganKonten.setText(postingan.getKonten());
        if(!postingan.getUser().getFotoProfil().equals("default.png")){
            String url = BASE_URL_IMAGE + postingan.getUser().getFotoProfil();
            Glide.with(this)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_user_round).centerCrop())
                .into(userFotoProfil);
        }
        PrettyTime prettyTime = new PrettyTime(new Locale("id"));
        Date createdAt = null;
        try {
            createdAt = parseDate(postingan.getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String waktuPost = prettyTime.format(createdAt);
        postinganCreatedAt.setText(waktuPost);

        refreshKomentar();
    }

    @OnClick(R.id.btn_kirim_komentar)
    public void kirimKomentar(){
        String isi = etKomentar.getText().toString();
        if(isi.equals(""))
            return;
        mAPIService.tambahKomentar(
                "Bearer " + apiToken,
                postinganId,
                isi
        ).enqueue(new Callback<BasicRes>() {
            @Override
            public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                if(response.isSuccessful()){
                    refreshKomentar();
                    int position = listKomentar.size() + 1;
                    if(position < 0)
                        position = 0;
                    komentarRecycleView.smoothScrollToPosition(position);
                } else {
                    Snackbar.make(
                            coordinatorLayout,
                            response.message(),
                            Snackbar.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<BasicRes> call, Throwable t) {
                Snackbar.make(
                        coordinatorLayout,
                        t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).show();
            }
        });

        postingan = db.getPostingan(postinganId);
        userNama.setText(postingan.getUser().getNama());
        postinganKonten.setText(postingan.getKonten());
    }

    public void refreshKomentar(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();

        mAPIService.getKomentar(
                "Bearer " + apiToken,
                postinganId
        ).enqueue(new Callback<List<Komentar>>() {
            @Override
            public void onResponse(Call<List<Komentar>> call, Response<List<Komentar>> response) {
                progressDialog.dismiss();
                listKomentar = Collections.emptyList();
                if(response.isSuccessful()){
                    listKomentar = response.body();
                    etKomentar.setText("");
                    tampilkanSemuaKomentar(listKomentar);

                }

            }

            @Override
            public void onFailure(Call<List<Komentar>> call, Throwable t) {
                progressDialog.dismiss();
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

    public void tampilkanSemuaKomentar(List<Komentar> listUser){
        KomentarRecycleViewAdapter adapter = new KomentarRecycleViewAdapter(this, user, listUser);
        komentarRecycleView.setLayoutManager(new LinearLayoutManager(this));
        komentarRecycleView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == 121){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Memproses");
            progressDialog.setMessage("Mohon untuk menunggu");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAPIService.hapusKomentar(
                    "Bearer "+ apiToken,
                    listKomentar.get(item.getGroupId()).getId()
            ).enqueue(new Callback<BasicRes>() {
            @Override
            public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    refreshKomentar();
                    Snackbar.make(
                            coordinatorLayout,
                            response.body().getPesan(),
                            Snackbar.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<BasicRes> call, Throwable t) {
                Snackbar.make(
                        coordinatorLayout,
                        t.getMessage(),
                        Snackbar.LENGTH_SHORT
                ).show();
            }
            });
        }
        return true;
    }

    public Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        return sdf.parse(date);
    }
}
