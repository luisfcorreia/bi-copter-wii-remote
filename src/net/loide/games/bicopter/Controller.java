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

		private void touch_start(float x, float y) {
			lX = x;
			lY = y;
		}

		private void touch_move(float x, float y) {
			float dx = Math.abs(x - lX);
			float dy = Math.abs(y - lY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				lX = x;
				lY = y;
			}
		}

		private void touch_up() {

			lX = 175;
			lY = 390 - circleSize;
		}

		public boolean onTouchEvent(MotionEvent event) {

			int pointerCount = event.getPointerCount();

			// Dump touch event to log
			dumpEvent(event);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				float y = event.getY();
				touch_start(x, y);
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				rX = event.getX(1);
				rY = event.getY(1);

				break;

			case MotionEvent.ACTION_MOVE:
				touch_move(event.getX(), event.getY());
				break;

			case MotionEvent.ACTION_UP:
				touch_up();
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
