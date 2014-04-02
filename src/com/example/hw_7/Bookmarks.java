package com.example.hw_7;

import java.util.ArrayList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import android.database.Cursor;

public class Bookmarks extends Activity 
{
	private static final String table_name = "tbl_bookmarks";
	protected ArrayAdapter adapter;
	final ArrayList<String> list = new ArrayList<String>();
	private static SQLiteDatabase mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookmarks);
		initializeDatabase();
		updateBookmarks();

		ActionBar actionbar = getActionBar();
		actionbar.show();

		Intent intent = getIntent();
		String text = intent.getStringExtra("BookMarkTitle");//Get url string from MainActivity

		if(text != null)
		{
			ContentValues cv = new ContentValues();
			String[] bookmarks = text.split("\\.");

			cv.put("server", bookmarks[0]);
			cv.put("hostname", bookmarks[1]);
			cv.put("tld", bookmarks[2]);

			mDatabase.insert(table_name, null, cv);

			updateBookmarks();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.bookmarks, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.action_done:
			startActivity(new Intent(getApplicationContext(), MainActivity.class));
			return true;
		case R.id.action_back:
			try
			{
				finish();
			}
			catch(Exception e)
			{
				Context context = getApplicationContext();
				CharSequence text = "Cannot go back!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
			}
			return true;
		case R.id.action_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void insertRecords(View v) 
	{
		final EditText bookmark = (EditText) findViewById(R.id.BookmarkName);

		ContentValues cv = new ContentValues();
		String[] bookmarks = bookmark.getText().toString().split("\\.", 3);

		cv.put("server", bookmarks[0]);
		cv.put("hostname", bookmarks[1]);
		cv.put("tld", bookmarks[2]);

		mDatabase.insert(table_name, null, cv);

		updateBookmarks();
	}

	public void deleteRecords(View v)
	{
		TextView tv = (TextView) findViewById(R.id.BookmarkNumber);
		String book_mark_no = tv.getText().toString();
		mDatabase.delete(table_name, "id=?", new String[] {book_mark_no});
		updateBookmarks();
	}
	private void updateBookmarks()
	{
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean sort = sharedPrefs.getBoolean("sort", false);
		try
		{
			Cursor c = mDatabase.query(table_name, null, null, null, null, null, null);
			Log.d("SQLiteDemo", "Num records = " + c.getCount());
			ArrayList<String> list = new ArrayList<String>();
			String cur_str = new String();
			String str_num = new String();
			if(sort == true)
			{
				c = mDatabase.query(table_name, null, null, null, null, null, "hostname");
			}
			c.moveToFirst();

			while (c.isAfterLast() == false) {
				str_num = c.getString(0)  + ")\t";
				cur_str = c.getString(0)  + ")\t" + c.getString(1) + ".";
				for (int i = 2; i < c.getColumnCount(); i++) {
					if (c.getString(i) != null) {
						cur_str += c.getString(i);
						if (i+1 < c.getColumnCount())
							cur_str += ".";
					}
				}
				list.add(cur_str);
				c.moveToNext();
			}
			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);//Adapter to display list of bookmarks
			ListView listview = (ListView)findViewById(R.id.DatabaseContent);
			listview.setAdapter(adapter);

			// Create an listener to act upon clicks
			OnItemClickListener listener = new OnItemClickListener() 
			{
				public void onItemClick(AdapterView parent, View v, int position, long id) 
				{
					CharSequence book = ((TextView) v).getText();
					String[] newWebAddress = book.toString().split("\t");
					Intent intent = new Intent(getApplicationContext(), com.example.hw_7.MainActivity.class);
					intent.putExtra("BookMark", newWebAddress[1]);
					startActivity(intent);	
				}
			};

			// Attach the listener to the ListView
			listview.setOnItemClickListener(listener);	
			c.close();
		}
		catch(Exception e)
		{
			Log.d("SQLiteDemo", "Error querying tbl_treks");
		}
	}
	public SQLiteDatabase initializeDatabase()
	{
		mDatabase = openOrCreateDatabase(
				"bookmarks.db",
				SQLiteDatabase.CREATE_IF_NECESSARY,
				null);
		final String CREATE_BOOKMARK_TABLE =
				"CREATE TABLE tbl_bookmarks (id INTEGER PRIMARY KEY AUTOINCREMENT, server TEXT, hostname TEXT, tld TEXT);";

		try
		{
			mDatabase.execSQL(CREATE_BOOKMARK_TABLE);
		}
		catch(Exception e)
		{
			Log.d("SQLiteDemo", "tbl_bookmarks table already exists");
		}
		return mDatabase;
	}
}
