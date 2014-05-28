//An Le
//CS 646 SDSU
//Final Project
//Caro Chess

package info.anle.carofinal;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {
	private Game twoPlayers;
	private Single single;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show Single View as default
		single = new Single(this);
		setContentView(single);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_two:
			twoPlayers = new Game(this);
			setContentView(twoPlayers);
			return true;
		case R.id.action_one:
			single = new Single(this);
			setContentView(single);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
