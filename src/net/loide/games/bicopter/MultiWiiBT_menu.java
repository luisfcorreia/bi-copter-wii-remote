package net.loide.games.bicopter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MultiWiiBT_menu extends Activity implements OnClickListener {

	public BluetoothAdapter mBluetoothAdapter;
	public static BluetoothDevice device;
	public static String remote_device_mac = "";
	public static final int REQUEST_CODE = 0xdeadbeef;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

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
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.cancel();

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

	}

	@Override
	public void onStart() {
		super.onStart();

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}

	public void onClick(View v) {
		Intent myIntent;
		switch (v.getId()) {
		case R.id.startgameBtn:
			// handle button A click;

			// Intent myIntent = new Intent(MultiWiiBT_menu.this,
			// Bluetest.class);

			if (remote_device_mac == "") {
				Toast.makeText(this,"Seleccione um dispositivo Bluetooth!", Toast.LENGTH_LONG).show();
			} else {

				myIntent = new Intent(MultiWiiBT_menu.this, Controller.class);
				MultiWiiBT_menu.this.startActivity(myIntent);
			}
			break;

		case R.id.configBtn:
			// handle button A click;

			// Intent myIntent = new Intent(MultiWiiBT_menu.this,
			// Bluetest.class);
			// myIntent = new Intent(MultiWiiBT_menu.this, Config.class);
			// MultiWiiBT_menu.this.startActivity(myIntent);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.scan:
			Intent myIntent = new Intent(MultiWiiBT_menu.this,
					BTDeviceList.class);
			// MultiWiiBT_menu.this.startActivity(myIntent);
			MultiWiiBT_menu.this.startActivityForResult(myIntent,
					REQUEST_CONNECT_DEVICE);
			// newGame();
			return true;
		case R.id.discoverable:
			// showHelp();
			Toast.makeText(this, "TBD", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						BTDeviceList.EXTRA_DEVICE_ADDRESS);
				remote_device_mac = address;
				// Get the BLuetoothDevice object
				Toast.makeText(this,
						"Bluetooth device address " + remote_device_mac,
						Toast.LENGTH_SHORT).show();
				device = mBluetoothAdapter.getRemoteDevice(address);
				// Attempt to connect to the device
				// mChatService.connect(device);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				// setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Toast.makeText(this, "Bluetooth was not enabled!",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

}