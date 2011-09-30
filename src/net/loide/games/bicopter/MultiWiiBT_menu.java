package net.loide.games.bicopter;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class MultiWiiBT_menu extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		SensorManager mySensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> mySensors = mySensorManager
				.getSensorList(Sensor.TYPE_ORIENTATION);

		boolean sensorrunning;
		if (mySensors.size() > 0) {

			SensorEventListener mySensorEventListener = null;
			mySensorManager.registerListener(mySensorEventListener,
					mySensors.get(0), SensorManager.SENSOR_DELAY_GAME);
			// .get(0), SensorManager.SENSOR_DELAY_UI);

			sensorrunning = true;

		} else {
			Toast.makeText(this, "No ORIENTATION Sensor", Toast.LENGTH_LONG)
					.show();
			sensorrunning = false;
			finish();
		}

		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.cancel();

		Button button1 = (Button) findViewById(R.id.startgameBtn);
		Button button3 = (Button) findViewById(R.id.exitBtn);

		// button1.setVisibility(View.INVISIBLE);

		button1.setOnClickListener(this);
		button3.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.startgameBtn:
			// handle button A click;

			Intent myIntent = new Intent(MultiWiiBT_menu.this, Bluetest.class);
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

}