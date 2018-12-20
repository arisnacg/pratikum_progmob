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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.activity.Home;
import com.example.gusarisna.pratikum.activity.TambahPostingan;
import com.example.gusarisna.pratikum.adapter.PostinganRecycleViewAdapter;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
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
    SwipeRefreshLayout swipeLayout;
    List<Postingan> listPostingan;
    APIService mAPIService;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences userPrefs;
    User user;
    String apiToken;
    DatabaseHelper db;
    private static final String LOG = "DEVELOP";

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
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);
                refreshPostingan();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ((Home)getActivity()).getUser();
        apiToken = ((Home)getActivity()).getApiToken();
        mAPIService = ApiUtils.getAPIService();
        db = ((Home)getActivity()).getDatabaseHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPostingan();
    }

    public void refreshPostingan(){
        Log.d(LOG, "Request API Postingan");
        mAPIService.getAllPostingan("Bearer " + apiToken).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if(response.isSuccessful()){
                    Log.d(LOG, "API Postingan Respone OK");
                    db.deleteAllPostingan();
                    db.addMultiPostingaan(response.body());
                    listPostingan = response.body();
                } else {
                    Log.d(LOG, "API Postingan Respone NOT OK");
                    listPostingan = db.getAllPostingan();
                }
                tampilkanSemuaPostingan(listPostingan);
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Log.d(LOG, "API Postingan Gagal : " + t.getMessage());
                Snackbar snackbar = Snackbar.make(
                        coordinatorLayout,
                        t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).setAction(t.getMessage(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshPostingan();
                    }
                });
                snackbar.show();
                listPostingan = db.getAllPostingan();
                tampilkanSemuaPostingan(listPostingan);
                swipeLayout.setRefreshing(false);
            }
        });
    }

    public void tampilkanSemuaPostingan(List<Postingan> listPostingan){
        PostinganRecycleViewAdapter adapter = new PostinganRecycleViewAdapter(getContext(), listPostingan, PostinganFragment.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
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
