package com.example.assg2;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class WebActivity extends Activity {
	private Button mWebButton;
	private EditText mWebURL;
	private WebView mWebView;
	
	private String mURL;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		//--------------------Home Button------------------------
		getActionBar().setHomeButtonEnabled(true);
				
		//--------------------URL field------------------------
		mWebURL = (EditText)findViewById(R.id.url_edittext);
		
		
		//--------------------Web View------------------------
		mWebView = (WebView)findViewById(R.id.web_view);
		mWebView.loadUrl("http://www.google.com");
		
		//--------------------Go Button------------------------
		mWebButton = (Button)findViewById(R.id.web_button);
		mWebButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mURL = mWebURL.getText().toString();
				mWebView.setWebViewClient(new WebViewClient(){

				    @Override
				    public boolean shouldOverrideUrlLoading(WebView view, String url){
				      return false;
				    }
				});
				mWebView.loadUrl("http://www." + mURL);
			}
		});
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
