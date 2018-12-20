package com.example.gusarisna.pratikum.data.model;

import com.google.gson.annotations.SerializedName;

public class Follower {

	@SerializedName("follower_id")
	private int followerId;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("user_followed")
	private User userFollowed;

	@SerializedName("user_id")
	private int userId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	public void setFollowerId(int followerId){
		this.followerId = followerId;
	}

	public int getFollowerId(){
		return followerId;
	}

	public void setUpdatedAt(String updatedAt){
		this.updatedAt = updatedAt;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public void setUserFollowed(User userFollowed){
		this.userFollowed = userFollowed;
	}

	public User getUserFollowed(){
		return userFollowed;
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

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	@Override
 	public String toString(){
		return 
			"Follower{" +
			"follower_id = '" + followerId + '\'' + 
			",updated_at = '" + updatedAt + '\'' + 
			",user_followed = '" + userFollowed + '\'' + 
			",user_id = '" + userId + '\'' + 
			",created_at = '" + createdAt + '\'' + 
			",id = '" + id + '\'' + 
			"}";
		}
}