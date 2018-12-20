package com.example.gusarisna.pratikum.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.adapter.ViewPagerAdapter;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.AuthRes;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;
import com.example.gusarisna.pratikum.fragment.NotifikasiFragment;
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
    String apiToken;
    User user;
    DatabaseHelper db;
    public static Activity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserAndToken();
        if(user.getId() == 0)
            logOutUser();
        setContentView(R.layout.home);
        ButterKnife.bind(this);

        setTitle("Profil");

        mAPIService = ApiUtils.getAPIService();
        db = new DatabaseHelper(this);
        mengirimAllUserReq();

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //add fragment
        viewPagerAdapter.addFragment(new ProfileFragment(), "Profil");
        viewPagerAdapter.addFragment(new PostinganFragment(), "Timeline");
        viewPagerAdapter.addFragment(new UserFragment(), "Pengguna");
        viewPagerAdapter.addFragment(new NotifikasiFragment(), "Notifikasi");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person);
        int tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.white);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_home);
        tabIconColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDarkest);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_people);
        tabLayout.getTabAt(2).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_notifications);
        tabLayout.getTabAt(3).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

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
                } else if(position == 2){
                    Home.this.setTitle("Pengguna");
                } else {
                    Home.this.setTitle("Notifikasi");
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        fa = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if(itemId == R.id.menu_pengaturan){
            Intent i = new Intent(this, Pengaturan.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            setUserAndToken();
        }
    }

    public void logOutUser(){
        clearPref();
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    public void clearPref(){
        SharedPreferences.Editor editor = userPrefs.edit();
        editor.putInt("userId", 0);
        editor.putString("userNama", "");
        editor.putString("userEmail", "");
        editor.putString("userFotoProfil", "");
        editor.putString("apiToken", "");
        editor.apply();
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

    public String getApiToken(){
        return apiToken;
    }

    public User getUser(){
        return user;
    }

    public DatabaseHelper getDatabaseHelper(){
        return db;
    }

    public void mengirimAllUserReq(){
        Log.d("DEVELOP", "INIT USER");
        mAPIService.getAllUser("Bearer "+apiToken).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()){
                    db.deleteAllUser();
                    db.addMultiUser(response.body());
                    Log.d("DEVELOP", "INIT USER :"+response.body().size());
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getBaseContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

}
