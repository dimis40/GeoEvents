package com.example.geoev;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FeedItemAdapter extends ArrayAdapter<FbFeed> {

	private int mResource;

	public FeedItemAdapter(Context context, int resource, List<FbFeed> items) {
		super(context, resource, items);
		mResource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout entryView;

		if (convertView == null) {
			entryView = new LinearLayout(getContext());
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			li.inflate(mResource, entryView, true);
		} else {
			entryView = (LinearLayout) convertView;
		}

		FbFeed entry = (FbFeed) getItem(position);
		Log.d("Adapter", entry.toString());
		if (entry.getUserName() != null) {
			((TextView)entryView.findViewById(R.id.user_name)).setText("Posted by: " + entry.getUserName());
		}
		if (entry.getMessage() != null) {
			((TextView)entryView.findViewById(R.id.message)).setText(entry.getMessage());
		}
		if (entry.getCreatedTime() != null) {
			((TextView)entryView.findViewById(R.id.created_time)).setText("Created time: " + convertFBTime(entry.getCreatedTime()));
		}
		return entryView;
	}

	//
	public static final String convertFBTime(String fbTime) {
		String ret;
		try {
			SimpleDateFormat fbFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ssZ");
			Date eventTime = fbFormat.parse(fbTime);
			Date curTime = new Date();

			long diffMillis = curTime.getTime() - eventTime.getTime();
			long diffSeconds = diffMillis / 1000;
			long diffMinutes = diffMillis / 1000 / 60;
			long diffHours = diffMillis / 1000 / 60 / 60;
			if (diffSeconds < 60) {
				ret = diffSeconds + " seconds ago";
			} else if (diffMinutes < 60) {
				ret = diffMinutes + " minutes ago";
			} else if (diffHours < 24) {
				ret = diffHours + " hours ago";
			} else {
				String dateFormat = "MMMMM d";
				if (eventTime.getYear() < curTime.getYear()) {
					dateFormat += ", yyyy";
				}
				dateFormat += "' at 'kk:mm";

				SimpleDateFormat calFormat = new SimpleDateFormat(dateFormat);
				ret = calFormat.format(eventTime);
			}
		} catch (Exception ex) {
			ret = "error: " + ex.toString();
		}
		return ret;
	}

}
