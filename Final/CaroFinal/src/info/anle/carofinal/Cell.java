//An Le
//CS 646 SDSU
//Final Project
//Caro Chess

package info.anle.carofinal;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;

public abstract class Cell extends Point{
	
	// Cell can be either Cross, Circle or Empty
	public Cell(int x, int y){
		super(x,y);
	}
	
	abstract public void draw(Canvas g, Resources res, int x, int y, int w, int h);
}
