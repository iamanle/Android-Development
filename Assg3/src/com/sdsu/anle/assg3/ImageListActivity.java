package com.sdsu.anle.assg3;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;


public class ImageListActivity extends FragmentActivity {
	private static final String TAG = "ImageListActivity";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagelist);
		
		//-----------Get USER_ID from userlist fragment----------
		Intent i = getIntent();
		int user_id = i.getIntExtra("USER_ID", RESULT_CANCELED);
		
		
		//-----------Send user_id to list fragment----------
		Bundle bundle = new Bundle();
		bundle.putInt("USER_ID", user_id);
		
		//-----------Add Image List Fragment----------
		FragmentManager fragments = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragments.beginTransaction();
		ImageListFragment imageListFragment = new ImageListFragment();
		imageListFragment.setArguments(bundle); // send argument with fragment
		fragmentTransaction.add(R.id.ilistContainer, imageListFragment);
		fragmentTransaction.commit();
		
		// Trying to do the tablet view:
		ViewGroup mImageView = (ViewGroup)findViewById(R.id.imageContainer);
		if(mImageView != null){
			Log.i(TAG, "Found imageContainer");
//			Bundle bundle2 = new Bundle();
//			bundle2.putInt("PHOTO_POS", 0);
//			ArrayList<Integer> a = new ArrayList<Integer>();
//			a.add(0);
//			bundle2.putIntegerArrayList("PHOTO_ARR", a);
//			
//			FragmentManager fragments2 = getFragmentManager();
//			FragmentTransaction fragmentTransaction2 = fragments2.beginTransaction();
//			ImageFragment imageFragment = new ImageFragment();
//			imageFragment.setArguments(bundle2);
//			fragmentTransaction2.add(R.id.imageContainer, imageFragment);
//			fragmentTransaction2.commit();
			
		}
	}
}
