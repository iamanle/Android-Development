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
public class Single extends View{
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
	// Comp turn
	private boolean compTurn = false;
	// Sound
	private SoundPool sp;
	private int soundID[];
	private static final String TAG = "Game";
	// maxResult array
	int[] lastCompMove;

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
				compTurn = true;
				break;
			case 2:
				Toast.makeText(getContext(), "X Win!", Toast.LENGTH_SHORT).show();
				compTurn = true;
				break;
			case 3:
				Toast.makeText(getContext(), "Tie!", Toast.LENGTH_SHORT).show();
				compTurn = true;
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	//---------------Game Constructor----------
	public Single(Context context){
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
		compTurn = false;
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
		// Check if it's computer turn
		if(compTurn){
			return false;
		}
		// Calculate position of touched square:
		int x_aux = (int)(event.getX() / (this.getWidth()/x));
		int y_aux = (int)(event.getY() / (this.getHeight()/y));
		// Draw player unit:
		if(singleSquare[y_aux][x_aux] instanceof Empty){
			drawXO(x_aux, y_aux);
			compMove(x_aux, y_aux);
		}
		
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
		sp.play(soundID[0], 1, 1, 0, 0, 1);
		singleSquare[y_aux][x_aux] = cell;
		Log.i(TAG, "Touched x_aux: "+ x_aux);
		Log.i(TAG, "Touched y_aux: "+ y_aux);

		// 4) Redraw
		handler.sendMessage(Message.obtain(handler,0));

		// 5) Call validateWinner to check if the move is a win.
		int[] winner = validateWin(x_aux,y_aux);
		if(winner != null && winner[0] >= 4){
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
	
	//---------------Computer Move----------
	private void compMove(int x, int y){
		// Disable touch
		compTurn = true;
		
		// Get Human best move
		int[] maxResult = validateWin(x, y);
		// First Move
		if(maxResult == null && lastCompMove == null){
			if(x == singleSquare.length-1){
				setCompMove(x-1, y);
				return;
			}
			setCompMove(x+1, y);
			return;
		}
		
		// Defend
		else if(maxResult != null && maxResult[0] >= 2){
			if(maxResult[0] >= 2){
				int xMove = maxResult[1];
				int yMove = maxResult[2];
				Log.i(TAG, "max1 x: "+maxResult[1]);
				Log.i(TAG, "max2 y: "+maxResult[2]);
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}

				xMove = maxResult[3];
				yMove = maxResult[4];
				Log.i(TAG, "max3 x: "+maxResult[3]);
				Log.i(TAG, "max4 y: "+maxResult[4]);
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}
		}

		// Attack
		else{
			// Get Comp best move
			Log.i(TAG, "Last comp move x:" + lastCompMove[0]);
			Log.i(TAG, "Last comp move y:" + lastCompMove[1]);
			int[] bestOfLastMove = validateWin(lastCompMove[0],lastCompMove[1]);
			int xMove = 0;
			int yMove = 0;

			// If there is best move
			if(bestOfLastMove != null){
				xMove = bestOfLastMove[1];
				yMove = bestOfLastMove[2];
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}

				xMove = bestOfLastMove[3];
				yMove = bestOfLastMove[4];
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}
			// If no best move
			// Check Horizontal
			xMove = lastCompMove[0]+1;
			yMove = lastCompMove[1];
			if(xMove < singleSquare.length-1){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			xMove = lastCompMove[0]-1;
			yMove = lastCompMove[1];
			if(xMove > 0){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			// Check Vertical
			xMove = lastCompMove[0];
			yMove = lastCompMove[1]+1;
			if(yMove < singleSquare.length-1){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			xMove = lastCompMove[0];
			yMove = lastCompMove[1]-1;
			if(yMove > 0){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			// Check Left Diagonal
			xMove = lastCompMove[0]+1;
			yMove = lastCompMove[1]+1;
			if(xMove < singleSquare.length-1 && yMove < singleSquare[0].length-1){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			xMove = lastCompMove[0]-1;
			yMove = lastCompMove[1]-1;
			if(xMove > 0 && yMove > 0){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			// Check Right Diagonal
			xMove = lastCompMove[0]+1;
			yMove = lastCompMove[1]-1;
			if(xMove < singleSquare.length-1 && yMove > 0){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}

			xMove = lastCompMove[0]-1;
			yMove = lastCompMove[1]+1;
			if(xMove > 0 && yMove < singleSquare[0].length-1){
				if(singleSquare[yMove][xMove] instanceof Empty){
					setCompMove(xMove, yMove);
					return;
				}
			}
		}
		
		// Re enable touch
		compTurn = false;
	}
	
	public void setCompMove(int xMove, int yMove){
		drawXO(xMove,yMove);
		lastCompMove = new int[2];
		lastCompMove[0] = xMove;
		lastCompMove[1] = yMove;	
		compTurn = false;
	}

	//---------------validateWin----------
	// Return an array 
	// 0: max number of units that player has in a row
	// 1,2: x,y coordinate of the start point of the row
	// 3,4: x,y coordinate of the end point of the row
	// 5: type of row 0-Left Diagnol, 1-Right D, 3:Horizontal, 4:Vertical
	private int[] validateWin(int x, int y){
		Cell cell = singleSquare[y][x];
		// Make this method return max move with start and end point of that move
		if(cell instanceof Empty)
			return null;

		int xD = 0, yD = 0, pointCounter = 0;
		int[][] results = new int[4][6];
		
		// Left Diagonal
		// Go to start point of the row
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
		results[0][1]=xD;
		results[0][2]=yD;

		// Go to end point of the row
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
		results[0][3]=xD;
		results[0][4]=yD;
		results[0][0]=pointCounter;

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
		results[1][1]=xD;
		results[1][2]=yD;

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
		results[1][3]=xD;
		results[1][4]=yD;
		results[1][0]=pointCounter;
		
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
		results[2][1]=xD;
		results[2][2]=yD;
		
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
		results[2][3]=xD;
		results[2][4]=yD;
		results[2][0]=pointCounter;

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
		results[3][1]=xD;
		results[3][2]=yD;

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
		results[3][3]=xD;
		results[3][4]=yD;
		results[3][0]=pointCounter;
		
		int max = 0;
		int[] maxResult = null;
		for(int i = 0; i < results.length; i++){
			if(results[i][0] > max){
				max = results[i][0];
				results[i][5]=i;
				maxResult = results[i];
			}
		}

		return maxResult;
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
