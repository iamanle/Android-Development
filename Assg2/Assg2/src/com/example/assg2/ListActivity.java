package com.example.assg2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

@SuppressLint("NewApi")
public class ListActivity extends Activity{
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		//--------------------Home Button------------------------
		getActionBar().setHomeButtonEnabled(true);
		
		//--------------------Add Fragment to FragContainer (activity_list.xml)------------
		FragmentManager fragments = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragments.beginTransaction();
		Assg2ListFragment mylistfragment = new Assg2ListFragment();
		fragmentTransaction.add(R.id.fragmentContainer, mylistfragment);
		fragmentTransaction.addToBackStack("androidlist");
		
		// If backstackcount == 0 then return to main menu:
		fragments.addOnBackStackChangedListener(new OnBackStackChangedListener() {
			
			@Override
			public void onBackStackChanged() {
				if (getFragmentManager().getBackStackEntryCount() == 0){
					finish();
				}
			}
		});
		
		fragmentTransaction.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(android.R.id.home == item.getItemId()){
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
