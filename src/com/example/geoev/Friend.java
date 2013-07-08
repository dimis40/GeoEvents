package com.example.geoev;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class Friend {
	private String name;
	private String profilePictureURL;
	private String uid;
	private Bitmap profilePicture = null;


	public Friend(JSONObject json) throws JSONException {
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

   // set profile picture to 80X80
	public void setProfilePic(Bitmap b) {
		this.profilePicture = Bitmap.createScaledBitmap(b, 80, 80, true);

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
		return uid.equals(((Friend)o).uid);
	}

}
