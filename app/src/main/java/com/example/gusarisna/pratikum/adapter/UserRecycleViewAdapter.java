package com.example.gusarisna.pratikum.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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
import com.example.gusarisna.pratikum.data.model.Follower;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRecycleViewAdapter extends RecyclerView.Adapter<UserRecycleViewAdapter.UserViewHolder> {

    Context mContext;
    List<User> listUser;
    User user;
    String BASE_URL_IMAGE;
    APIService mAPIService;
    SharedPreferences userPrefs;
    String apiToken;
    DatabaseHelper db;

    public UserRecycleViewAdapter(Context mContext, User user, List<User> listUser) {
        this.mContext = mContext;
        this.listUser = listUser;
        this.user = user;
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
        db = new DatabaseHelper(mContext);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.user_item, viewGroup, false);
        UserViewHolder userViewHolder = new UserViewHolder(v);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i) {
        userViewHolder.tvNama.setText(listUser.get(i).getNama());
        userViewHolder.tvEmail.setText(listUser.get(i).getEmail());
        String url = BASE_URL_IMAGE + listUser.get(i).getFotoProfil();
        Glide.with(mContext)
            .load(url)
            .apply(new RequestOptions()
                    .placeholder(R.mipmap.ic_user_round).centerCrop())
            .into(userViewHolder.ivFotoProfil);
        User curUser = listUser.get(i);
        if(curUser.getId() == user.getId()){
            userViewHolder.btnFollow.setVisibility(View.GONE);
        } else {
            if(curUser.isFollowed()){
                int color = ContextCompat.getColor(mContext, R.color.colorSecondary);
                userViewHolder.btnFollow.setBackgroundColor(color);
                userViewHolder.btnFollow.setImageResource(R.drawable.ic_person_outline);
            } else {
                int color = ContextCompat.getColor(mContext, R.color.colorPrimary);
                userViewHolder.btnFollow.setBackgroundColor(color);
                userViewHolder.btnFollow.setImageResource(R.drawable.ic_person_add);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class UserViewHolder extends  RecyclerView.ViewHolder {

        TextView tvNama;
        TextView tvEmail;
        CircleImageView ivFotoProfil;
        ImageButton btnFollow;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            userPrefs = itemView.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            apiToken = userPrefs.getString("apiToken", "");
            mAPIService = ApiUtils.getAPIService();
            tvNama = (TextView) itemView.findViewById(R.id.pengguna_nama);
            tvEmail = (TextView) itemView.findViewById(R.id.pengguna_email);
            ivFotoProfil = (CircleImageView) itemView.findViewById(R.id.pengguna_image);
            btnFollow = (ImageButton) itemView.findViewById(R.id.btn_follow);
        }

        @OnClick(R.id.btn_follow)
        public void followToggle(View v){
            mAPIService.toggleFollow("Bearer "+ apiToken, listUser.get(getAdapterPosition()).getId()).enqueue(new Callback<BasicRes>() {
                @Override
                public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                    if(response.body().isStatus()){
                        int color = ContextCompat.getColor(mContext, R.color.colorSecondary);
                        btnFollow.setBackgroundColor(color);
                        btnFollow.setImageResource(R.drawable.ic_person_outline);
                    } else {
                        int color = ContextCompat.getColor(mContext, R.color.colorPrimary);
                        btnFollow.setBackgroundColor(color);
                        btnFollow.setImageResource(R.drawable.ic_person_add);
                    }
                    Toast.makeText(mContext, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<BasicRes> call, Throwable t) {
                    Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
