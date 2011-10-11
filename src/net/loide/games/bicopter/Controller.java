package net.loide.games.bicopter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Controller extends Activity implements OnTouchListener {
	private static final String TAG = "Touch";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new MyView(this));

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStrokeWidth(12);
	}

	private Paint mPaint;

	public class MyView extends View {

		private Canvas mCanvas;

		private int circleSize = 32;
		private float lX = 175;
		private float lY = 390 - circleSize;
		private float rX = 625;
		private float rY = 240;

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
			canvas.drawRect(25, 90, 325, 390, mPaint);

			// desenhar quadrado direito
			mPaint.setColor(0xFF92C957);
			canvas.drawRect(475, 90, 775, 390, mPaint);

			// desenhar takeoff
			mPaint.setColor(0xFF0011EE);
			Path path = new Path();
			path.moveTo(320, 0);
			path.lineTo(350, 50);
			path.lineTo(450, 50);
			path.lineTo(480, 0);
			path.lineTo(320, 0);
			path.close();
			canvas.drawPath(path, mPaint);

			// desenhar poweroff
			mPaint.setColor(0xFFEE0000);
			Path path1 = new Path();
			path1.moveTo(320, 480);
			path1.lineTo(350, 430);
			path1.lineTo(450, 430);
			path1.lineTo(480, 480);
			path1.lineTo(320, 480);
			path1.close();
			canvas.drawPath(path1, mPaint);

			// desenhar stick esquerdo
			mPaint.setColor(0xFFCDE3A1);
			// canvas.drawCircle(175, 240, 48, mPaint);
			canvas.drawCircle(lX, lY, circleSize, mPaint);

			// desenhar stick direito
			mPaint.setColor(0xFFCDE3A1);
			// canvas.drawCircle(625, 240, 48, mPaint);
			canvas.drawCircle(rX, rY, circleSize, mPaint);

		}

		public boolean onTouchEvent(MotionEvent event) {

			float x1 = 0, y1 = 0, x2 = 0, y2 = 0;
			float dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

			int pointerCount = event.getPointerCount();

			if (pointerCount == 1) {
				x1 = event.getX();
				y1 = event.getY();
			}

			if (pointerCount == 2) {
				x1 = event.getX(0);
				y1 = event.getY(0);
				x2 = event.getX(1);
				y2 = event.getY(1);
			}

			Log.d(TAG, "num pointers:" + pointerCount);

			// Dump touch event to log
			// dumpEvent(event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if ((x1 > 25) && (x1 < 325) && (y1 > 90) && (y1 < 390)) {
					lX = x1;
					lY = y1;
				}
				if ((x1 > 475) && (x1 < 775) && (y1 > 90) && (y1 < 390)) {
					rX = x1;
					rY = y1;
				}
				invalidate();
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				if ((x2 > 25) && (x2 < 325) && (y2 > 90) && (y2 < 390)) {
					lX = x2;
					lY = y2;
				}
				if ((x2 > 475) && (x2 < 775) && (y2 > 90) && (y2 < 390)) {
					rX = x2;
					rY = y2;
				}

				invalidate();
				break;

			case MotionEvent.ACTION_MOVE:
				// testar pointer 1 no quadrado esquerdo
				if ((x1 > 25) && (x1 < 325) && (y1 > 90) && (y1 < 390)) {
					dx1 = Math.abs(x1 - lX);
					dy1 = Math.abs(y1 - lY);
					if (dx1 >= TOUCH_TOLERANCE || dy1 >= TOUCH_TOLERANCE) {
						lX = x1;
						lY = y1;
					}
				}
				// testar pointer 2 no quadrado esquerdo
				if ((x2 > 25) && (x2 < 325) && (y2 > 90) && (y2 < 390)) {
					dx2 = Math.abs(x2 - lX);
					dy2 = Math.abs(y2 - lY);
					if (dx2 >= TOUCH_TOLERANCE || dy2 >= TOUCH_TOLERANCE) {
						lX = x2;
						lY = y2;
					}
				}
				// testar pointer 1 no quadrado direito
				if ((x1 > 475) && (x1 < 775) && (y1 > 90) && (y1 < 390)) {
					dx1 = Math.abs(x1 - lX);
					dy1 = Math.abs(y1 - lY);
					if (dx1 >= TOUCH_TOLERANCE || dy1 >= TOUCH_TOLERANCE) {
						rX = x1;
						rY = y1;
					}
				}
				// testar pointer 2 no quadrado direito
				if ((x2 > 475) && (x2 < 775) && (y2 > 90) && (y2 < 390)) {
					dx2 = Math.abs(x2 - lX);
					dy2 = Math.abs(y2 - lY);
					if (dx2 >= TOUCH_TOLERANCE || dy2 >= TOUCH_TOLERANCE) {
						rX = x2;
						rY = y2;
					}
				}

				invalidate();
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				if ((x1 > 25) && (x1 < 325) && (y1 > 90) && (y1 < 390)) {
					lX = 175;
					lY = 390 - circleSize;
				}
				if ((x2 > 475) && (x2 < 775) && (y2 > 90) && (y2 < 390)) {
					rX = 625;
					rY = 240;
				}
				invalidate();
				break;
			}

			return true;
		}

		public void setmCanvas(Canvas mCanvas) {
			this.mCanvas = mCanvas;
		}

		public Canvas getmCanvas() {
			return mCanvas;
		}

		/** Show an event in the LogCat view, for debugging */
		private void dumpEvent(MotionEvent event) {
			// ...
			String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
					"POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
			StringBuilder sb = new StringBuilder();
			int action = event.getAction();
			int actionCode = action & MotionEvent.ACTION_MASK;
			sb.append("event ACTION_").append(names[actionCode]);
			if (actionCode == MotionEvent.ACTION_POINTER_DOWN
					|| actionCode == MotionEvent.ACTION_POINTER_UP) {
				sb.append("(pid ").append(
						action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
				sb.append(")");
			}
			sb.append("[");
			for (int i = 0; i < event.getPointerCount(); i++) {
				sb.append("#").append(i);
				sb.append("(pid ").append(event.getPointerId(i));
				sb.append(")=").append((int) event.getX(i));
				sb.append(",").append((int) event.getY(i));
				if (i + 1 < event.getPointerCount())
					sb.append(";");
			}
			sb.append("]");
			Log.d(TAG, sb.toString());
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
}
