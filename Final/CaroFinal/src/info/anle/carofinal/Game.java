//An Le
//CS 646 SDSU
//Final Project
//Caro Chess

package info.anle.carofinal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Game extends View{
	private Cell[][] singleSquare = null;
	private Paint drawingPen;
	// Number of squares on screen
	private int x = 20;
	private int y = 20;
	// Number of units in a row to win
	private int winCondition = 5;
	// Screen size
	private int screenWidth;
	private int screenHeight;
	// whotTurn = true is X turn, false is O turn. Set X goes first:
	private boolean whoTurn = true;
	// Sound
	private SoundPool sp;
	private int soundID[];
	private static final String TAG = "Game";
	// Touch enable
	boolean isTouch;

	//---------------Handler----------
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			// Redraw for new match
			case 0:
				invalidate();
				break;
				// Toast to notify winner:
			case 1:
				Toast.makeText(getContext(), "O Win!", Toast.LENGTH_SHORT).show();
				isTouch = false;
				break;
			case 2:
				Toast.makeText(getContext(), "X Win!", Toast.LENGTH_SHORT).show();
				isTouch = false;
				break;
			case 3:
				Toast.makeText(getContext(), "Tie!", Toast.LENGTH_SHORT).show();
				isTouch = false;
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	//---------------Game Constructor----------
	public Game(Context context){
		super(context);

		// Load sound
		sp = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundID = new int[2];
		soundID[0] = sp.load(context, R.raw.tick, 1);
		soundID[1] = sp.load(context, R.raw.winner, 1);

		// Set Paint, color, strokeWidth
		drawingPen = new Paint();
		this.drawingPen.setARGB(255,0,0,0);
		this.drawingPen.setAntiAlias(true);
		this.drawingPen.setStyle(Style.STROKE);
		this.drawingPen.setStrokeWidth(1);

		// Init number of squares on screen
		singleSquare = new Cell[x][y];

		// Calculate and set size of each square
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();
		int xss = screenWidth/x;
		int yss = screenHeight/y;

		for(int z = 0; z < y; z++){
			for(int i = 0; i < x; i++){
				singleSquare[z][i] = new Empty(xss*i,z*yss);
			}
		}
		
		// Enable touch
		isTouch = true;
	}

	//---------------resizeGame----------
	// It calls redraw message from handler.
	// Also, I plan to use this method for setting new N for NxN grid.
	public void resizegame(int s) { 
		x = s;
		y = s;
		singleSquare = new Cell[x][y]; 
		int xss = screenWidth / x;
		int yss = screenHeight / y;
		for (int z = 0; z < y; z++) { 
			for (int i = 0; i < x; i++) {
				singleSquare[z][i] = new Empty(xss * i, z * yss); 
			}
		}
		handler.sendMessage(Message.obtain(handler, 0)); 
	}

	//---------------onDraw----------
	@Override
	protected void onDraw(Canvas canvas){
		// Draw all the object in singleSquare. For its first time, all objects are Empty objects.
		for(int i = 0; i < singleSquare.length; i++){
			for(int j = 0; j < singleSquare[0].length; j++){
				// Input for drawing rect(left,top,right,bottom)
				// j, i position index, width of 1 square, height of 1 square
				singleSquare[i][j].draw(canvas,getResources(),
						j,i,
						this.getWidth()/singleSquare.length,
						this.getHeight()/singleSquare[0].length);
			}
		}

		// Draw Grid:
		int xs = this.getWidth()/x;
		int ys = this.getHeight()/y;
		for(int i = 0; i <= x; i++){
			canvas.drawLine(xs*i, 0, xs*i, this.getHeight(), drawingPen);
		}
		for(int i = 0; i <= y; i++){
			canvas.drawLine(0, ys*i, this.getWidth(), ys*i, drawingPen);
		}
		super.onDraw(canvas);
	}

	//---------------onTouchEvent----------
	@Override
	public boolean onTouchEvent(MotionEvent event){
		if(!isTouch){
			return false;
		}
		
		// Calculate position of touched square:
		int x_aux = (int)(event.getX() / (this.getWidth()/x));
		int y_aux = (int)(event.getY() / (this.getHeight()/y));
		// Draw player unit:
		drawXO(x_aux, y_aux);
		return super.onTouchEvent(event);
	}

	//---------------drawXO----------
	public void drawXO(int x_aux, int y_aux){
		// 1) Init a cell
		Cell cell = null;

		// 2) Check whoTurn: true is X, false is O
		if(whoTurn){
			cell = new Cross(singleSquare[x_aux][y_aux].x,singleSquare[x_aux][y_aux].y);
			whoTurn = false;
		} else {
			cell = new Circle(singleSquare[x_aux][y_aux].x,singleSquare[x_aux][y_aux].y);
			whoTurn = true;
		}

		// 3) Check if the called singleSquared is Empty
		// Set it to cell if it is Empty. This prevents players from overwriting the object at that cell.
		if(singleSquare[y_aux][x_aux] instanceof Empty){
			sp.play(soundID[0], 1, 1, 0, 0, 1);
			singleSquare[y_aux][x_aux] = cell;
			Log.i(TAG, "Touched x_aux: "+ x_aux);
			Log.i(TAG, "Touched y_aux: "+ y_aux);
		} else {
			whoTurn = !whoTurn;
		}

		// 4) Redraw
		handler.sendMessage(Message.obtain(handler,0));

		// 5) Call validateWinner to check if the move is a win.
		if(validateWin(singleSquare[y_aux][x_aux],x_aux,y_aux)){
			sp.play(soundID[1], 1, 1, 0, 0, 1);
			// O win. 
			// Check if whoTurn is X turn (true), then the winner is O the one made the last move.
			if(whoTurn){
				handler.sendMessage(Message.obtain(handler,1));
			// X win
			} else {
				handler.sendMessage(Message.obtain(handler,2));
			}
		}
		// Tie if no square on screen is available to go
		else if(isFull()) {
			handler.sendMessage(Message.obtain(handler, 3));
			resizegame(x);
		}
	}

	//---------------validateWin----------
	private boolean validateWin(Cell cell, int x, int y){
		if(cell instanceof Empty)
			return false;

		int xD = 0, yD = 0, pointCounter = 0;

		// Left Diagonal
		xD = x;
		yD = y;
		while(xD < singleSquare.length-1 && yD < singleSquare[0].length-1  
				&& pointCounter < winCondition-1){
			if(singleSquare[++yD][++xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Front Left Diagonal: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		xD = x;
		yD = y;
		while(xD > 0 && yD > 0
				&& pointCounter < winCondition-1){
			if(singleSquare[--yD][--xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Back Left Diagonal: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		if(pointCounter >= 4){
			return true;
		}

		// Right Diagonal
		pointCounter = 0;
		xD = x;
		yD = y;
		while(xD < singleSquare.length-1 && yD > 0  
				&& pointCounter < winCondition-1){
			if(singleSquare[--yD][++xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Front Right Diagonal: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		xD = x;
		yD = y;
		while(xD > 0 && yD < singleSquare[0].length-1
				&& pointCounter < winCondition-1){
			if(singleSquare[++yD][--xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Back Right Diagonal: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		if(pointCounter >= 4){
			return true;
		}
		
		// Horizontal
		pointCounter = 0;
		xD = x;
		yD = y;
		while(xD < singleSquare.length-1  
				&& pointCounter < winCondition-1){
			if(singleSquare[yD][++xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Front Horizontal: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		xD = x;
		yD = y;
		while(xD > 0
				&& pointCounter < winCondition-1){
			if(singleSquare[yD][--xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Back Horizontal: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		if(pointCounter >= 4){
			return true;
		}

		// Vertical
		pointCounter = 0;
		xD = x;
		yD = y;
		while(yD < singleSquare.length-1  
				&& pointCounter < winCondition-1){
			if(singleSquare[++yD][xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Front Vertical: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		xD = x;
		yD = y;
		while(yD > 0
				&& pointCounter < winCondition-1){
			if(singleSquare[--yD][xD].equals(cell)){
				pointCounter++;
				Log.i(TAG, "Back Vertical: "+pointCounter);
				Log.i(TAG, "xD: "+xD);
				Log.i(TAG, "yD: "+yD);
			}
			else
				break;
		}

		if(pointCounter >= 4){
			return true;
		}

		return false;
	}

	//---------------isFull----------
	private boolean isFull() {
		for (int i = 0; i < singleSquare.length; i++) {
			for (int j = 0; j < singleSquare[0].length; j++) { 
				if (singleSquare[i][j] instanceof Empty) {
					return false; 
				}
			} 
		}
		return true; 
	}
}
