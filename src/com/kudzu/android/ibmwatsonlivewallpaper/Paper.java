/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kudzu.android.ibmwatsonlivewallpaper;



import java.util.Random;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;



public class Paper extends WallpaperService {

	public static final String SHARED_PREFS_NAME = "watson_settings";


	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new PaperEngine();
	}

	class PaperEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener {

		private final Handler mHandler = new Handler();

		
		private final Paint mPaint = new Paint();
		
		Paint pBlue,pGreen,pRed,pYellow;
		
		Paint[] paints;
		
		float mOffset = -1;
		private float mTouchX = -1;
		private float mTouchY = -1;
		private float mCenterX;
		private float mCenterY;

		private final Runnable mDrawWatson = new Runnable() {
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible;
		private SharedPreferences mPrefs;

		
		Hair[] hairs;
		Bitmap image;
		int truesize;
		double mOffsetAxis;
		
		
		int max_hair = 20;
		int max_fps = 24;
		
		
		PaperEngine() {
			// Create a Paint to draw the lines for our cube
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);			
			
			
			
			pBlue = new Paint();
			pBlue.setColor(0xff00A8EF);
			pBlue.setAntiAlias(true);
			pBlue.setStrokeWidth(10);
			pBlue.setStrokeCap(Paint.Cap.ROUND);
			pBlue.setStyle(Paint.Style.STROKE);

			
			pGreen = new Paint();
			pGreen.setColor(0xff00B74D);
			pGreen.setAntiAlias(true);
			pGreen.setStrokeWidth(10);
			pGreen.setStrokeCap(Paint.Cap.ROUND);
			pGreen.setStyle(Paint.Style.STROKE);

			pRed = new Paint();
			pRed.setColor(0xffff0000);
			pRed.setAntiAlias(true);
			pRed.setStrokeWidth(10);
			pRed.setStrokeCap(Paint.Cap.ROUND);
			pRed.setStyle(Paint.Style.STROKE);
			
			pYellow = new Paint();
			pYellow.setColor(0xffD0EE2E);
			pYellow.setAntiAlias(true);
			pYellow.setStrokeWidth(10);
			pYellow.setStrokeCap(Paint.Cap.ROUND);
			pYellow.setStyle(Paint.Style.STROKE);
			
			paints = new Paint[4];
			paints[0] = pYellow;//pRed;
			paints[1] = pGreen;
			paints[2] = pBlue;
			paints[3] = pRed;
			
			
			//mStartTime = SystemClock.elapsedRealtime();

			mPrefs = Paper.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
			mPrefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPrefs, null);
		}

		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {


			max_hair = prefs.getInt("hairs", 14)+1;
			max_fps = prefs.getInt("fps", 24);
			
			Random r = new Random();
			
			hairs = new Hair[max_hair];
			for(int i=0;i<max_hair;i++){
				hairs[i] = new Hair(r,paints[r.nextInt(3)]);	///set back to 4 to get red
			}
			image = BitmapFactory
					.decodeResource(getResources(), R.drawable.orb);

			 truesize = image.getWidth();
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy() {
			super.onDestroy();
			mHandler.removeCallbacks(mDrawWatson);
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			mVisible = visible;
			if (visible) {
				drawFrame();
			} else {
				mHandler.removeCallbacks(mDrawWatson);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			// store the center of the surface, so we can draw the cube in the
			// right spot
			mCenterX = width / 2.0f;
			mCenterY = height / 2.0f;
			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder) {
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawWatson);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
			mOffset = xOffset;
			drawFrame();
		}

		/*
		 * Store the position of the touch event so we can use it for drawing
		 * later
		 */
		@Override
		public void onTouchEvent(MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				mTouchX = event.getX();
				mTouchY = event.getY();
			} else {
				mTouchX = -1;
				mTouchY = -1;
			}
			super.onTouchEvent(event);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void drawFrame() {
			final SurfaceHolder holder = getSurfaceHolder();
			final Rect frame = holder.getSurfaceFrame();
			final int width = frame.width();
			final int height = frame.height();

			Canvas c = null;
			try {
				c = holder.lockCanvas();
				if (c != null) {
					// draw something
					drawWatson(c,width,height);
					//drawTouchPoint(c);
				}
			} finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawWatson);
			if (mVisible) {
				mHandler.postDelayed(mDrawWatson, 1000 / max_fps);
			}
		}
		
		void drawWatson(Canvas c,int width,int height) {
			c.save();
			c.drawColor(0xff000000);

			if (mTouchX > -1) {
				double dx = mTouchX - mCenterX;
				double dy = mTouchX - mCenterY;
				mOffsetAxis = Math.atan2(dy, dx);				
			}			

								
			
			Rect src = new Rect(0,0,truesize,truesize);
			RectF dst ;
			float s = 1;
			if(height>width){				
				s = width/400.0f;				
				float half = width/2;				
				dst = new RectF(0,mCenterY-half,width,mCenterY+half);
			}else{
				s = height/400.0f;				
				float half = height/2;							

				dst = new RectF(mCenterX-half,0,mCenterX+half,height);
			}
			
			c.drawBitmap(image, src, dst, mPaint);

			
			for(int i=0;i<max_hair;i++){
				hairs[i].Update(mOffsetAxis);
				hairs[i].Draw(c, mCenterX, mCenterY,s);
			}
			
			c.restore();
		}


		void drawTouchPoint(Canvas c) {
			if (mTouchX >= 0 && mTouchY >= 0) {
				c.drawCircle(mTouchX, mTouchY, 80, mPaint);
			}
		}
	}
}
