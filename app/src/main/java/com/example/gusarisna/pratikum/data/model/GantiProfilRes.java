package com.example.gusarisna.pratikum.data.model;

import com.google.gson.annotations.SerializedName;

public class GantiProfilRes{

    @SerializedName("pesan")
    private String pesan;

    @SerializedName("status")
    private boolean status;

    @SerializedName("foto_profil")
    private String fotoProfil;

    public void setPesan(String pesan){
        this.pesan = pesan;
    }

    public String getPesan(){
        return pesan;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean isStatus(){
        return status;
    }

    public String getFotoProfil() {
        return fotoProfil;
    }

    public void setFotoProfil(String fotoProfil) {
        this.fotoProfil = fotoProfil;
    }

    @Override
    public String toString(){
        return
                "BasicRes{" +
                        "pesan = '" + pesan + '\'' +
                        ",status = '" + status + '\'' +
                        "}";
    }
}
