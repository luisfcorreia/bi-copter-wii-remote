package net.loide.games.bicopter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
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
		Button btn2 = (Button) findViewById(R.id.btnSave);
		btn2.setOnClickListener(this);
		SeekBar sbYAW = (SeekBar) findViewById(R.id.sbYAW);
		SeekBar sbPitch = (SeekBar) findViewById(R.id.sbPitch);
		SeekBar sbRoll = (SeekBar) findViewById(R.id.sbRoll);
		sbYAW.setOnSeekBarChangeListener(sbcl1);
		sbPitch.setOnSeekBarChangeListener(sbcl1);
		sbRoll.setOnSeekBarChangeListener(sbcl1);

		TextView tvstate = (TextView) this.findViewById(R.id.stateConnected);
		TextView tvmac = (TextView) this.findViewById(R.id.MacAdress);



		if (MultiWiiBT_menu.prefs.getString("remote_device", "") == "")
			tvstate.setText("Not Connected");
		else {
			tvstate.setText("Connected to: ");
			tvmac.setText(MultiWiiBT_menu.remote_device_mac);
			
			Controller.aYaw = MultiWiiBT_menu.prefs.getInt("yaw_percent", 50);
			Controller.aPit = MultiWiiBT_menu.prefs.getInt("pitch_percent", 50);
			Controller.aRol = MultiWiiBT_menu.prefs.getInt("rol_percent", 50);
			sbYAW.setProgress(Controller.aYaw);
			sbPitch.setProgress(Controller.aPit);
			sbRoll.setProgress(Controller.aRol);
			
		}
	}

	private SeekBar.OnSeekBarChangeListener sbcl1 = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			TextView tvs = null;
			updateTview(tvs, seekBar);
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};

	public void updateTview(TextView tv1, SeekBar skb) {
		switch (skb.getId()) {
		case R.id.sbYAW:
			tv1 = (TextView) this.findViewById(R.id.tvYAW);
			Controller.aYaw = skb.getProgress();
			tv1.setText("YAW " + Controller.aYaw + "% control");
			break;
		case R.id.sbPitch:
			tv1 = (TextView) this.findViewById(R.id.tvPitch);
			Controller.aPit = skb.getProgress();
			tv1.setText("Pitch " + Controller.aPit + " % control");
			break;
		case R.id.sbRoll:
			tv1 = (TextView) this.findViewById(R.id.tvRoll);
			Controller.aRol = skb.getProgress();
			tv1.setText("Roll " + Controller.aRol + " % control");
			break;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			Intent myIntent = new Intent(Config.this, BTDeviceList.class);
			Config.this
					.startActivityForResult(myIntent, REQUEST_CONNECT_DEVICE);
			break;
		case R.id.btnSave:
			MultiWiiBT_menu.prefs
					.edit()
					.putString("remote_device",
							MultiWiiBT_menu.remote_device_mac).commit();
			MultiWiiBT_menu.prefs
					.edit()
					.putInt("yaw_percent",
							Controller.aYaw).commit();
			MultiWiiBT_menu.prefs
					.edit()
					.putInt("pitch_percent",
							Controller.aPit).commit();
			MultiWiiBT_menu.prefs
					.edit()
					.putInt("roll_percent",
							Controller.aRol).commit();

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

				// Save preferences in config file
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
				// Bluetooth is now enabled
			} else {
				// User did not enable Bluetooth or an error occured
				Toast.makeText(this, "Bluetooth was not enabled!",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

}
