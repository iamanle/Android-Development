package com.example.assg2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("NewApi")
public class KeyboardActivity extends Activity {
	public static final String EXTRA_INPUT_TEXT = "com.example.assg2.input_text"; // extra key
	
	private String mInputText;
	
	private EditText mEditText1;
	
	private Button mBackButton;
	private Button mHideButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard);
		
		//--------------------Get Extra from MainActivity------------
		mInputText = getIntent().getStringExtra(EXTRA_INPUT_TEXT);
		
		//--------------------Show Input Text------------------------
		mEditText1 = (EditText)findViewById(R.id.editText1);
		mEditText1.setText(mInputText);
		
		//--------------------Hide Button------------------------
		mHideButton = (Button)findViewById(R.id.hide_button);
		mHideButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager manager;
				manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
				manager.hideSoftInputFromWindow(mEditText1.getWindowToken(), 0);
			}
		});
		
		//--------------------Back Button------------------------
		mBackButton = (Button)findViewById(R.id.back_button);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed(); // or finish()
			}
		});
		
		//--------------------Home Button------------------------
		getActionBar().setHomeButtonEnabled(true);
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
