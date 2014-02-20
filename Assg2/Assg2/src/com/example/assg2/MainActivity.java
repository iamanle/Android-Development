package com.example.assg2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

@SuppressLint("NewApi")
public class MainActivity extends Activity{
	private static final String TAG = "MainActivity";
	
	private int mItemSelected;
	private String mOutputText;
	
	private Spinner mChoicesSpinner;
	private Button mGoButton;
	private EditText mInputEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		//--------------------Get Edit Text------------------------
		mInputEditText = (EditText)findViewById(R.id.input_edittext);
		
		//--------------------Set Up Spinner------------------------
		mChoicesSpinner = (Spinner)findViewById(R.id.choices_spinner);
		// Create an ArrayAdapter using the string choices_array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.choices_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		mChoicesSpinner.setAdapter(adapter);
		// When an item is selected:
		mChoicesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mItemSelected = position;
				Log.d(TAG, "Position selected: " + mItemSelected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {	
			}
		});
		
		//--------------------Go Button------------------------
		mGoButton = (Button)findViewById(R.id.go_button);
		mGoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//-------------Keyboard Activity--------------------
				if(mItemSelected == 1){
					// Pass in the current package where ActivityManager can find KeyboardActivity
					Intent i = new Intent(MainActivity.this, KeyboardActivity.class);
					String inputText = mInputEditText.getText().toString();
					Log.d(TAG, "Input Text: " + inputText);
					i.putExtra(KeyboardActivity.EXTRA_INPUT_TEXT, inputText);
					startActivity(i);
				}
				
				//-------------Keyboard Activity--------------------
				if(mItemSelected == 2){
					Intent i = new Intent(MainActivity.this, WebActivity.class);
					startActivity(i);
				}
				
				//-------------List Activity--------------------
				if(mItemSelected == 3){
					Intent i = new Intent(MainActivity.this, ListActivity.class);
					startActivityForResult(i,0);
				}
			}
		});
		
		
	}
	


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null){
			return;
		}
		mOutputText = data.getStringExtra(Assg2ListFragment.EXTRA_OUTPUT_TEXT);
		mInputEditText.setText(mOutputText);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}


}
