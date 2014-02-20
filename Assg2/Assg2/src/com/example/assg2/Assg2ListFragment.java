package com.example.assg2;

import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

public class Assg2ListFragment extends ListFragment implements OnItemClickListener {
	private static final String TAG = "Assg2ListFragment";
	public static final String EXTRA_OUTPUT_TEXT = "com.example.assg2.output_text"; // extra key
	
	private String[] mAndroidVersion;
	private String mClickedButton;
	
	private Button mBackButton;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mAndroidVersion = getResources().getStringArray(R.array.android_version);
		Log.d(TAG, "before inflate");
		// Without assg2_list_fragment.xml, this instruction cant be called to create simple list:
		//	return super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.assg2_list_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				getActivity(), 
				R.array.android_version, 
				android.R.layout.simple_list_item_1);
		setListAdapter(adapter);
		getListView().setOnItemClickListener(this);
		
		mBackButton = (Button)getView().findViewById(R.id.list_backbutton);
		mBackButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra(EXTRA_OUTPUT_TEXT, mClickedButton);
				getActivity().setResult(-1, data);
				FragmentManager fragment = getFragmentManager();
				fragment.popBackStack();
			}
		});
	}

	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		mClickedButton = mAndroidVersion[pos];
		Toast.makeText(getActivity(), mClickedButton + " selected!", Toast.LENGTH_SHORT).show();
	}
}
