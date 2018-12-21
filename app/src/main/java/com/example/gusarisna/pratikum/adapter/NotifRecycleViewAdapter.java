package com.example.gusarisna.pratikum.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gusarisna.pratikum.R;
import com.example.gusarisna.pratikum.data.model.Komentar;
import com.example.gusarisna.pratikum.data.model.Notif;
import com.example.gusarisna.pratikum.data.model.User;
import com.example.gusarisna.pratikum.data.remote.ApiUtils;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotifRecycleViewAdapter extends RecyclerView.Adapter<NotifRecycleViewAdapter.NotifViewHolder> {

    Context context;
    List<Notif> listNotif;
    User user;
    String apiToken;
    String BASE_URL_IMAGE;

    public NotifRecycleViewAdapter(Context context, List<Notif> listNotif) {
        this.context = context;
        this.listNotif = listNotif;
        BASE_URL_IMAGE = ApiUtils.BASE_URL + "foto_profil/";
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.notif_item, viewGroup, false);
        return new NotifViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder viewHolder, int i) {
        if(!listNotif.get(i).getFrom().getFotoProfil().equals("default.png")){
            String url = BASE_URL_IMAGE + listNotif.get(i).getFrom().getFotoProfil();
            Glide.with(context)
                    .load(url)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.ic_user_round).centerCrop())
                    .into(viewHolder.fromUser);
        }
        viewHolder.isiNotif.setText(listNotif.get(i).getIsi());
    }

    @Override
    public int getItemCount() {
        return listNotif.size();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class NotifViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.from_user_notif)
        CircleImageView fromUser;
        @BindView(R.id.isi_notif)
        TextView isiNotif;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
