package com.sdsu.anle.assg3;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


public class ImageFragment extends Fragment {
	private static final String TAG = "ImageFragment";
	private int mPhotoPos;
	private int mPhotoId;
	private ArrayList<Integer> mPhotoArray;
	private ImageView mImageView;
	private Bitmap bitmap;
	
	// --------------------Action Bar Button---------------------------
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.imagebar, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.save_image:
				try {
					boolean saved = saveImage();
					Log.d(TAG, "Saved: " + saved);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	// --------------------Save Image--------------------------
	public boolean saveImage() throws IOException{
		String fullPath = Environment.getDataDirectory().getAbsolutePath() + "/directoryName";
		Log.i(TAG, "Saved path: " + fullPath);
		String filename = String.valueOf(mPhotoId);
		OutputStream file = new BufferedOutputStream(getActivity().openFileOutput(filename, Context.MODE_PRIVATE));
		boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG,50,file);
		
		file.close();
		Context context =getActivity().getApplicationContext();
		CharSequence text = "Image saved!";
		int duration = Toast.LENGTH_SHORT;
		Toast saved = Toast.makeText(context, text, duration);
		saved.show();
		return compressed;
	}
	
	// -------------------------onCreate----------------------------
	// Execute GetJSON class with userlist url
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
		mPhotoPos = getArguments().getInt("PHOTO_POS");
		mPhotoArray = getArguments().getIntegerArrayList("PHOTO_ARR");
		mPhotoId = mPhotoArray.get(mPhotoPos);
		Log.i(TAG, "Received mPhotoId: " + mPhotoId);
	}

	// -------------------------onCreateView----------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.i(TAG, "GetActivity value is: " + getActivity());
		View v = inflater.inflate(R.layout.fragment_image, container, false);
		mImageView = (ImageView)v.findViewById(R.id.imageView);
		
		updateView(mPhotoId);
		return v;

	}
	
	// -------------------------upDateView----------------------------
	public void updateView(int photo_id){
		// Call GetImage
		GetImage task = new GetImage();
		String url = "http://bismarck.sdsu.edu/photoserver/photo/" + photo_id;
		task.execute(url);
	}
	
	// -------------------------onActivityCreated----------------------------
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	// -------------------------Download Image---------------------------
	class GetImage extends AsyncTask<String, Void, Bitmap>{
		protected Bitmap doInBackground(String...urls){
			HttpClient httpclient = null;
			try{
				httpclient = AndroidHttpClient.newInstance(null);
				HttpGet getMethod = new HttpGet(urls[0]);
				HttpResponse response = httpclient.execute(getMethod);
				HttpEntity entity = response.getEntity();
				if(entity == null) return null;
				byte[] bytes = EntityUtils.toByteArray(entity);
				bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				return bitmap;
			} catch (Throwable t){
				Log.i(TAG, "Cannot download image!");
			} finally {
				((AndroidHttpClient) httpclient).close();
			}
			return null;
		}
		
		protected void onPostExecute(Bitmap photo){
			mImageView.setImageBitmap(photo);
		}
	}
}
