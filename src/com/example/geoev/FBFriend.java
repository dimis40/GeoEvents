package com.example.geoev;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class FBFriend {
	private String name;
	private String profilePictureURL;
	private String uid;
	private Bitmap profilePicture = null;
	private boolean selected;
	
	
	public FBFriend(JSONObject json) throws JSONException {
        this.name = json.getString("name");
        this.profilePictureURL = json.getString("pic_square");
        this.uid = json.getString("uid");
	}


	public CharSequence getName() {
		return name;
	}
	
	public String getProfilePictureURL(){
		return profilePictureURL;
	}

	public boolean isSelected(){
		return selected;
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
	}

	public void setProfilePic(Bitmap b) {
		this.profilePicture = Bitmap.createScaledBitmap(b, Constants.size_Profile_Picture_Width, Constants.size_Profile_Picture_Heigth, true);
		
	}

	public boolean hasDownloadedProfilePicture() {
		return profilePicture != null;
	}


	public Bitmap getProfilePicture() {
		return profilePicture;
	}
	
	public String getUID(){
		return uid;
	}
	
	@Override
	public boolean equals(Object o) {
		return uid.equals(((FBFriend)o).uid);
	}
	
}

