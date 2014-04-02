package com.example.hw_7;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class SettingsActivity extends Activity 
{
	private NotificationManager notifier = null;
	public SharedPreferences sharedPrefs = null;
	public Calendar calendar;
	public AlarmManager alarm;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		ActionBar actionbar = getActionBar();
		actionbar.show();
		alarmControl();
		getFragmentManager().beginTransaction().replace(android.R.id.content, 
				new SettingsFragment()).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public int alarmControl()
	{
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		SimpleDateFormat dateTime = new SimpleDateFormat("kmm");
		String time = sharedPrefs.getString("time", "12:00");
		String[] timeSplitter = time.split(":");
		int notificationHours = Integer.parseInt(timeSplitter[0]);
		int notificationMinutes = Integer.parseInt(timeSplitter[1]);
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int currentTime = Integer.parseInt(dateTime.format(calendar.getTimeInMillis()));
		int hours = notificationHours - currentTime/100;
		int minutes = notificationMinutes - currentTime%100;	
		
		if(minutes < 0)
		{
			hours = hours-1;
			minutes += 60;
		}
		if(hours < 0)
		{
			hours+= 24;
		}
		
		int milliTime = ((hours*3600) + (minutes*60))*1000;
		
		return milliTime;	
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_done:
			alarm = MainActivity.returnAlarm();
			int alarmTime = alarmControl();
			alarm.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + alarmTime, MainActivity.returnPending());
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
