package net.loide.games.bicopter;

import fi.sulautetut.android.tblueclient.TBlue;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Controller extends Activity implements OnTouchListener,
		SensorEventListener {

	// Unique UUID for this application
	public static String pitch;
	public static String roll;
	public static String yaw;
	public static String accX;
	public static String accY;
	public static String accZ;
	private SensorManager sensorManager = null;
	private TBlue bt;
	private String mac = "";
	private boolean running = false;
	private int BT_SEND_DELAY = 100;

	private int circleSize = 32;
	private float lX = 150;
	private float lY = 480 - circleSize;

	public int mThr = 0;
	public int mYaw = 0;
	public int mPit = 0;
	public int mRol = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new MyView(this));

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStrokeWidth(4);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		mac = MultiWiiBT_menu.remote_device_mac;
		bt = new TBlue(mac);

		running = true;
		CommLink.run();

	}

	private Paint mPaint;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public class MyView extends View {

		private Canvas mCanvas;

		/*
		 * private float rX = 625; private float rY = 240;
		 */
		private float x = 0, y = 0;
		private float dx1 = 0, dy1 = 0;

		private static final float TOUCH_TOLERANCE = 4;

		public MyView(Context c) {
			super(c);
		}

		public void onCreate() {
			android.graphics.Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			Bitmap mBitmap = Bitmap.createBitmap(480, 800, conf);
			setmCanvas(new Canvas(mBitmap));
		}

		@Override
		protected void onDraw(Canvas canvas) {

			// desenhar quadrado esquerdo
			mPaint.setColor(0xFF92C957);
			canvas.drawRect(0, 0, 300, 480, mPaint);

			// desenhar quadrado esquerdo
			mPaint.setColor(0xFF000066);
			canvas.drawRect(301, 0, 800, 60, mPaint);

			// desenhar app name
			mPaint.setColor(0xFF00FFFF);
			mPaint.setTextSize(42);
			canvas.drawText("MultiWiiBT UI v" + MultiWiiBT_menu.UI_VERSION,
					360, 45, mPaint);

			// desenhar arm/disarm button
			mPaint.setColor(0xFF660000);
			canvas.drawRect(600, 70, 800, 130, mPaint);
			mPaint.setTextSize(42);
			mPaint.setColor(0xFF00FF00);
			canvas.drawText("--ARM---", 610, 115, mPaint);
			canvas.drawText("-DISARM-", 610, 115, mPaint);

			mPaint.setTextSize(20);
			mPaint.setColor(0xFFFFFFFF);
			canvas.drawText("Pitch  : " + pitch, 310, 120, mPaint);
			canvas.drawText("Roll    : " + roll, 310, 140, mPaint);
			canvas.drawText("Yaw    : " + yaw, 310, 160, mPaint);

			mPaint.setColor(0xFFFFFFFF);
			canvas.drawText("accX   : " + accX, 310, 180, mPaint);
			canvas.drawText("accY   : " + accY, 310, 200, mPaint);
			canvas.drawText("accZ   : " + accZ, 310, 220, mPaint);

			canvas.drawText("BlueT  : " + bt.read(), 310, 240, mPaint);

			// desenhar stick esquerdo
			mPaint.setColor(0xFFCDE3A1);
			canvas.drawCircle(lX, lY, circleSize, mPaint);

			// desenhar simulador de sticks
			mPaint.setColor(0xFFAAAAAA);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(360, 260, 560, 460, mPaint);
			canvas.drawLine(460, 260, 460, 460, mPaint);
			canvas.drawLine(360, 360, 560, 360, mPaint);
			canvas.drawRect(580, 260, 780, 460, mPaint);
			canvas.drawLine(680, 260, 680, 460, mPaint);
			canvas.drawLine(580, 360, 780, 360, mPaint);

			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(0xFFCDE3A1);
			canvas.drawCircle(460, 360, circleSize / 2, mPaint);
			canvas.drawCircle(680, 360, circleSize / 2, mPaint);

		}

		public boolean onTouchEvent(MotionEvent event) {

			x = event.getX();
			y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if ((x > 0) && (x < 300)) {
					if ((y > 0) && (y < 480)) {
						lX = x;
						lY = y;
					}
				}
				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				if ((x > 0) && (x < 300)) {
					if ((y > 0) && (y < 480)) {
						dx1 = Math.abs(x - lX);
						dy1 = Math.abs(y - lY);
						if (dx1 >= TOUCH_TOLERANCE || dy1 >= TOUCH_TOLERANCE) {
							lX = x;
							lY = y;
						}
					}
				}
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				lX = 150;
				invalidate();
				break;
			}

			// dumpEvent(event);
			// Log.d(TAG, "Left:" + lX + " " + lY );

			return true;
		}

		public void setmCanvas(Canvas mCanvas) {
			this.mCanvas = mCanvas;
		}

		public Canvas getmCanvas() {
			return mCanvas;
		}

		/** Show an event in the LogCat view, for debugging */

		/*
		 * private void dumpEvent(MotionEvent event) { String names[] = {
		 * "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN",
		 * "POINTER_UP", "7?", "8?", "9?" }; StringBuilder sb = new
		 * StringBuilder(); int action = event.getAction(); int actionCode =
		 * action & MotionEvent.ACTION_MASK;
		 * sb.append("event ACTION_").append(names[actionCode]); if (actionCode
		 * == MotionEvent.ACTION_POINTER_DOWN || actionCode ==
		 * MotionEvent.ACTION_POINTER_UP) { sb.append("(pid ").append( action >>
		 * MotionEvent.ACTION_POINTER_ID_SHIFT); sb.append(")"); }
		 * sb.append("["); for (int i = 0; i < event.getPointerCount(); i++) {
		 * sb.append("#").append(i);
		 * sb.append("(pid ").append(event.getPointerId(i));
		 * sb.append(")=").append((int) event.getX(i));
		 * sb.append(",").append((int) event.getY(i)); if (i + 1 <
		 * event.getPointerCount()) sb.append(";"); } sb.append("]"); Log.d(TAG,
		 * sb.toString()); }
		 */
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Register this class as a listener for the accelerometer sensor
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		// ...and the orientation sensor
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		// Unregister the listener
		sensorManager.unregisterListener(this);
		running = false;
		bt.close();
		super.onStop();
	}

	public void onSensorChanged(SensorEvent sensorEvent) {
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			int x = Math.round(sensorEvent.values[0]);
			int y = Math.round(sensorEvent.values[1]);
			int z = Math.round(sensorEvent.values[2]);
			accX = String.valueOf((int) (Math.abs(x)));
			accY = String.valueOf((int) (Math.abs(y)));
			accZ = String.valueOf((int) (Math.abs(z)));
		}

		if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			int x = Math.round(sensorEvent.values[0]);
			int y = Math.round(sensorEvent.values[1]);
			int z = Math.round(sensorEvent.values[2]);
			pitch = String.valueOf((int) (Math.abs(x)));
			roll = String.valueOf((int) (Math.abs(y)));
			yaw = String.valueOf((int) (Math.abs(z)));
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	private Handler mHandler = new Handler();
	private Runnable CommLink = new Runnable() {

		public void run() {
			if (running) {

				mThr = (int) (Math.abs(lY - 480) * 100 / 480);
				mYaw = (int) lX * 100 / 300;
				mPit = (int) 50;
				mRol = (int) 50;
/*				
	      rcData[THROTTLE] = (Serial.read() * 5) + 900;
	      rcData[ROLL]     = (Serial.read() * 5) + 900;
	      rcData[PITCH]    = (Serial.read() * 5) + 900;
	      rcData[YAW]      = (Serial.read() * 5) + 900;
*/				
				bt.write("Z" + mThr + mRol + mPit + mYaw);
				mHandler.postDelayed(CommLink, BT_SEND_DELAY);
			}
		}
	};
}