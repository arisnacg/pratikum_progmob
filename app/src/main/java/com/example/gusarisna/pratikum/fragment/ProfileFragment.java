package com.example.gusarisna.pratikum.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.activity.Home;
import com.example.gusarisna.pratikum.adapter.FollowerRecycleViewAdapter;
import com.example.gusarisna.pratikum.adapter.PostinganRecycleViewAdapter;
import com.example.gusarisna.pratikum.adapter.UserPostinganRecyclerViewAdapter;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.Follower;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    View v;
    User user;
    String apiToken;
    SharedPreferences userPrefs;
    APIService mAPIService;
    final String LOG = "DEVELOP";

    TextView tvNama;
    TextView tvEmail;
    RecyclerView rvPostingan;
    TextView tvToggleFollower;
    TextView tvTogglePostingan;
    LinearLayout llFollower;
    LinearLayout llPostingan;
    RecyclerView rvFollower;
    CircleImageView ivFotoProfil;
    DatabaseHelper db;
    List<Postingan> userPostingan;
    List<User> listFollower;

    Boolean toggleFollower = true;
    Boolean togglePostingan = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ((Home)getActivity()).getUser();
        apiToken = ((Home)getActivity()).getApiToken();
        mAPIService = ApiUtils.getAPIService();
        this.userPrefs = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        db = ((Home)getActivity()).getDatabaseHelper();
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfile();
        refreshFollower();
        refreshUserPostingan();
    }

    @OnClick(R.id.collapse_follower)
    public void toggleCollapseFollower(){
        if(toggleFollower){
            llFollower.setVisibility(View.GONE);
            toggleFollower = false;
        } else {
            llFollower.setVisibility(View.VISIBLE);
            toggleFollower = true;
        }
    }

    @OnClick(R.id.collapse_postingan)
    public void toggleCollapsePostingan(){
        if(togglePostingan){
            llPostingan.setVisibility(View.GONE);
            togglePostingan = false;
        } else {
            llPostingan.setVisibility(View.VISIBLE);
            togglePostingan = true;
        }
    }


    public void refreshUserPostingan(){
        mAPIService.getUserPostingan("Bearer "+apiToken).enqueue(new Callback<List<Postingan>>() {
            @Override
            public void onResponse(Call<List<Postingan>> call, Response<List<Postingan>> response) {
                if(response.isSuccessful()){
                    Log.d(LOG, "API User Postingan Respone OK");
                    db.deleteAllPostingan();
                    db.addMultiPostingaan(response.body());
                    userPostingan = response.body();
                } else {
                    Log.d(LOG, "API Postingan Respone NOT OK");
                    userPostingan = db.getAllPostingan();
                }
                UserPostinganRecyclerViewAdapter adapter = new UserPostinganRecyclerViewAdapter(getContext(), userPostingan, ProfileFragment.this);
                rvPostingan.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvPostingan.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Postingan>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void refreshFollower(){
        mAPIService.getFollower("Bearer " + apiToken).enqueue(new Callback<List<Follower>>() {
            @Override
            public void onResponse(Call<List<Follower>> call, Response<List<Follower>> response) {
                if(response.isSuccessful()){
                    Log.d(LOG, "API User OK");
                    db.clearFollower();
                    db.addMultiFollower(response.body());
                } else {
                    Log.d(LOG, "API Follower NOT OK");
                }
                listFollower = db.getFollowerUser();
                FollowerRecycleViewAdapter adapter = new FollowerRecycleViewAdapter(
                        getContext(),
                        listFollower,
                        user,
                        ProfileFragment.this
                );
                rvFollower.setLayoutManager(new LinearLayoutManager(getActivity()));
                rvFollower.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Follower>> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, v);
        tvNama = (TextView) v.findViewById(R.id.user_nama);
        tvEmail = (TextView) v.findViewById(R.id.user_email);
        ivFotoProfil = (CircleImageView) v.findViewById(R.id.user_fotoprofil);
        rvPostingan = (RecyclerView) v.findViewById(R.id.recyclerview_postingan);
        rvFollower = (RecyclerView) v.findViewById(R.id.recyclerview_follower);
        tvToggleFollower = (TextView) v.findViewById(R.id.collapse_follower);
        tvTogglePostingan = (TextView) v.findViewById(R.id.collapse_postingan);
        llFollower = (LinearLayout) v.findViewById(R.id.ll_follower);
        llPostingan = (LinearLayout) v.findViewById(R.id.ll_postingan);
        return v;
    }

    public void setProfile(){
        user.setNama(userPrefs.getString("userNama", ""));
        user.setEmail(userPrefs.getString("userEmail", ""));
        user.setFotoProfil(userPrefs.getString("userFotoProfil", ""));
        tvNama.setText(user.getNama());
        tvEmail.setText(user.getEmail());
        String urlFotoProfil = ApiUtils.BASE_URL+"foto_profil/"+user.getFotoProfil();
        if(!user.getFotoProfil().equals("default.png")) {
            Glide.with(v)
                    .load(urlFotoProfil)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_user_round).centerCrop())
                    .into(ivFotoProfil);
        }
    }

}
