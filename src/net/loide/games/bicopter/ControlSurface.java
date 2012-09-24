package net.loide.games.bicopter;

import java.util.Random;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.view.SurfaceView;

// TODO ver o lunar lander!!!

public class ControlSurface extends Activity implements OnTouchListener,
		SensorEventListener {

	private static String adID = "a14eced1c1c11aa";
	MySurfaceView mySurfaceView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mySurfaceView = new MySurfaceView(this);
		setContentView(R.layout.controlsurface);

		//mySurfaceView = findViewById(R.id.surface);
		mySurfaceView = (MySurfaceView) findViewById(R.id.surface);
		
		/*
		 * Load AD into screen
		 */
		LinearLayout layout = (LinearLayout) findViewById(R.id.rightAd);
		AdView adView = new AdView(this, AdSize.BANNER, adID);
		layout.addView(adView);

		AdRequest request = new AdRequest();
		adView.loadAd(request);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mySurfaceView.onResumeMySurfaceView();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mySurfaceView.onPauseMySurfaceView();
	}

	class MySurfaceView extends SurfaceView implements Runnable {

		Thread thread = null;
		SurfaceHolder surfaceHolder;
		volatile boolean running = false;
		private float x = 0, y = 0;
		private float dx1 = 0, dy1 = 0;
		private static final float TOUCH_TOLERANCE = 4;

		volatile boolean touched = false;
		volatile float touched_x, touched_y;

		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Random random;

		public MySurfaceView(Context context) {
			super(context);
			surfaceHolder = getHolder();
			random = new Random();
		}

		public void onResumeMySurfaceView() {
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void onPauseMySurfaceView() {
			boolean retry = true;
			running = false;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (running) {
				if (surfaceHolder.getSurface().isValid()) {
					Canvas canvas = surfaceHolder.lockCanvas();
					// ... actual drawing on canvas

					paint.setStyle(Paint.Style.STROKE);
					paint.setStrokeWidth(3);

					int w = canvas.getWidth();
					int h = canvas.getHeight();

					// desenhar quadrado esquerdo
					paint.setColor(0xFF92C957);
					canvas.drawRect(0, 0, 300, h - 1, paint);

					if (touched) {
						paint.setStrokeWidth(50);
						paint.setColor(Color.YELLOW);
						canvas.drawPoint(touched_x, touched_y, paint);
					}

					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub

			touched_x = event.getX();
			touched_y = event.getY();

			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				touched = true;
				break;
			case MotionEvent.ACTION_MOVE:
				touched = true;
				break;
			case MotionEvent.ACTION_UP:
				touched = false;
				break;
			case MotionEvent.ACTION_CANCEL:
				touched = false;
				break;
			case MotionEvent.ACTION_OUTSIDE:
				touched = false;
				break;
			default:
			}
			return true; // processed
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
