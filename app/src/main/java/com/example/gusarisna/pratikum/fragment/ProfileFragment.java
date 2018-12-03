package com.example.gusarisna.pratikum.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.activity.Home;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    View v;
    User user;
    SharedPreferences userPrefs;
    APIService mAPIService;
    int userId;
    String apiToken;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_fragment, container, false);
        return v;
    }

    public void getUserAPI(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Memproses");
        progressDialog.setMessage("Mohon untuk menunggu");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAPIService.getUser("Bearer " + apiToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                progressDialog.dismiss();
                if(response.isSuccessful()){
                    user = response.body();
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                    //((Home)getActivity()).logOutUser();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getContext(), "Terjadi kesalahan", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean cekUser(){
        userId = userPrefs.getInt("userId", 0);
        apiToken = userPrefs.getString("apiToken", "");
        return (userId == 0)? false : true;
    }

}
