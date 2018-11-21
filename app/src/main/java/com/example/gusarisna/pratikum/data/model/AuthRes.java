package com.example.gusarisna.pratikum.data.model;


import com.google.gson.annotations.SerializedName;


public class AuthRes {

	@SerializedName("pesan")
	private String pesan;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("api_token")
	private String apiToken;

	@SerializedName("status")
	private boolean status;

	public void setPesan(String pesan){
		this.pesan = pesan;
	}

	public String getPesan(){
		return pesan;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public int getUserId(){
		return userId;
	}

	public void setApiToken(String apiToken){
		this.apiToken = apiToken;
	}

	public String getApiToken(){
		return apiToken;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"AuthRes{" + 
			"pesan = '" + pesan + '\'' + 
			",user_id = '" + userId + '\'' + 
			",api_token = '" + apiToken + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}