package com.example.hw_7;

import java.util.Calendar;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

public class MainActivity extends Activity 
{
	private static final int RESULT_SETTINGS = 1;
	EditText edit;
	WebView wv; 
	String webtext;
	WebSettings webSettings;
	ImageButton cancel;
	final Activity activity = this;
	static PendingIntent pi;
	BroadcastReceiver br;
	static AlarmManager am;
	private NotificationManager notifier = null;
	public SharedPreferences sharedPrefs = null;
	public Calendar calendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}	

	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		volumeControl();
		ActionBar actionbar = getActionBar();
		actionbar.show();
		setup();

		cancel = (ImageButton) findViewById(R.id.cancel);
		cancel.setVisibility(View.GONE);

		Intent intent = getIntent();//get intent from Bookmarks if it exists
		String text = intent.getStringExtra("BookMark");//string of url from Bookmarks

		if (text != null)
		{
			httpControl(text);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) 
		{
		case RESULT_SETTINGS:
			volumeControl();
			break;
		}
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_edit:
			startActivity(new Intent(getApplicationContext(), Bookmarks.class));
			return true;
		case R.id.action_add:
			String url = wv.getUrl();
			if(url != null)
			{
				Intent intent = new Intent(getApplicationContext(), com.example.hw_7.Bookmarks.class);
				intent.putExtra("BookMarkTitle", url);//Send url to be saved to Bookmarks
				startActivity(intent);	
			}
			else
			{
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_LONG;
				Toast.makeText(context, "Cannot add bookmark", duration).show();	
			}
			return true;
		case R.id.action_back:
			try
			{
				wv.goBack();
			}
			catch(Exception e)
			{
				try
				{
					finish();
				}
				catch(Exception e1)
				{
					Context context = getApplicationContext();
					CharSequence text = "Cannot go back!";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			}
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void cancelLoading(View v)
	{
		wv.stopLoading();
	}

	public void viewBookmarks(View v)
	{
		startActivity(new Intent(getApplicationContext(), com.example.hw_7.Bookmarks.class));
	}


	public void searchInterwebs(View v)
	{
		edit = (EditText)findViewById(R.id.text);
		webtext = edit.getText().toString();
		edit.setText(webtext);

		if(webtext != null)
		{
			httpControl(webtext);
		}
	}
	
	public void go_home(View v) 
	{
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String text = sharedPrefs.getString("home_page", null);

		if (text != null)
		{
			httpControl(text);
		}
	}
	
	private void volumeControl()
	{
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean volume = sharedPrefs.getBoolean("Volume", false);
		if(volume == true)
		{
			AudioManager soundManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
			soundManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
		}
	}

	@SuppressWarnings("deprecation")
	public void setup() 
	{
		notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final Notification notify = new Notification(R.drawable.ic_launcher,
				"Hello!", System.currentTimeMillis());
		notify.icon = R.drawable.ic_launcher;
		notify.tickerText = "Notification!";
		notify.when = System.currentTimeMillis();

		br = new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context c, Intent i) 
			{
				notify.number++;
				notify.flags |= Notification.FLAG_AUTO_CANCEL;
				Intent toLaunch = new Intent(MainActivity.this, MainActivity.class);
				String text = sharedPrefs.getString("webpage", null);
				toLaunch.putExtra("BookMark", text);
				PendingIntent intentBack = PendingIntent.getActivity(
						MainActivity.this, 0, toLaunch, PendingIntent.FLAG_CANCEL_CURRENT);
				notify.setLatestEventInfo(MainActivity.this,
						"Webpage", "Click to go load webpage.", intentBack);
				notifier.notify(0x1001, notify);
			}
		};

		registerReceiver(br, new IntentFilter("com.authorwjf.wakeywakey"));
		pi = PendingIntent.getBroadcast( this, 0, new Intent("com.authorwjf.wakeywakey"),0);
		am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
	}

	public static PendingIntent returnPending()
	{
		return pi;
	}

	public static AlarmManager returnAlarm()
	{
		return am;
	}
	
	private void httpControl(String urlText)
	{
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean http = sharedPrefs.getBoolean("http", false);
		edit = (EditText)findViewById(R.id.text);
		webtext = edit.getText().toString();
		edit.setText(webtext);
		wv = (WebView) findViewById(R.id.webview);
		wv.setPadding(0,0,0,0);
		webSettings = wv.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

		//Override android browser so that only webview will be used
		wv.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				return false;
			}
		});
		wv.setWebChromeClient(new WebChromeClient() 
		{
			public void onProgressChanged(WebView view, int loading) 
			{
				activity.setProgress(loading * 1000);
				activity.setProgress(loading * 100);

				if(loading > 0 && loading < 100)
				{
					cancel = (ImageButton) findViewById(R.id.cancel);
					cancel.setVisibility(View.VISIBLE);
				}
				else
				{
					cancel = (ImageButton) findViewById(R.id.cancel);
					cancel.setVisibility(View.GONE);
				}
			}
		});

		if(http == true)
		{

			if(urlText.contains("http://"))
			{
				edit.setText(urlText);
				wv.loadUrl(urlText);
			}
			else if(urlText.contains("easter egg"))
			{
				wv.loadUrl("http://youtu.be/dQw4w9WgXcQ?autoplay=1");
			}
			else
			{
				edit.setText("http://" +urlText);
				wv.loadUrl("http://" + urlText);
			}
		}
		else
		{
			edit.setText(urlText);
			wv.loadUrl(urlText);
		}

	}
}