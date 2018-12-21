package com.example.gusarisna.pratikum.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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
import com.example.gusarisna.pratikum.adapter.NotifRecycleViewAdapter;
import com.example.gusarisna.pratikum.adapter.PostinganRecycleViewAdapter;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.Notif;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import java.util.List;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifikasiFragment extends Fragment {

    View v;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout swipeLayout;
    List<Notif> listNotif;
    APIService mAPIService;
    CoordinatorLayout coordinatorLayout;
    SharedPreferences userPrefs;
    User user;
    String apiToken;
    DatabaseHelper db;
    private static final String LOG = "DEVELOP";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.notif_fragment, container, false);
        coordinatorLayout = (CoordinatorLayout) v.findViewById(R.id.notif_coordinator_layout);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.notif_recycle_view);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        refreshNotif();
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotif();
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

    public void refreshNotif(){
        Log.d(LOG, "Request API Postingan");
        swipeLayout.setRefreshing(true);
        mAPIService.getNotif("Bearer "+apiToken).enqueue(new Callback<List<Notif>>() {
            @Override
            public void onResponse(Call<List<Notif>> call, Response<List<Notif>> response) {
                swipeLayout.setRefreshing(false);
                if(response.isSuccessful()){
                    listNotif = response.body();
                    tampilkanSemuaPostingan(listNotif);
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notif>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void tampilkanSemuaPostingan(List<Notif> listNotif){
        NotifRecycleViewAdapter adapter = new NotifRecycleViewAdapter(getContext(), listNotif);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(adapter);
    }
}
