package net.loide.games.bicopter;

import fi.sulautetut.android.tblueclient.TBlue;
import java.util.LinkedList;
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
import android.util.Log;

public class Controller extends Activity implements OnTouchListener,
		SensorEventListener {

	// Unique UUID for this application
	private SensorManager sensorManager = null;
	private TBlue bt;
	private String bt_rd = "";
	private String mac = "";
	private boolean running = false;
	private int BT_SEND_DELAY = 10;

	private int circleSize = 32;
	private float lX = 150;
	private float lY = 480 - circleSize;

	public int mThr = 50;
	public int mYaw = 50;
	public int mPit = 50;
	public int mRol = 50;
	public int mAux = 50;
	public float base_mPit = 0;
	public float base_mRol = 0;
	public int pitch;
	public int roll;
	public int arm = 0;
	public int prearm = 0;
	String TAG = "Controller";
	public MovingAverage Thr;
	public MovingAverage Yaw;
	public MovingAverage Pit;
	public MovingAverage Rol;
	public MovingAverage Aux;

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
		Thr.count = 5;
		Yaw.count = 5;
		Pit.count = 5;
		Rol.count = 5;
		Aux.count = 5;
	}

	private Paint mPaint;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	public class MyView extends View {

		private Canvas mCanvas;
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
			if (arm == 1) {
				canvas.drawText("-DISARM-", 610, 115, mPaint);
			} else {
				canvas.drawText("--ARM---", 610, 115, mPaint);
			}

			mPaint.setTextSize(20);
			mPaint.setColor(0xFFFFFFFF);
			canvas.drawText("Pitch  : " + pitch, 310, 120, mPaint);
			canvas.drawText("Roll    : " + roll, 310, 140, mPaint);

			canvas.drawText("BlueT : " + bt_rd, 310, 240, mPaint);

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

			// desenhar posição calculada dos sticks
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(0xFFCDE3A1);
			canvas.drawCircle(360 + (mYaw * 2),
					Math.abs(260 + (200 - (mThr * 2))), circleSize / 2, mPaint);
			canvas.drawCircle(580 + (mRol * 2),
					Math.abs(260 + (200 - (mPit * 2))), circleSize / 2, mPaint);
		}

		public boolean onTouchEvent(MotionEvent event) {

			x = event.getX();
			y = event.getY();

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if ((x > 1) && (x < 300)) {
					if ((y > 1) && (y < 480)) {
						lX = x;
						lY = y;
						base_mPit = pitch;
						base_mRol = roll;
					}
				}
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
							Pit.pushValue(mPit);
							Rol.pushValue(mRol);
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
				Pit.pushValue(mPit);
				Rol.pushValue(mRol);
				Yaw.pushValue(mYaw);

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
		if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			roll = Math.round(sensorEvent.values[1]);
			pitch = Math.round(sensorEvent.values[2]);
		}
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	private Handler mHandler = new Handler();
	private Runnable CommLink = new Runnable() {
		private String data = "";

		public void run() {
			int mt, mr, mp, my, ma;
			if (running) {

				/*
				 * mThr = (int) (Math.abs(lY - 480) * 99 / 480) + 1; mYaw =
				 * (int) (lX * 99 / 300) + 1; mAux = (int) (arm * 99) + 1;
				 */
				Thr.pushValue(Math.abs(lY - 480) * 100 / 480);
				Yaw.pushValue(lX * 100 / 300);
				Aux.pushValue((arm * 99) + 1);
				
				mThr = (int) Thr.getValue();
				mYaw = (int) Yaw.getValue();
				mAux = (int) Aux.getValue();
				mRol = (int) Rol.getValue();
				mPit = (int) Pit.getValue();

				mt = mThr * 255 / 100;
				mr = mRol * 255 / 100;
				mp = mPit * 255 / 100;
				my = mYaw * 255 / 100;
				ma = mAux * 255 / 100;

				data = "K" + (char) mt + (char) mr + (char) mp + (char) my
						+ (char) ma;
				bt.write(data);
				bt_rd = bt.read();

				Log.i(TAG, "Throttle:" + mThr + " Yaw:" + mYaw + " Roll:"
						+ mRol + " Pitch:" + mPit + " Aux1:" + mAux);
				mHandler.postDelayed(CommLink, BT_SEND_DELAY);
			}
		}
	};

	/**
	 * Simple Moving Average
	 */

    /**
     * A simple moving average implementation.
     *
     * SMA (Simple moving average) sometimes called rolling average, or running average (mean).
     * see: http://en.wikipedia.org/wiki/Moving_average.
     *
     * @author scottkirkwood
     */
    public class MovingAverage {
        private float circularBuffer[];
        private float mean;
        private int circularIndex;
        private int count;

        public MovingAverage(int size) {
            circularBuffer = new float[size];
            reset();
        }

        /**
         * Get the current moving average.
         * @see com.forusers.android.filter.Filter#getValue()
         */
        public float getValue() {
            return mean;
        }

        /**
         * @see com.forusers.android.filter.Filter#pushValue(float)
         */
        public void pushValue(float x) {
            if (count++ == 0) {
                primeBuffer(x);
            }
            float lastValue = circularBuffer[circularIndex];
            mean = mean + (x - lastValue) / circularBuffer.length;
            circularBuffer[circularIndex] = x;
            circularIndex = nextIndex(circularIndex);
        }

        /*
         * @see com.forusers.android.filter.Filter#reset()
         */
        public void reset() {
            count = 0;
            circularIndex = 0;
            mean = 0;
        }

        public long getCount() {
            return count;
        }

        private void primeBuffer(float val) {
            for (int i = 0; i < circularBuffer.length; ++i) {
                circularBuffer[i] = val;
            }
            mean = val;
        }

        private int nextIndex(int curIndex) {
            if (curIndex + 1 >= circularBuffer.length) {
                return 0;
            }
            return curIndex + 1;
        }
    }
}