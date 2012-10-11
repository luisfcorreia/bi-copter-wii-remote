package net.loide.games.bicopter;

/*
 * sensor code shamelessly stolen from
 * http://www.netmite.com/android/mydroid/cupcake/development/samples/Compass/src/com/example/android/compass/CompassActivity.java
 */

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
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Controller extends Activity implements OnTouchListener,
		SensorEventListener {

	// Unique UUID for this application
	private SensorManager sensorManager = null;
	private TBlue bt;
	private String bt_rd = "";
	String TAG = "Controller";

	private boolean running = false;
	private int BT_SEND_DELAY = 200;

	private int circleSize = 32;
	private float lX = 150;
	private float lY = 480 - circleSize;

	private Paint mPaint;

	public int mThr = 1;
	public int mYaw = 50;
	public int mPit = 50;
	public int mRol = 50;
	public int mAux = 50;
	public double base_mPit = 0;
	public double base_mRol = 0;
	public int pitch;
	public int roll;
	public int arm = 0;
	public int prearm = 0;

	public static int aPit;
	public static int aRol;
	public static int aYaw;

	private double aaPit = 0.0;
	private double aaRol = 0.0;
	private double aaYaw = 0.0;

	// for roll & pitch accel/magneto reading
	private float[] mOrientation = new float[3];
	private float[] mGData = new float[3];
	private float[] mMData = new float[3];
	private float[] mR = new float[16];
	private float[] mI = new float[16];
	private float rad2deg = (float) (180.0f / Math.PI);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new MyView(this));
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStrokeWidth(4);

		bt = new TBlue(MultiWiiBT.remote_device_mac);

		/*
		 * Read values from config file
		 */
		aYaw = MultiWiiBT.prefs.getInt("yaw_percent", 50);
		aPit = MultiWiiBT.prefs.getInt("pitch_percent", 50);
		aRol = MultiWiiBT.prefs.getInt("roll_percent", 50);

		aaRol = aRol / 100.0;
		aaPit = aPit / 100.0;
		aaYaw = aYaw / 100.0;

		if (bt.connected) {
			running = true;
			CommLink.run();
		} else {
			Toast.makeText(this, getString(R.string.btdev_una),
					Toast.LENGTH_LONG).show();
			finish();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public class MyView extends View {

		private Canvas mCanvas;
		private float x = 0, y = 0;
		private float dx1 = 0, dy1 = 0;
		private static final float TOUCH_TOLERANCE = 4;
		/*
		 * posicionamento ajustavel para a pub private int topline = 260;
		 */
		private int topline = 200;

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
			canvas.drawText("MultiWiiBT UI v" + MultiWiiBT.UI_VERSION, 360, 45,
					mPaint);

			// desenhar arm/disarm button
			mPaint.setColor(0xFF660000);
			canvas.drawRect(600, 70, 800, 130, mPaint);
			mPaint.setTextSize(42);
			mPaint.setColor(0xFF00FF00);
			if (arm == 1) {
				canvas.drawText("-DISARM-", 610, 115, mPaint);
			} else {
				canvas.drawText("--ARM---", 610, 115, mPaint);
			}

			mPaint.setTextSize(20);
			mPaint.setColor(0xFFFFFFFF);
			canvas.drawText("Pitch  : " + pitch, 310, 120, mPaint);
			canvas.drawText("Roll    : " + roll, 310, 140, mPaint);

			canvas.drawText(bt_rd, 310, 160, mPaint);

			// desenhar stick esquerdo
			mPaint.setColor(0xFFCDE3A1);
			canvas.drawCircle(lX, lY, circleSize, mPaint);

			// desenhar simulador de sticks
			mPaint.setColor(0xFFAAAAAA);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(360, topline + 0, 560, topline + 200, mPaint);
			canvas.drawLine(460, topline + 0, 460, topline + 200, mPaint);
			canvas.drawLine(360, topline + 100, 560, topline + 100, mPaint);
			canvas.drawRect(580, topline + 0, 780, topline + 200, mPaint);
			canvas.drawLine(680, topline + 0, 680, topline + 200, mPaint);
			canvas.drawLine(580, topline + 100, 780, topline + 100, mPaint);

			// desenhar posição calculada dos sticks
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(0xFFCDE3A1);
			canvas.drawCircle(360 + (mYaw * 2),
					Math.abs(topline + (200 - (mThr * 2))), circleSize / 2,
					mPaint);
			canvas.drawCircle(580 + (mRol * 2),
					Math.abs(topline + (200 - (mPit * 2))), circleSize / 2,
					mPaint);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			x = event.getX();
			y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				/*
				 * Detect throttle & yaw press
				 */
				if ((x > 1) && (x < 300)) {
					if ((y > 1) && (y < 480)) {
						lX = x;
						lY = y;
						base_mPit = pitch;
						base_mRol = roll;
					}
				}
				/*
				 * Detect press in the ARM area
				 */
				if ((x > 600) && (x < 800)) {
					if ((y > 70) && (y < 130)) {
						prearm = 1;
					}
				}
				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				int maxP;
				int maxR;
				int fifty = 50;
				if ((x > 1) && (x < 300)) {
					if ((y > 1) && (y < 480)) {
						dx1 = Math.abs(x - lX);
						dy1 = Math.abs(y - lY);
						if (dx1 >= TOUCH_TOLERANCE || dy1 >= TOUCH_TOLERANCE) {
							lX = x;
							lY = y;
							maxP = (int) (base_mPit - pitch);
							maxR = (int) (base_mRol - roll);
							if (maxP > fifty) {
								maxP = fifty;
							}
							if (maxP < -fifty) {
								maxP = -fifty;
							}
							if (maxR > fifty) {
								maxR = fifty;
							}
							if (maxR < -fifty) {
								maxR = -fifty;
							}
							mPit = maxP + fifty;
							mRol = maxR + fifty;

							mThr = (int) FloatMath
									.floor(Math.abs(lY - 480) * 100 / 480);
							mYaw = (int) (lX * 100 / 300);

						}
					}
				}
				invalidate();
				break;

			case MotionEvent.ACTION_UP:

				lX = 150;
				base_mPit = 50;
				base_mRol = 50;
				mPit = 50;
				mRol = 50;
				mYaw = 50;

				/*
				 * If this press was started AND stopped in the ARM area, toggle
				 * the switch
				 */
				if ((prearm == 1) && (x > 600) && (x < 800)) {
					if ((y > 70) && (y < 130)) {
						prearm = 0;
						if (arm == 0) {
							arm = 1;
						} else {
							arm = 0;
						}
					}
				}
				invalidate();
				break;
			}
			invalidate();
			return true;
		}

		public void setmCanvas(Canvas mCanvas) {
			this.mCanvas = mCanvas;
		}

		public Canvas getmCanvas() {
			return mCanvas;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Sensor gsensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor msensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(this, gsensor,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(this, msensor,
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

	@Override
	public void onSensorChanged(SensorEvent event) {
		int type = event.sensor.getType();
		float[] data;
		if (type == Sensor.TYPE_ACCELEROMETER) {
			data = mGData;
		} else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
			data = mMData;
		} else {
			// we should not be here.
			return;
		}
		for (int i = 0; i < 3; i++)
			data[i] = event.values[i];

		SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
		SensorManager.getOrientation(mR, mOrientation);

		roll = (int) Math.round(mOrientation[1] * rad2deg);
		pitch = (int) Math.round(mOrientation[2] * rad2deg);

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	private Handler mHandler = new Handler();

	private Runnable CommLink = new Runnable() {

		@Override
		public void run() {
			int mt, mr, mp, my, ma;
			char[] mw = new char[] { 0x10, 0xc8, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00 };
			int checksum = 0;
			int THROTTLE = 1100;
			int PITCH = 1500;
			int ROLL = 1500;
			int YAW = 1500;
			int AUX1 = 1500;
			int AUX2 = 1500;
			int AUX3 = 1500;
			int AUX4 = 1500;
			String comms = "";

			if (running) {

				mThr = (int) FloatMath.floor(Math.abs(lY - 480) * 100 / 480);
				mYaw = (int) ((lX * 99 / 300) + 1);

				/*
				 * Values now go from 0-100 for Throttle All others go from -50
				 * +50
				 */
				mt = mThr;
				my = mYaw - 50;
				mr = mRol - 50;
				mp = mPit - 50;
				ma = (arm * 100) - 50;

				/*
				 * factor in the influence in percentage
				 */
				mr = (int) (mr * aaRol);
				mp = (int) (mp * aaPit);
				my = (int) (my * aaYaw);

				/*
				 * translate values to pulse data
				 */
				THROTTLE = THROTTLE + (mt * 6);
				ROLL = ROLL + (mr * 6);
				PITCH = PITCH + (mp * 6);
				YAW = YAW + (my * 6);
				AUX1 = AUX1 + (ma * 6);

				Log.i(TAG, "r " + ROLL);
				Log.i(TAG, "p " + PITCH);
				Log.i(TAG, "y " + YAW);
				Log.i(TAG, "t " + THROTTLE);
				Log.i(TAG, "a " + AUX1);

				/*
				 * MultiWii header
				 */
				/*
				 * comando[0] = '$'; comando[1] = 'M'; comando[2] = '<';
				 */
				mw[0] = 0x10;
				mw[1] = 0xc8;

				/*
				 * #define ROLL 0
				 */
				mw[2] = (char) (ROLL & 0xff);
				mw[3] = (char) (ROLL >> 8);

				/*
				 * #define PITCH 1
				 */
				mw[4] = (char) (PITCH & 0xff);
				mw[5] = (char) (PITCH >> 8);

				/*
				 * #define YAW 2
				 */
				mw[6] = (char) (YAW & 0xff);
				mw[7] = (char) (YAW >> 8);

				/*
				 * #define THROTTLE 3
				 */
				mw[8] = (char) (THROTTLE & 0xff);
				mw[9] = (char) (THROTTLE >> 8);

				/*
				 * #define AUX1 4
				 */
				mw[10] = (char) (AUX1 & 0xff);
				mw[11] = (char) (AUX1 >> 8);

				/*
				 * #define AUX2 5
				 */
				mw[12] = (char) (AUX2 & 0xff);
				mw[13] = (char) (AUX2 >> 8);

				/*
				 * #define AUX3 6
				 */
				mw[14] = (char) (AUX3 & 0xff);
				mw[15] = (char) (AUX3 >> 8);

				/*
				 * #define AUX4 7
				 */
				mw[16] = (char) (AUX4 & 0xff);
				mw[17] = (char) (AUX4 >> 8);

				/*
				 * calculate checksum
				 */
				checksum = 0;
				for (int i = 0; i < 17; i++) {
					checksum ^= (mw[i] & 0xFF);
				}
				mw[17] = (char) checksum;

				/*
				 * Send string to MultiWii device
				 */
				comms = String.valueOf(mw);
				bt.write("$M<" + comms);

				Log.i(TAG, "$M<" + comms);

				Log.i(TAG, "BT read" + bt.read());
				mHandler.postDelayed(CommLink, BT_SEND_DELAY);
			}
		}
	};
}
