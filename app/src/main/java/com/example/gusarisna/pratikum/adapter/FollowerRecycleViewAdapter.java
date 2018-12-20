package com.example.gusarisna.pratikum.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;
import com.example.gusarisna.pratikum.fragment.ProfileFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowerRecycleViewAdapter extends RecyclerView.Adapter<FollowerRecycleViewAdapter.FollowerViewHolder> {

    Context mContext;
    List<User> listUser;
    User user;
    ProfileFragment fragment;
    String BASE_URL_IMAGE;
    APIService mAPIService;
    SharedPreferences userPrefs;
    String apiToken;
    DatabaseHelper db;

    public FollowerRecycleViewAdapter(Context mContext, List<User> listUser, User user, ProfileFragment fragment) {
        this.mContext = mContext;
        this.listUser = listUser;
        this.user = user;
        this.fragment = fragment;
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
        db = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public FollowerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        FollowerViewHolder viewHolder = new FollowerRecycleViewAdapter.FollowerViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FollowerViewHolder followerViewHolder, int i) {
        followerViewHolder.tvNama.setText(listUser.get(i).getNama());
        followerViewHolder.tvEmail.setText(listUser.get(i).getEmail());
        String url = BASE_URL_IMAGE + listUser.get(i).getFotoProfil();
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher_round).centerCrop())
                .into(followerViewHolder.ivFotoProfil);
        User curUser = listUser.get(i);
        if(curUser.getId() == user.getId()){
            followerViewHolder.btnFollow.setVisibility(View.GONE);
        } else {
            if(curUser.isFollowed()){
                int color = ContextCompat.getColor(mContext, R.color.colorSecondary);
                followerViewHolder.btnFollow.setBackgroundColor(color);
                followerViewHolder.btnFollow.setImageResource(R.drawable.ic_person_outline);
            } else {
                int color = ContextCompat.getColor(mContext, R.color.colorPrimary);
                followerViewHolder.btnFollow.setBackgroundColor(color);
                followerViewHolder.btnFollow.setImageResource(R.drawable.ic_person_add);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class FollowerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pengguna_nama)
        TextView tvNama;
        @BindView(R.id.pengguna_email)
        TextView tvEmail;
        @BindView(R.id.pengguna_image)
        CircleImageView ivFotoProfil;
        @BindView(R.id.btn_follow)
        ImageButton btnFollow;


        public FollowerViewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            userPrefs = itemView.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            apiToken = userPrefs.getString("apiToken", "");
            mAPIService = ApiUtils.getAPIService();
        }

        @OnClick(R.id.btn_follow)
        public void followToggle(View v){
            mAPIService.toggleFollow("Bearer "+ apiToken, listUser.get(getAdapterPosition()).getId()).enqueue(new Callback<BasicRes>() {
                @Override
                public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                    if(response.isSuccessful()) {
                        if (response.body().isStatus()) {
                            int color = ContextCompat.getColor(mContext, R.color.colorSecondary);
                            btnFollow.setBackgroundColor(color);
                            btnFollow.setImageResource(R.drawable.ic_person_outline);
                        } else {
                            int color = ContextCompat.getColor(mContext, R.color.colorPrimary);
                            btnFollow.setBackgroundColor(color);
                            btnFollow.setImageResource(R.drawable.ic_person_add);

                        }
                        Toast.makeText(mContext, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, response.message().toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BasicRes> call, Throwable t) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
