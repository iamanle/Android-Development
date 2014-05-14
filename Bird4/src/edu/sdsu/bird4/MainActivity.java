package edu.sdsu.bird4;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends ActionBarActivity implements OnTouchListener {
	private Button startButton;
	private ImageView birdView;
	private boolean isInMotion;
	private int screenHeight;
	//private int screenWidth;
	
	private int downY = 20;
	private int collideY = 100;
	private int upY = 70;
	
	private AnimationDrawable birdAnimation;
	private RelativeLayout main_view;
	private RectDrawing pipes;
	
	private SoundPool sp;
	private int soundID[];
	
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Load sound
		sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundID = new int[2];
		soundID[0] = sp.load(this, R.raw.woosh, 0);
		soundID[1] = sp.load(this, R.raw.punch, 1);
		
 		// Set on touch listener
		main_view = (RelativeLayout)findViewById(R.id.main_view);
		main_view.setOnTouchListener(this);
		
		// Hide Action bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.hide();
		
		// Get Screen Size
		Display display = getWindowManager().getDefaultDisplay();
		Point screenSize = new Point();
		display.getSize(screenSize);
		screenHeight = screenSize.y;
		//screenWidth = screenSize.x;
		
		// Set bird animation
		birdView = (ImageView)findViewById(R.id.bird);
		birdView.setBackgroundResource(R.drawable.bird_animation);
		birdAnimation = (AnimationDrawable)birdView.getBackground();
		birdAnimation.start();
		birdView.bringToFront();
		
		// Set pipes
		pipes = (RectDrawing)findViewById(R.id.rectDrawing1);
	
		// Start button
		startButton = (Button)findViewById(R.id.start_button);
		startButton.setBackgroundResource(R.drawable.playbutton);
		startButton.bringToFront();
		startButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				playButton(v);
				
			}
		});
	}
	
	// -----------------Play button-----------------
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void playButton(View button){
		startButton.setVisibility(View.GONE);
		isInMotion = true;
		moveDown();
	}
	
	// -----------------Touch event-----------------
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		moveUp();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		switch(actionCode){
		case MotionEvent.ACTION_DOWN:
			return handleActionUp(event);
		}
		return false;
	}
	
	private boolean handleActionUp(MotionEvent event){
		isInMotion = true;
		moveUp();
		return true;
	}
	
	// -----------------Move Up-----------------
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void moveUp(){
		birdView.setY(birdView.getY() - upY);
		sp.play(soundID[0], 1, 1, 0, 0, 1);
		pipeCollision();
	}

	// -----------------Move Down-----------------
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void moveDown(){
		birdView.setY(birdView.getY() + downY);
		isInMotion = true;
		pipeCollision();
		changeOnCollison();
		if(isInMotion)
			birdView.postDelayed(new MoverDown(), 50);
	}
	
	public class MoverDown implements Runnable{
		@Override
		public void run() {
			moveDown();
		}
	}
	
	// -----------------Collide-----------------
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void collide(){
		birdView.setY(birdView.getY() + collideY);
		isInMotion = false;
		changeOnCollison();
		if(!isInMotion)
			birdView.postDelayed(new MoverCollide(), 50);
	}
	
	public class MoverCollide implements Runnable{
		@Override
		public void run() {
			collide();
		}
	}
	
	// -----------------Edge collision-----------------
	private void changeOnCollison(){
		if (yIsOutOfBounds(birdView)){
			collideY = 0;
			downY = 0;
			upY = 0;
			birdAnimation.stop();
			isInMotion = false;
			pipes.stop();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean yIsOutOfBounds(View widget){
		float y = widget.getY();
		if (y + widget.getHeight() + 70 > screenHeight) return true;
		return false;
	}
	
	// -----------------Pipe collision-----------------
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void pipeCollision(){
		
		float[] pipe1 = pipes.getPipe1XY();
		float[] pipe2 = pipes.getPipe2XY();
		float x = birdView.getX() + birdView.getWidth();
		float y = birdView.getY() + birdView.getHeight();
		
		if(pipe1[0] <= x && x <= pipe1[1]){
			if(y <= pipe1[2] || y >= pipe1[3]){
				sp.play(soundID[1], 1, 1, 1, 0, 1);
				collide();
			}
		}
		
		if(pipe2[0] <= x && x <= pipe2[1]){
			if(y <= pipe2[2] || y >= pipe2[3]){
				sp.play(soundID[1], 1, 1, 1, 0, 1);
				collide();

			}
		}
	}
	
}
