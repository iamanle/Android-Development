package edu.sdsu.bird4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class RectDrawing extends View {
	private Paint paint = new Paint();
	// XY of 2 pipes
	private int x1 = 1200;
	private int x2 = 500;
	private int y = 0;
	
	// Array to store current coordinator of 2 pipes
	private float[] pipe1;
	private float[] pipe2;
	
	// pipe width and height
	private int width = 220;
	private int min = 200;
	private int max = 1000;
	private int height1 = min + (int)(Math.random() * ((max - min) + 1));
	private int height2 = min + (int)(Math.random() * ((max - min) + 1));
	
	// stop
	private boolean stop = false;
	
	public RectDrawing(Context context, AttributeSet xmlAttrs){
		super(context, xmlAttrs);
	}
	
	public float[] getPipe1XY(){
		return pipe1;
	}
	
	public float[] getPipe2XY(){
		return pipe2;
	}
	
	public void stop(){
		stop = true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int bottom = canvas.getHeight();

		paint.setColor(0xff74AC23);
		canvas.drawRect(x1,y,x1+width,y+height1, paint);
		canvas.drawRect(x1,bottom-(1300-height1),x1+width,bottom, paint);
		pipe1 = new float[]{x1,x1+width,y+height1,bottom-(1300-height1)};
		x1 = x1 - 4;
		
		
		canvas.drawRect(x2,y,x2+width,y+height2, paint);
		canvas.drawRect(x2,bottom-(1300-height2),x2+width,bottom, paint);
		pipe2 = new float[]{x2,x2+width,y+height2,bottom-(1300-height2)};
		x2 = x2 - 4;
		
		
		if (x1 + width < 0) {
			x1 = 1200;
			height1 = min + (int)(Math.random() * ((max - min) + 1));
		}
		
		if (x2 + width < 0) {
			x2 = 1200;
			height2 = min + (int)(Math.random() * ((max - min) + 1));
		}
		
		if(!stop){
			invalidate();
		}
		
	}
}
