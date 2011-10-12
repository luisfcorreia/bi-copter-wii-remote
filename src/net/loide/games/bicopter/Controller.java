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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public class MyView extends View {

		private Canvas mCanvas;

		private int circleSize = 32;
		private float lX = 150;
		private float lY = 480 - circleSize;
/*
		private float rX = 625;
		private float rY = 240;
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

			mPaint.setColor(0xFFFFFFFF);
			canvas.drawText("Pitch  : "+MultiWiiBT_menu.pitch, 500, 20, mPaint);
			canvas.drawText("Roll   : "+MultiWiiBT_menu.roll,  500, 40, mPaint);
			canvas.drawText("Yaw    : "+MultiWiiBT_menu.yaw,   500, 60, mPaint);
			
			mPaint.setColor(0xFFFFFFFF);
			canvas.drawText("accX   : "+MultiWiiBT_menu.accX,  500, 80, mPaint);
			canvas.drawText("accY   : "+MultiWiiBT_menu.accY,  500,100, mPaint);
			canvas.drawText("accZ   : "+MultiWiiBT_menu.accZ,  500,120, mPaint);
			
			// desenhar stick esquerdo
			mPaint.setColor(0xFFCDE3A1);
			canvas.drawCircle(lX, lY, circleSize, mPaint);

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
				//lY = 480 - circleSize;
				invalidate();
				break;
			}

			dumpEvent(event);
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

		private void dumpEvent(MotionEvent event) {
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

}
