package com.sdsu.anle.assg3;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListFragment;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class UserListFragment extends ListFragment{
	private static final String TAG = "UserListFragment";
	private ArrayList<String> mUsers = new ArrayList<String>();
	private ArrayList<Integer> mUserId = new ArrayList<Integer>();

	// -------------------------onCreate----------------------------
	// Execute GetJSON class with userlist url
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		GetJSON task = new GetJSON();
		String url = "http://bismarck.sdsu.edu/photoserver/userlist";
		task.execute(url);
	}

	// -------------------------onCreateView----------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.i(TAG, "GetActivity value is: " + getActivity());
		return inflater.inflate(R.layout.fragment_userlist, container, false);

	}

	// -------------------------onActivityCreated----------------------------
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	// ------------------GetJSON AsyncTask--------------------------------
	// <Params, Progress, Result>
	private class GetJSON extends AsyncTask<String, Void, String>{
		@Override
		protected String doInBackground(String... urls) {
			try{
				ResponseHandler<String> responseHandler =  new BasicResponseHandler();
				HttpGet getMethod = new HttpGet(urls[0]);
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance(null);
				String responseBody = httpclient.execute(getMethod, responseHandler);
				Log.i(TAG, "Got response: " + responseBody);
				httpclient.close();
				return responseBody;
			} catch (IOException ioe){
				Log.i(TAG, "Failed to fetch!");
			}
			return null;
		}

		// Loop each JSONObject and add to arraylist
		protected void onPostExecute(String jsonString){
			try{
				JSONArray data = new JSONArray(jsonString);
				JSONObject person;

				for(int i = 0; i < data.length(); i++){
					person = (JSONObject)data.get(i);
					mUsers.add(person.getString("name"));
					mUserId.add(person.getInt("id"));
					Log.i(TAG, "Name: " + mUsers.get(i) + " id: " + mUserId.get(i));
				}
				// Call create adapter after fetching data
				setupAdapter();
			} catch(JSONException e){
				Log.i(TAG, "Failed to get person object!");
			}
		}
	}

	// -------------------------Create Adapter----------------------------
	void setupAdapter(){
		if(getActivity() == null) return;
		if(mUsers != null){
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					getActivity(),
					android.R.layout.simple_list_item_1,
					mUsers);

			setListAdapter(adapter);
			Log.d(TAG, "ListAdapter Set!");
		}else{
			setListAdapter(null);
			Log.d(TAG, "Failed ListAdapter Set!");
		}
	}

	// -------------------------onListItemClick----------------------------
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		int user_id = mUserId.get(position);
		Log.i(TAG, "User ID: " + user_id);
		Intent i = new Intent(getActivity(), ImageListActivity.class);
		i.putExtra("USER_ID", user_id);
		startActivity(i);
	}


}
