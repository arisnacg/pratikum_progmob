package com.example.gusarisna.pratikum.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.activity.TambahPostingan;
import com.example.gusarisna.pratikum.adapter.PostinganRecycleViewAdapter;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostinganFragment extends Fragment {

    View v;
    RecyclerView mRecyclerView;
    List<Postingan> listPostingan;
    APIService mAPIService;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences userPrefs;
    int userId;
    String apiToken;

    @BindView(R.id.btn_tambah_post)
    FloatingActionButton btnTambahPost;

    public PostinganFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.postingan_fragment, container, false);
        ButterKnife.bind(this, v);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.postingan_coordinator_layout);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.postingan_recycle_view);
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPrefs = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        cekUser();
        mAPIService = ApiUtils.getAPIService();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPostingan();
    }

    public void refreshPostingan(){
        mAPIService.getAllPostingan("Bearer " + apiToken).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                listPostingan = Collections.emptyList();
                if(response.isSuccessful()){
                    listPostingan = response.body();
                }
                tampilkanSemuaPostingan(listPostingan);

            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(
                        coordinatorLayout,
                        t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).setAction("Coba Lagi", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshPostingan();
                    }
                });
                snackbar.show();
            }
        });
    }

    public void tampilkanSemuaPostingan(List<Postingan> listPostingan){
        PostinganRecycleViewAdapter adapter = new PostinganRecycleViewAdapter(getContext(), listPostingan, PostinganFragment.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }

    public boolean cekUser(){
        userId = userPrefs.getInt("userId", 0);
        apiToken = userPrefs.getString("apiToken", "");
        return (userId == 0)? false : true;
    }

    @OnClick(R.id.btn_tambah_post)
    public void tambahPostingan(){
        Intent i = new Intent(getActivity().getBaseContext(), TambahPostingan.class);
        startActivity(i);
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }
}
