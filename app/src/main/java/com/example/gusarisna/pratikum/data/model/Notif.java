package com.example.gusarisna.pratikum.data.model;

import com.google.gson.annotations.SerializedName;

public class Notif {

	@SerializedName("from_id")
	private int fromId;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("from")
	private From from;

	@SerializedName("id")
	private int id;

	@SerializedName("user")
	private User user;

	@SerializedName("isi")
	private String isi;

	public void setFromId(int fromId){
		this.fromId = fromId;
	}

	public int getFromId(){
		return fromId;
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

	public void setCreatedAt(String createdAt){
		this.createdAt = createdAt;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public void setFrom(From from){
		this.from = from;
	}

	public From getFrom(){
		return from;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setUser(User user){
		this.user = user;
	}

	public User getUser(){
		return user;
	}

	public void setIsi(String isi){
		this.isi = isi;
	}

	public String getIsi(){
		return isi;
	}

	@Override
 	public String toString(){
		return 
			"Notif{" +
			"from_id = '" + fromId + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",user_id = '" + userId + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",from = '" + from + '\'' + 
			",id = '" + id + '\'' + 
			",user = '" + user + '\'' + 
			",isi = '" + isi + '\'' + 
			"}";
		}
}