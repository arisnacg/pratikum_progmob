package com.example.gusarisna.pratikum.data.model;

import com.google.gson.annotations.SerializedName;


public class LikeRes{

	@SerializedName("pesan")
	private String pesan;

	@SerializedName("like_count")
	private int likeCount;

	@SerializedName("liked")
	private boolean liked;

	public void setPesan(String pesan){
		this.pesan = pesan;
	}

	public String getPesan(){
		return pesan;
	}

	public void setLikeCount(int likeCount){
		this.likeCount = likeCount;
	}

	public int getLikeCount(){
		return likeCount;
	}

	public void setLiked(boolean liked){
		this.liked = liked;
	}

	public boolean isLiked(){
		return liked;
	}

	@Override
 	public String toString(){
		return 
			"LikeRes{" + 
			"pesan = '" + pesan + '\'' + 
			",like_count = '" + likeCount + '\'' + 
			",liked = '" + liked + '\'' + 
			"}";
		}
}