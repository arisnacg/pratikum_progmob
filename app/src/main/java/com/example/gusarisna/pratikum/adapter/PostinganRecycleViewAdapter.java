package com.example.gusarisna.pratikum.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import  com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.activity.EditPostingan;
import com.example.gusarisna.pratikum.data.model.BasicRes;
import com.example.gusarisna.pratikum.data.model.LikeRes;
import com.example.gusarisna.pratikum.data.model.Postingan;
import com.example.gusarisna.pratikum.data.remote.APIService;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;
import com.example.gusarisna.pratikum.fragment.PostinganFragment;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostinganRecycleViewAdapter extends RecyclerView.Adapter<PostinganRecycleViewAdapter.PostinganViewHolder> {

    Context mContext;
    PostinganFragment postinganFragment;
    APIService mAPIService;
    List<Postingan> listPostingan;
    SharedPreferences userPrefs;
    int userId;
    String apiToken;

    public PostinganRecycleViewAdapter(Context context, List<Postingan> listPostingan, PostinganFragment postinganFragment) {
        this.mContext = context;
        this.listPostingan = listPostingan;
        this.postinganFragment = postinganFragment;
    }

    @NonNull
    @Override
    public PostinganViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.postingan_item, viewGroup, false);
        PostinganViewHolder postinganViewHolder = new PostinganRecycleViewAdapter.PostinganViewHolder(v);
        return postinganViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostinganViewHolder postinganViewHolder, int i) {
        postinganViewHolder.tvNama.setText(listPostingan.get(i).getUser().getNama());
        PrettyTime prettyTime = new PrettyTime(new Locale("id"));
        Date createdAt = null;
        try {
            createdAt = parseDate(listPostingan.get(i).getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String waktuPost = prettyTime.format(createdAt);
        postinganViewHolder.tvWaktu.setText(waktuPost);
        postinganViewHolder.tvKonten.setText(listPostingan.get(i).getKonten());
        postinganViewHolder.tvLike.setText(listPostingan.get(i).getLikeCount()+"");
        postinganViewHolder.tvKomentar.setText(listPostingan.get(i).getKomentarCount()+"");
        if(listPostingan.get(i).isLiked())
            postinganViewHolder.btnLike.setColorFilter(Color.parseColor("#e24646"));
        if(listPostingan.get(i).getUserId() != userId)
            postinganViewHolder.menuPostingan.setVisibility(LinearLayout.GONE);

    }

    @Override
    public int getItemCount() {
        return listPostingan.size();
    }

    public class PostinganViewHolder extends RecyclerView.ViewHolder {
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


        public PostinganViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mAPIService = ApiUtils.getAPIService();
            userPrefs = itemView.getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
            getUser();
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
                                tvLike.setText(response.body().getLikeCount()+"");
                            }
                        }

                        @Override
                        public void onFailure(Call<LikeRes> call, Throwable t) {
                            Toast.makeText(context, "Terjadi Kesalahan Jaringan", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                                    Snackbar.make(postinganFragment.getCoordinatorLayout(), response.body().getPesan(), Snackbar.LENGTH_SHORT).show();
                                    postinganFragment.refreshPostingan();
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

    }

    public Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        return sdf.parse(date);
    }

    public boolean getUser(){
        userId = userPrefs.getInt("userId", 0);
        apiToken = userPrefs.getString("apiToken", "");
        return (userId == 0)? false : true;
    }

}
