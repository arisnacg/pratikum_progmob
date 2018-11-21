package com.example.gusarisna.pratikum.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.adapter.UserRecycleViewAdapter;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {
    View v;
    RecyclerView mRecyclerView;
    List<User> listUser;
    APIService mAPIService;
    CoordinatorLayout coordinatorLayout;

    public UserFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.v = inflater.inflate(R.layout.user_fragment, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.user_coordinator_layout);
        mengirimAllUserReq();
        return this.v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPIService = ApiUtils.getAPIService();
    }

    public void mengirimAllUserReq(){
        mAPIService.getAllUser().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    listUser = response.body();
                    tampilkanSemuaUser(listUser);
                } else {
                    tampilkanSemuaUser(Collections.<User>emptyList());
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Snackbar snackbar = Snackbar.make(
                        coordinatorLayout,
                        "Terjadi kesalahan jaringan",
                        Snackbar.LENGTH_LONG
                ).setAction("Coba Lagi", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mengirimAllUserReq();
                    }
                });
                snackbar.show();
                tampilkanSemuaUser(Collections.<User>emptyList());
            }
        });
    }

    public void tampilkanSemuaUser(List<User> listUser){
        mRecyclerView = (RecyclerView) v.findViewById(R.id.user_recycler_view);
        UserRecycleViewAdapter adapter = new UserRecycleViewAdapter(getContext(), listUser);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }
}
