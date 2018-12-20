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
import com.example.gusarisna.pratikum.activity.Home;
import com.example.gusarisna.pratikum.adapter.UserRecycleViewAdapter;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
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
    DatabaseHelper db;
    User user;
    String apiToken;
    private static final String LOG = "DEVELOP";



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.v = inflater.inflate(R.layout.user_fragment, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.user_coordinator_layout);
        return this.v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mengirimAllUserReq();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPIService = ApiUtils.getAPIService();
        db = ((Home)getActivity()).getDatabaseHelper();
        user = ((Home)getActivity()).getUser();
        apiToken = ((Home)getActivity()).getApiToken();
    }

    public void mengirimAllUserReq(){
        Log.d(LOG, "Request API User");
        mAPIService.getAllUser("Bearer "+apiToken).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    Log.d(LOG, "API User OK");
                    db.deleteAllUser();
                    db.addMultiUser(response.body());
                    listUser = response.body();
                } else {
                    Log.d(LOG, "API User NOT OK");
                    listUser = db.getAllUser();
                }
                tampilkanSemuaUser(listUser);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(LOG, "API User Gagal");
                Snackbar snackbar = Snackbar.make(
                        coordinatorLayout,
                        t.getMessage(),
                        Snackbar.LENGTH_LONG
                ).setAction("Coba Lagi", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mengirimAllUserReq();
                    }
                });
                snackbar.show();
                listUser = db.getAllUser();
                tampilkanSemuaUser(listUser);
            }

        });
    }

    public void tampilkanSemuaUser(List<User> listUser){
        mRecyclerView = (RecyclerView) v.findViewById(R.id.user_recycler_view);
        UserRecycleViewAdapter adapter = new UserRecycleViewAdapter(getContext(), user, listUser);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }
}
