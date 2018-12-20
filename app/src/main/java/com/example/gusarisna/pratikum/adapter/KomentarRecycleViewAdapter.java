package com.example.gusarisna.pratikum.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.Komentar;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class KomentarRecycleViewAdapter extends  RecyclerView.Adapter<KomentarRecycleViewAdapter.KomentarViewHolder>{

    Context context;
    List<Komentar> listKomentar;
    User user;
    String apiToken;
    String BASE_URL_IMAGE;

    public KomentarRecycleViewAdapter(Context context, User user, List<Komentar> listKomentar) {
        this.context = context;
        this.listKomentar = listKomentar;
        this.user = user;
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
    }

    @NonNull
    @Override
    public KomentarViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.komentar_item, viewGroup, false);
        KomentarViewHolder viewHolder = new KomentarViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull KomentarViewHolder komentarViewHolder, int i) {
        komentarViewHolder.tvUserNama.setText(listKomentar.get(i).getUser().getNama());
        komentarViewHolder.tvIsiKomentar.setText(listKomentar.get(i).getIsi());
        PrettyTime prettyTime = new PrettyTime(new Locale("id"));
        Date createdAt = null;
        try {
            createdAt = parseDate(listKomentar.get(i).getCreatedAt());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String waktu = prettyTime.format(createdAt);
        komentarViewHolder.tvWaktuKomentar.setText(waktu);
        String url = BASE_URL_IMAGE + listKomentar.get(i).getUser().getFotoProfil();
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.ic_launcher_round).centerCrop())
                .into(komentarViewHolder.fotoProfil);
    }

    @Override
    public int getItemCount() {
        return listKomentar.size();
    }

    public Date parseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        return sdf.parse(date);
    }


    public class KomentarViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        @BindView(R.id.komentar_user)
        TextView tvUserNama;
        @BindView(R.id.komentar_isi)
        TextView tvIsiKomentar;
        @BindView(R.id.komentar_created_at)
        TextView tvWaktuKomentar;
        @BindView(R.id.layout_komentar)
        LinearLayout layoutKomentar;
        @BindView(R.id.komentar_user_fotoprofil)
        CircleImageView fotoProfil;

        public KomentarViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            layoutKomentar.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            if(listKomentar.get(getAdapterPosition()).getUserId() == user.getId()){
                menu.add(getAdapterPosition(), 121, 0, "Hapus");
//                menu.add(getAdapterPosition(), 122, 0, "Hapus");
            }
        }
    }

    public void hapusKomentar(int position){
        listKomentar.remove(position);
        notifyDataSetChanged();
    }

}
