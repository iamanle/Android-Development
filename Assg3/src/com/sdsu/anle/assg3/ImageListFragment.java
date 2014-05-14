package com.sdsu.anle.assg3;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class ImageListFragment extends Fragment {
	private static final String TAG = "ImageListFragment";
	private int user_id;

	private ArrayList<String> mPhoto = new ArrayList<String>();
	private ArrayList<Integer> mPhotoId = new ArrayList<Integer>();
	private ListView mPhotoListView;
	
	private Bitmap[] mImageArray;
	
	// -------------------------onCreate----------------------------
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		user_id = getArguments().getInt("USER_ID");
		Log.i(TAG, "Received USER_ID: " + user_id);
	}

	// -------------------------onCreateView----------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		GetJSON task = new GetJSON();
		task.execute();
		
		View v = inflater.inflate(R.layout.fragment_imagelist, container, false);
		mPhotoListView = (ListView)v.findViewById(R.id.photoListView);
		
		
		return v;
	}

	// -------------------------onActivityCreated----------------------------
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
	}
	
	// ----------------GetJSON image list------------------------
	private class GetJSON extends AsyncTask<Void, Void, String>{
		@Override
		protected String doInBackground(Void... params){
			try{
				String url = "http://bismarck.sdsu.edu/photoserver/userphotos/" + user_id;
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				HttpGet getMethod = new HttpGet(url);
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance(null);
				String responseBody = httpclient.execute(getMethod, responseHandler);
				Log.i(TAG, "Got photo list response: " + responseBody);
				httpclient.close();
				return responseBody;
			} catch (IOException ioe){
				Log.i(TAG, "Failed to fetch photo list");
			}
			return null;
		}

		protected void onPostExecute(String jsonString){
			try{
				JSONArray data = new JSONArray(jsonString);
				JSONObject photo;

				for(int i = 0; i < data.length(); i++){
					photo = (JSONObject)data.getJSONObject(i);
					mPhoto.add(photo.getString("name"));
					mPhotoId.add(photo.getInt("id"));
					Log.i(TAG, "Photo name: "+ mPhoto.get(i) + " id: "+mPhotoId.get(i));
				}
				
				// Call GetImage to download all images to Bitmap
				mImageArray = new Bitmap[mPhotoId.size()];
				String[] url_arr = new String[mPhotoId.size()];
				GetImage set_image = new GetImage();
				for(int i = 0; i < mPhotoId.size(); i++){
					url_arr[i] = "http://bismarck.sdsu.edu/photoserver/photo/" + mPhotoId.get(i);
					
				}
				set_image.execute(url_arr);
				
				// Handle ListView after fetching data
				PhotoAdapter adapter = new PhotoAdapter(mPhoto);
				mPhotoListView.setAdapter(adapter);
				mPhotoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.d(TAG, "Position selected: " + position + " photoid: " + mPhotoId.get(position));
						
						// Get image fragment for tablet
						ImageFragment imgFrag = (ImageFragment)getFragmentManager().findFragmentById(R.id.imageView);
						
						if(imgFrag == null){
							Intent i = new Intent(getActivity(), ImageActivity.class);
							i.putExtra("PHOTO_POS", position);
							i.putIntegerArrayListExtra("PHOTO_ARR", mPhotoId);
							startActivity(i);
						} else {
							// update if imagefragment found
						}
						
					}
				});
			} catch (JSONException e){
				Log.i(TAG, " Failed to get photo object!");
			}
		}
	}

	// -------------------------Create Adapter----------------------------
	private class PhotoAdapter extends ArrayAdapter<String>{
		public PhotoAdapter(ArrayList<String> photos){
			super(getActivity(), 0, photos);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null){
				convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_photo, null);
			}
			
			String photo_name = mPhoto.get(position);
			Log.i(TAG,"mPhoto: " + photo_name);
			
			TextView photoNameTextView = (TextView)convertView.findViewById(R.id.photo_list_titleTextView);
			photoNameTextView.setText(photo_name);
			
			ImageView photoImageView = (ImageView)convertView.findViewById(R.id.photo_listImageView);
			photoImageView.setImageBitmap(mImageArray[position]);
			
			
			return convertView;
		}
	}
	
	// -------------------------Download Image---------------------------
		class GetImage extends AsyncTask<String[], Void, Bitmap[]>{
			protected Bitmap[] doInBackground(String[]...param){
				HttpClient httpclient = null;
				try{
					String[] url = param[0];
					Bitmap[] bitmap = new Bitmap[url.length];
					httpclient = AndroidHttpClient.newInstance(null);
					for(int i = 0; i < url.length; i++){
						HttpGet getMethod = new HttpGet(url[i]);
						HttpResponse response = httpclient.execute(getMethod);
						HttpEntity entity = response.getEntity();
						if(entity == null) bitmap[i] = null;
						byte[] bytes = EntityUtils.toByteArray(entity);
						Bitmap tmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
						bitmap[i] = tmp;
					}
					return bitmap;
				} catch (Throwable t){
					Log.i(TAG, "Cannot download image!");
				} finally {
					((AndroidHttpClient) httpclient).close();
				}
				return null;
			}
			
			protected void onPostExecute(Bitmap[] photo){
				mImageArray = photo;
				//Refresh View
				mPhotoListView.invalidateViews();
			}


		}

}
