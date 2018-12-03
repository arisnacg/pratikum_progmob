package com.example.gusarisna.pratikum.data.model;

import com.google.gson.annotations.SerializedName;


public class Postingan{

	@SerializedName("like_count")
	private int likeCount;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("konten")
	private String konten;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("komentar_count")
	private int komentarCount;

	@SerializedName("user")
	private User user;

	@SerializedName("liked")
	private boolean liked;

	public void setLikeCount(int likeCount){
		this.likeCount = likeCount;
	}

	public int getLikeCount(){
		return likeCount;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setKonten(String konten){
		this.konten = konten;
	}

	public String getKonten(){
		return konten;
	}

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setKomentarCount(int komentarCount){
		this.komentarCount = komentarCount;
	}

	public int getKomentarCount(){
		return komentarCount;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
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
			"Postingan{" + 
			"like_count = '" + likeCount + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",user_id = '" + userId + '\'' + 
			",konten = '" + konten + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			",komentar_count = '" + komentarCount + '\'' + 
			",user = '" + user + '\'' + 
			",liked = '" + liked + '\'' + 
			"}";
		}
}