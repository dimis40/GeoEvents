package com.example.geoev;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class FriendListAdapter extends ArrayAdapter<FBFriend> implements SectionIndexer {
	private final Context context;
	ArrayList<FBFriend> friends;

	//Fastscoll variables
	HashMap<Character, Integer> letterIndex;
	Character[] sections;
	
	//Picture for unknown people
	Bitmap mr_unknown;
	
	//
	SparseBooleanArray mSparseBooleanArray;

	public FriendListAdapter(Context context, ArrayList<FBFriend> friends) {
		super(context, R.layout.list_item_friend, friends);
		this.context = context;
		//
		//friends = new ArrayList<FBFriend>();
		this.friends = friends;
		
		Resources r = this.getContext().getResources();
		Bitmap b = BitmapFactory.decodeResource(r, R.drawable.mr_unknown);
		mr_unknown = Bitmap.createScaledBitmap(b, Constants.size_Profile_Picture_Width, Constants.size_Profile_Picture_Heigth, true);
		
		//
		mSparseBooleanArray = new SparseBooleanArray();
		createKeyIndex();
	}
	
	//
	public ArrayList<FBFriend> getCheckedItems() {


		  ArrayList<FBFriend> mTempArry = new ArrayList<FBFriend>();


		  for(int i=0;i<friends.size();i++) {

		   if(mSparseBooleanArray.get(i)) {

		    mTempArry.add(friends.get(i));

		   }

		  }

		  return mTempArry;

		 }
	
	public int getCount() {

		  // TODO Auto-generated method stub

		  return friends.size();

		 }
	@Override


	 public FBFriend getItem(int position) {

	  // TODO Auto-generated method stub

	  return friends.get(position);

	 }




	 @Override


	 public long getItemId(int position) {


	  // TODO Auto-generated method stub


	  return position;


	 }


	public void createKeyIndex(){
		letterIndex = new HashMap<Character, Integer>(); 

		for (int i = friends.size()-1; i >0; i--) {
			letterIndex.put(friends.get(i).getName().charAt(0), i); 
		}
		
		sections = letterIndex.keySet().toArray(new Character[letterIndex.size()]);
		
		Arrays.sort(sections);
	}

	
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		createKeyIndex();
	}

	//For view recycling (optimization for listview)
	static class ViewHolder {
		public TextView name;
		public ImageView profilePicture;
		public CheckBox chk;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View layout = convertView;
		//
		ViewHolder viewHolder = null;
		if(convertView == null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = inflater.inflate(R.layout.list_item_friend, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) layout.findViewById(R.id.real_life_name);
			viewHolder.profilePicture = (ImageView) layout.findViewById(R.id.profile_picture);
			viewHolder.chk = (CheckBox) layout.findViewById(R.id.chkfriend);
			/*viewHolder.chk
			.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					FBFriend friend = (FBFriend) viewHolder.chk
							.getTag();
					friend.setSelected(buttonView.isChecked());
					//int getPosition = (Integer) view.getTag();
					//friends.get(getPosition).setSelected(view.isChecked());

				}
			});*/
			
			
			viewHolder.chk.setOnClickListener( new View.OnClickListener() {  
			     public void onClick(View v) {  
			         CheckBox cb = (CheckBox) v ;  
			         FBFriend friend = (FBFriend) cb.getTag();  
			         Log.v("selecteed", "Clicked on Checkbox: " + cb.getText() +
			          " is " + cb.isChecked());
			         /*Toast.makeText(getApplicationContext(),
			          "Clicked on Checkbox: " + cb.getText() +
			          " is " + cb.isChecked(), 
			          Toast.LENGTH_LONG).show();*/
			         friend.setSelected(cb.isChecked());
			        }  
			       });  
			
			
			
			layout.setTag(viewHolder);
			viewHolder.chk.setTag(friends.get(position));
		//}
			
		} else {
			layout = convertView;
			((ViewHolder) layout.getTag()).chk.setTag(friends.get(position));
		}	
			
		

		FBFriend friend = friends.get(position);
		
		ViewHolder holder = (ViewHolder) layout.getTag();
        //
		//holder.text.setText(list.get(position).getName());
		holder.chk.setChecked(friend.isSelected());
		holder.name.setText(friend.getName());
		if(friend.hasDownloadedProfilePicture()){
			holder.profilePicture.setImageBitmap(friend.getProfilePicture());
		}else{
			holder.profilePicture.setImageBitmap(mr_unknown);
		}
		
		//viewHolder.name.setText(" (" +  friend.getUID() + ")");
		  //viewHolder.chk.setText(friend.getName());
		  //viewHolder.chk.setChecked(friend.isSelected());
		 // viewHolder.chk.setTag(friend);
		return layout;
	}



	@Override
	public int getPositionForSection(int section) {
		Character letter = sections[section];

		return letterIndex.get(letter);
	}

	@Override
	public Object[] getSections() {
		return sections;
	}

	@Override
	public int getSectionForPosition(int arg0) {
		return 0;
	}

}