package com.sdsu.anle.assg3;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;

public class UserListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);
		
		//-----------Add User List Fragment----------
		FragmentManager fragments = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragments.beginTransaction();
		UserListFragment userListFragment = new UserListFragment();
		fragmentTransaction.add(R.id.ulistContainer, userListFragment);
		fragmentTransaction.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
