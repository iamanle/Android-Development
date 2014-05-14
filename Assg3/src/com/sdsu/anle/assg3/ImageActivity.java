package com.sdsu.anle.assg3;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

public class ImageActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		
		//-----------Get phot_arr and phot_pos extra----------
		Intent i = getIntent();
		int photo_pos = i.getIntExtra("PHOTO_POS", RESULT_CANCELED);
		ArrayList<Integer> photo_arr = i.getIntegerArrayListExtra("PHOTO_ARR");

		//-----------Send extras to fragment----------
		Bundle bundle = new Bundle();
		bundle.putInt("PHOTO_POS", photo_pos);
		bundle.putIntegerArrayList("PHOTO_ARR", photo_arr);
		
		
		//-----------Add User List Fragment----------
		FragmentManager fragments = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragments.beginTransaction();
		ImageFragment imageFragment = new ImageFragment();
		imageFragment.setArguments(bundle);
		fragmentTransaction.add(R.id.imageContainer, imageFragment);
		fragmentTransaction.commit();
	}


}
