package com.example.gusarisna.pratikum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.User;

import java.util.List;

public class UserRecycleViewAdapter extends RecyclerView.Adapter<UserRecycleViewAdapter.UserViewHolder> {

    Context mContext;
    List<User> listUser;

    public UserRecycleViewAdapter(Context mContext, List<User> listUser) {
        this.mContext = mContext;
        this.listUser = listUser;
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
    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public static class UserViewHolder extends  RecyclerView.ViewHolder {

        TextView tvNama;
        TextView tvEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = (TextView) itemView.findViewById(R.id.pengguna_nama);
            tvEmail = (TextView) itemView.findViewById(R.id.pengguna_email);
        }
    }

}
