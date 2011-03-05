package com.kudzu.android.ibmwatsonlivewallpaper;

import java.util.Random;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Hair {

	SpherePoint head;
	
	SpherePoint[] lastPoints;
	
	int len = 8;
	
	
	double G_da, G_axis;

	Paint paint;
	Random rand;
	public Hair(Random r,Paint pp){
		
		head = new SpherePoint();
		// set initial position as 50,50,50
		head.x = 50;
		head.y = 50;
		head.z = 50;
		G_da = .20-r.nextDouble()/8;
		G_axis = r.nextInt(180);
		
		lastPoints = new SpherePoint[len];
		
		int i = len -1;
		while(i>=0){
			lastPoints[i] = head.clone();	
			i--;
		}
		
		paint = pp;
		rand = r;
		
	}
	
	
	public void Update(double off_ax){
		
		
		head.RotateXZ(G_axis+off_ax + rand.nextDouble()/5);
		head.ToSpherical();
		head.azimuth += G_da;
		head.toCartesian();
		head.RotateXZ(-G_axis-off_ax + rand.nextDouble()/5);
		
		int i = len -1;
		while(i>0){
			lastPoints[i] = lastPoints[i-1];
			i--;
		}
		lastPoints[0] = head.clone();
	}
	
	public void Draw(Canvas c, double mCenterX, double mCenterY,float sScale){

		float scale = sScale;
		
		//Log.d("xxxxx","ssss"+scale);
		
		float x = (float) (head.x * scale + mCenterX);
		float y = (float) (mCenterY - head.z * scale);
		
		
		
		float xx = (float) (lastPoints[0].x * scale + mCenterX);
		float yy = (float) (mCenterY - lastPoints[0].z * scale);
		float rr = (float) (lastPoints[0].y/7);
		
		
		for(int i=1;i<len;i++){
			
			if(rr<0){
				paint.setAlpha((int) (Math.abs(rr)*10));
				paint.setStrokeWidth(1);
			}else{
				paint.setAlpha(255);
				paint.setStrokeWidth(rr/2);
			}
				c.drawLine(x,y,xx,yy,paint);
			x=xx;
			y=yy;
			
			xx = (float) (lastPoints[i].x * scale + mCenterX);
			yy = (float) (mCenterY - lastPoints[i].z * scale);
			rr = (float) (lastPoints[i].y / 7);
		}
		
		
		
	}
	
	
}
