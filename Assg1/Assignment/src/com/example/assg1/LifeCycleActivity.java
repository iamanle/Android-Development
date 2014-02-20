package com.example.assg1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LifeCycleActivity extends Activity {
	private static final String TAG = "LifeCycleActivity";
	
	private static final String CREATE = "onCreate";
	private static final String RESTART = "onRestart";
	private static final String START = "onStart";
	private static final String RESUME = "onResume";
	private static final String PAUSE = "onPause";
	
	private int mOnCreate = 0;
	private int mOnRestart = 0;
	private int mOnStart = 0;
	private int mOnResume = 0;
	private int mOnPause = 0;
	
	private Button mResetButton;
	private TextView mCreateTextView;
	private TextView mRestartTextView;
	private TextView mStartTextView;
	private TextView mResumeTextView;
	private TextView mPauseTextView;
	private TextView mLogView;
	
	//-----------------------Update View-------------------------
	private void updateText(){
		mCreateTextView.setText("" + mOnCreate);
		mRestartTextView.setText("" + mOnRestart);
		mStartTextView.setText("" + mOnStart);
		mResumeTextView.setText("" + mOnResume);
		mPauseTextView.setText("" + mOnPause);
		
	}
	
	
	//-------------------------onCreate-------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//--------------------Retrieve Saved State-------------------
		if (savedInstanceState != null){
			mOnCreate = savedInstanceState.getInt(CREATE, 0);
			mOnStart = savedInstanceState.getInt(START, 0);
			mOnRestart = savedInstanceState.getInt(RESTART, 0);
			mOnResume = savedInstanceState.getInt(RESUME, 0);
			mOnPause = savedInstanceState.getInt(PAUSE, 0);
		}
		
		Log.d(TAG, "onCreate called");
		++mOnCreate;
		Log.d(TAG, "mOnCreate: " + mOnCreate);
		
		//--------------------Get View By ID------------------------
		setContentView(R.layout.activity_life_cycle);
		mCreateTextView = (TextView)findViewById(R.id.cCreate);
		mRestartTextView = (TextView)findViewById(R.id.cRestart);
		mStartTextView = (TextView)findViewById(R.id.cStart);
		mResumeTextView = (TextView)findViewById(R.id.cResume);
		mPauseTextView = (TextView)findViewById(R.id.cPause);
		mLogView = (TextView)findViewById(R.id.logView);
		
		//------------------change text color and append text log------
		mLogView.setTextColor(Color.RED);
		mLogView.append("onCreate called \n");
		//-------------------------Reset Button-------------------------
		mResetButton = (Button)findViewById(R.id.reset_button);
		mResetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnCreate = 0;
				mOnRestart = 0;
				mOnStart = 0;
				mOnResume = 0;
				mOnPause = 0;
				updateText();
				mLogView.setText("");
			}
		});
		
		updateText();
	}
	
	//----------------------Does not update onCreate correctly-------------------------
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState){
//		super.onRestoreInstanceState(savedInstanceState);
//		if (savedInstanceState != null){
//			mOnCreate = savedInstanceState.getInt(CREATE, 0);
//			mOnStart = savedInstanceState.getInt(START, 0);
//			mOnRestart = savedInstanceState.getInt(RESTART, 0);
//			mOnResume = savedInstanceState.getInt(RESUME, 0);
//			mOnPause = savedInstanceState.getInt(PAUSE, 0);
//			updateText();
//		}
//	}

	//-------------------------Save State-------------------------
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		Log.i(TAG, "onSavedInstanceState called");
		savedInstanceState.putInt(CREATE, mOnCreate);
		savedInstanceState.putInt(RESTART, mOnRestart);
		savedInstanceState.putInt(START, mOnStart);
		savedInstanceState.putInt(RESUME, mOnResume);
		savedInstanceState.putInt(PAUSE, mOnPause);
	}
	
	@Override
	public void onRestart(){
		super.onRestart();
		Log.d(TAG, "onRestart called");
		++mOnRestart;
		Log.d(TAG, "mOnRestart: " + mOnRestart);
		mLogView.append("onRestart called \n");
		updateText();
	}
	
	@Override
	public void onStart(){
		super.onStart();
		Log.d(TAG, "onStart called");
		++mOnStart;
		Log.d(TAG, "mOnStart: " + mOnStart);
		mLogView.append("onStart called \n");
		updateText();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "onResume called");
		++mOnResume;
		Log.d(TAG, "mOnResume: " + mOnResume);
		mLogView.append("onResume called \n");
		updateText();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG, "onPause called");
		++mOnPause;
		Log.d(TAG, "mOnPause: " + mOnPause);
		mLogView.append("onPause called \n");
		updateText();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.life_cycle, menu);
		return true;
	}

}
