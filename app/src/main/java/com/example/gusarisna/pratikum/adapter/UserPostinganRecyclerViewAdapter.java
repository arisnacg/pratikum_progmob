package com.example.gusarisna.pratikum.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.activity.EditPostingan;
import com.example.gusarisna.pratikum.activity.TambahKomentar;
import com.example.gusarisna.pratikum.data.database.DatabaseHelper;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.LikeRes;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;
import com.example.gusarisna.pratikum.fragment.PostinganFragment;
import com.example.gusarisna.pratikum.fragment.ProfileFragment;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPostinganRecyclerViewAdapter extends RecyclerView.Adapter<UserPostinganRecyclerViewAdapter.UserPostinganViewHolder>{

    Context mContext;
    ProfileFragment fragment;
    APIService mAPIService;
    List<Postingan> listPostingan;
    SharedPreferences userPrefs;
    int userId;
    String apiToken;
    DatabaseHelper db;
    String BASE_URL_IMAGE;

    public UserPostinganRecyclerViewAdapter(Context context, List<Postingan> listPostingan, ProfileFragment fragment) {
        this.fragment = fragment;
        this.mContext = context;
        this.listPostingan = listPostingan;
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
    }

    @NonNull
    @Override
    public UserPostinganViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.postingan_item, viewGroup, false);
        UserPostinganViewHolder viewHolder = new UserPostinganViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostinganViewHolder userPostinganViewHolder, int i) {
        Log.d("DEVELOP", "Postingan : "+listPostingan.get(i).getKonten());
        userPostinganViewHolder.tvNama.setText(listPostingan.get(i).getUser().getNama());
        PrettyTime prettyTime = new PrettyTime(new Locale("id"));
        Date createdAt = null;
        try {
            createdAt = parseDate(listPostingan.get(i).getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String waktuPost = prettyTime.format(createdAt);
        userPostinganViewHolder.tvWaktu.setText(waktuPost);
        userPostinganViewHolder.tvKonten.setText(listPostingan.get(i).getKonten());
        userPostinganViewHolder.tvLike.setText(listPostingan.get(i).getLikeCount()+"");
        userPostinganViewHolder.tvKomentar.setText(listPostingan.get(i).getKomentarCount()+"");
        if(listPostingan.get(i).isLiked())
            userPostinganViewHolder.btnLike.setColorFilter(Color.parseColor("#e24646"));
        if(listPostingan.get(i).getUserId() != userId)
            userPostinganViewHolder.menuPostingan.setVisibility(LinearLayout.GONE);
        if(!listPostingan.get(i).getUser().getFotoProfil().equals("default.png")){
            String url = BASE_URL_IMAGE + listPostingan.get(i).getUser().getFotoProfil();
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_launcher_round).centerCrop())
                    .into(userPostinganViewHolder.ivUser);
        }
    }

    @Override
    public int getItemCount() {
        return listPostingan.size();
    }

    public Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        return sdf.parse(date);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //View Holder
    public class UserPostinganViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.postingan_user_nama)
        TextView tvNama;
        @BindView(R.id.postingan_created_at)
        TextView tvWaktu;
        @BindView(R.id.postingan_konten)
        TextView tvKonten;
        @BindView(R.id.postingan_like)
        TextView tvLike;
        @BindView(R.id.postingan_komentar)
        TextView tvKomentar;
        @BindView(R.id.btn_like)
        ImageButton btnLike;
        @BindView(R.id.menu_postingan)
        LinearLayout menuPostingan;
        @BindView(R.id.btn_edit_post)
        ImageButton btnEdit;
        @BindView(R.id.btn_hapus_post)
        ImageButton btnHapus;
        @BindView(R.id.postingan_user)
        CircleImageView ivUser;

        public UserPostinganViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mAPIService = ApiUtils.getAPIService();
            userPrefs = itemView.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            getUser();
            db = new DatabaseHelper(itemView.getContext());
        }

        @OnClick(R.id.btn_hapus_post)
        public void hapusPostingan(View v){

            final Context context = v.getContext();
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Memproses");
            progressDialog.setMessage("Mohon untuk menunggu");
            progressDialog.setCancelable(false);
            progressDialog.show();
            mAPIService.hapusPostingan("Bearer " + apiToken, listPostingan.get(getAdapterPosition()).getId())
                    .enqueue(new Callback<BasicRes>() {
                        @Override
                        public void onResponse(Call<BasicRes> call, Response<BasicRes> response) {
                            if(response.isSuccessful()){
                                if(response.body().isStatus()){
                                    Toast.makeText(context, response.body().getPesan(), Toast.LENGTH_SHORT).show();
                                    db.deletePostingan(listPostingan.get(getAdapterPosition()).getId());
                                    fragment.refreshUserPostingan();
                                }
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<BasicRes> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Terjadi Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @OnClick(R.id.btn_edit_post)
        public void editPostingan(View v){
            final Context context = v.getContext();
            Intent i = new Intent(context, EditPostingan.class);
            i.putExtra("POSTINGAN_ID", listPostingan.get(getAdapterPosition()).getId());
            i.putExtra("POSTINGAN_KONTEN", listPostingan.get(getAdapterPosition()).getKonten());
            context.startActivity(i);
        }

        @OnClick(R.id.btn_like)
        public void btnLikeToggle(View v){
            final Context context = v.getContext();
            mAPIService.toggleLike("Bearer " + apiToken, listPostingan.get(getAdapterPosition()).getId())
                    .enqueue(new Callback<LikeRes>() {
                        @Override
                        public void onResponse(Call<LikeRes> call, Response<LikeRes> response) {
                            if(response.isSuccessful()){
                                String color = "#cccccc";
                                if(response.body().isLiked()){
                                    color = "#e24646";
                                }
                                btnLike.setColorFilter(Color.parseColor(color));
                                listPostingan.get(getAdapterPosition()).setLikeCount(response.body().getLikeCount());
                                listPostingan.get(getAdapterPosition()).setLiked(response.body().isLiked());
                                db.updateLike(listPostingan.get(getAdapterPosition()));
                                tvLike.setText(response.body().getLikeCount()+"");
                            }
                        }

                        @Override
                        public void onFailure(Call<LikeRes> call, Throwable t) {
                            Toast.makeText(context, "Terjadi Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        @OnClick(R.id.btn_komentar)
        public void btnKomentarClicked(View v){
            Intent i = new Intent(mContext, TambahKomentar.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("POSTINGAN_ID", listPostingan.get(getAdapterPosition()).getId());
            mContext.startActivity(i);
        }

        public boolean getUser(){
            userId = userPrefs.getInt("userId", 0);
            apiToken = userPrefs.getString("apiToken", "");
            return (userId == 0)? false : true;
        }

    }

}