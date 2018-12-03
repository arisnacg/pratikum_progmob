package com.example.gusarisna.pratikum.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.adapter.ViewPagerAdapter;
import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;
import com.example.gusarisna.pratikum.fragment.PostinganFragment;
import com.example.gusarisna.pratikum.fragment.ProfileFragment;
import com.example.gusarisna.pratikum.fragment.UserFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {


    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ViewPagerAdapter viewPagerAdapter;

    SharedPreferences userPrefs;
    APIService mAPIService;
    int userId;
    String apiToken;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPrefs = getSharedPreferences("user", Context.MODE_PRIVATE);

        if(!cekUser()){
            logOutUser();
        }
        setContentView(R.layout.home);
        ButterKnife.bind(this);

        setTitle("Profil");

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //add fragment
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profil");
        viewPagerAdapter.addFragment(new PostinganFragment(), "Postingan");
        viewPagerAdapter.addFragment(new UserFragment(), "Pengguna");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person);
        int tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.white);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_people);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                int position = tab.getPosition();
                if(position == 0){
                    Home.this.setTitle("Profil");
                } else if(position == 1) {
                    Home.this.setTitle("Beranda");
                } else {
                    Home.this.setTitle("Pengguna");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDarkest);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mAPIService = ApiUtils.getAPIService();
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public void btnLogoutClicked(){
        mAPIService.logoutUser("Bearer " + apiToken).enqueue(new Callback<AuthRes>() {
            @Override
            public void onResponse(Call<AuthRes> call, Response<AuthRes> response) {
                if(response.isSuccessful()){
                    logOutUser();
                } else {
                    Toast.makeText(getBaseContext(), "Logout gagal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthRes> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void logOutUser(){
        simpanPrefs(0, "");
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public boolean cekUser(){
        userId = userPrefs.getInt("userId", 0);
        apiToken = userPrefs.getString("apiToken", "");
        return (userId == 0)? false : true;
    }

    public void simpanPrefs(int userId, String apiToken){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt("userId", userId);
        editor.putString("apiToken", apiToken);
        editor.apply();
    }

    public String getApiToken(){
        return apiToken;
    }

    public User getUser(){
        return user;
    }

}
