package com.example.geoev;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryItemAdapter extends ArrayAdapter<FbEvent> {

	private UnixTimeConverter myunix = new UnixTimeConverter();
	private int mResource;

	public EntryItemAdapter(Context context, int resource, List<FbEvent> items) {
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

		FbEvent entry = (FbEvent) getItem(position);
		Log.d("Adapter", entry.toString());
		if (entry.getTitle() != null) {
			((TextView)entryView.findViewById(R.id.event_entry_name)).setText(entry.getTitle());
		}
		if (entry.getLocation() != null) {
			((TextView)entryView.findViewById(R.id.event_entry_location)).setText(entry.getLocation());
		}
		if (entry.getStartTime() != null) {
			
			
			((TextView)entryView.findViewById(R.id.event_entry_start_time)).setText("Starts: " + myunix.convertFromUnixToHuman(entry.getStartTime()));
		}
		if (entry.getEndTime() != null) {
			
			((TextView)entryView.findViewById(R.id.event_entry_end_time)).setText("Ends: " + myunix.convertFromUnixToHuman(entry.getEndTime()));
		}

		return entryView;
	}
	
	//stringToDate("2013-02-05T10:15:00+0200").toString()
	//JSONDate.toViewString(jsonEvents.getJSONObject(i).getString(EventFields.START_TIME)));
	
	public static long formatTimeForEvent(long pacificTime) {
		TimeZone tz = TimeZone.getDefault();
		System.out.println("TimeZone   "+tz.getDisplayName(false, TimeZone.SHORT)+" Timezon id :: " +tz.getID());
		return (pacificTime + TimeZone.getTimeZone("Europe/Athens").getOffset(pacificTime))
		        - TimeZone.getDefault().getOffset(pacificTime);
	}
	
	
	//
	private Date stringToDate(String dateString)
	{
		int pos = 0;
		int year = Integer.parseInt(dateString.substring(pos, pos = dateString.indexOf("-", pos)));
		int month = Integer.parseInt(dateString.substring(pos + 1, pos = dateString.indexOf("-", pos + 1))) - 1;
		int day = Integer.parseInt(dateString.substring(pos + 1, pos = dateString.indexOf("T", pos + 1)));
		int hours = Integer.parseInt(dateString.substring(pos + 1, pos = pos+3));
		int minutes = Integer.parseInt(dateString.substring(pos + 1, pos+3));

		hours = hours - 8;
		if(hours < 0) {
			hours = 24 + hours;
			day--;
		}
		
		return new Date(year - 1900, month, day, hours, minutes);
	}
	
}