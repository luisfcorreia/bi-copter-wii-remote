package net.loide.games.bicopter;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MultiWiiBT_menu extends Activity implements OnClickListener,
		SensorEventListener {

	public static String pitch;
	public static String roll;
	public static String yaw;
	public static String accX;
	public static String accY;
	public static String accZ;
	public BluetoothAdapter mBluetoothAdapter;

	private SensorManager sensorManager = null;
	private Vibrator vibrator = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button button1 = (Button) findViewById(R.id.startgameBtn);
		Button button2 = (Button) findViewById(R.id.configBtn);
		Button button3 = (Button) findViewById(R.id.exitBtn);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);

		// init sensors
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.cancel();

		// Get local Bluetooth adapter
	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	    // If the adapter is null, then Bluetooth is not supported
	    if (mBluetoothAdapter == null) {
	        Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	        finish();
	        return;
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
		super.onStop();
	}

	public void onClick(View v) {
		Intent myIntent;
		switch (v.getId()) {
		case R.id.startgameBtn:
			// handle button A click;

			// Intent myIntent = new Intent(MultiWiiBT_menu.this,
			// Bluetest.class);
			myIntent = new Intent(MultiWiiBT_menu.this, Controller.class);
			MultiWiiBT_menu.this.startActivity(myIntent);
			break;

		case R.id.configBtn:
			// handle button A click;

			// Intent myIntent = new Intent(MultiWiiBT_menu.this,
			// Bluetest.class);
			myIntent = new Intent(MultiWiiBT_menu.this, Config.class);
			MultiWiiBT_menu.this.startActivity(myIntent);
			break;

		/*
		 * 
		 * if (scannedText.compareTo("") == 0) { Toast.makeText(this,
		 * "Primeiro tem que ler o código de barras!",
		 * Toast.LENGTH_LONG).show();
		 * 
		 * } else {
		 * 
		 * // enviar mensagem para o servidor passar ao estado Jogo
		 * 
		 * Intent myIntent = new Intent(Entrymenu.this, Battlefield.class);
		 * Entrymenu.this.startActivity(myIntent); break; }
		 */

		case R.id.exitBtn:
			// handle button B click;
			MultiWiiBT_menu.this.finish();
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public void onSensorChanged(SensorEvent sensorEvent) {
		synchronized (this) {
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
				roll = String.valueOf((int)  (Math.abs(y)));
				yaw = String.valueOf((int)   (Math.abs(z)));

			}
		}
	}
}