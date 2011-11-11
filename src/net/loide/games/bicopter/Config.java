package net.loide.games.bicopter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Config extends Activity implements OnClickListener {
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);
		Button btn1 = (Button) findViewById(R.id.btnConnect);
		btn1.setOnClickListener(this);

		TextView tvstate = (TextView) this.findViewById(R.id.stateConnected);
		TextView tvmac = (TextView) this.findViewById(R.id.MacAdress);

		if (MultiWiiBT_menu.prefs.getString("remote_device", "") == "")
			tvstate.setText("Not Connected");
		else {
			tvstate.setText("Connected");
			tvmac.setText(MultiWiiBT_menu.remote_device_mac);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			Intent myIntent = new Intent(Config.this, BTDeviceList.class);

			// MultiWiiBT_menu.this.startActivity(myIntent);

			Config.this
					.startActivityForResult(myIntent, REQUEST_CONNECT_DEVICE);
			Config.this
					.startActivityForResult(myIntent, REQUEST_CONNECT_DEVICE);
			// newGame();
			// return true;
			/*
			 * case R.id.discoverable: // showHelp(); Toast.makeText(this,
			 * "TBD", Toast.LENGTH_LONG).show(); return true;
			 */
			break;
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				MultiWiiBT_menu.remote_device_mac = data.getExtras().getString(
						BTDeviceList.EXTRA_DEVICE_ADDRESS);

				// Save preference in config file
				MultiWiiBT_menu.prefs
						.edit()
						.putString("remote_device",
								MultiWiiBT_menu.remote_device_mac).commit();

				Toast.makeText(
						this,
						"Bluetooth device address "
								+ MultiWiiBT_menu.remote_device_mac + " saved.",
						Toast.LENGTH_SHORT).show();
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
