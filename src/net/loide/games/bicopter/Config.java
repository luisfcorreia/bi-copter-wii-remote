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
	private int aPit;
	private int aRol;
	private int aYaw;
	
	@Override
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

		if (MultiWiiBT.prefs.getString("remote_device", "") == "")
			tvstate.setText(getString(R.string.cfg_ncon));
		else {
			tvstate.setText(getString(R.string.cfg_con));
			tvmac.setText(MultiWiiBT.remote_device_mac);

			MultiWiiBT.remote_device_mac = MultiWiiBT.prefs
					.getString("remote_device", "");

			aYaw = MultiWiiBT.prefs.getInt("yaw_percent", 50);
			aPit = MultiWiiBT.prefs.getInt("pitch_percent", 50);
			aRol = MultiWiiBT.prefs.getInt("roll_percent", 50);
			sbYAW.setProgress(aYaw);
			sbPitch.setProgress(aPit);
			sbRoll.setProgress(aRol);
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
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	};

	public void updateTview(TextView tv1, SeekBar skb) {
		switch (skb.getId()) {
		case R.id.sbYAW:
			tv1 = (TextView) this.findViewById(R.id.tvYAW);
			aYaw = skb.getProgress();
			tv1.setText(getString(R.string.cfg_yaw) + " " + aYaw
					+ "%");
			break;
		case R.id.sbPitch:
			tv1 = (TextView) this.findViewById(R.id.tvPitch);
			aPit = skb.getProgress();
			tv1.setText(getString(R.string.cfg_pitch) + " " + aPit
					+ "%");
			break;
		case R.id.sbRoll:
			tv1 = (TextView) this.findViewById(R.id.tvRoll);
			aRol = skb.getProgress();
			tv1.setText(getString(R.string.cfg_roll) + " " + aRol
					+ " %");
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			Intent myIntent = new Intent(Config.this, BTDeviceList.class);
			Config.this
					.startActivityForResult(myIntent, REQUEST_CONNECT_DEVICE);
			break;
		case R.id.btnSave:
			MultiWiiBT.prefs
					.edit()
					.putString("remote_device",
							MultiWiiBT.remote_device_mac).commit();
			MultiWiiBT.prefs.edit().putInt("yaw_percent", aYaw)
					.commit();
			MultiWiiBT.prefs.edit()
					.putInt("pitch_percent", aPit).commit();
			MultiWiiBT.prefs.edit()
					.putInt("roll_percent", aRol).commit();
			finish();
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				MultiWiiBT.remote_device_mac = data.getExtras().getString(
						BTDeviceList.EXTRA_DEVICE_ADDRESS);
				// update UI
				TextView tvmac = (TextView) this.findViewById(R.id.MacAdress);
				tvmac.setText(MultiWiiBT.remote_device_mac);

				// Save preferences in config file
				MultiWiiBT.prefs
						.edit()
						.putString("remote_device",
								MultiWiiBT.remote_device_mac).commit();

				Toast.makeText(
						this,
						getString(R.string.btdev_devaddr) + " "
								+ MultiWiiBT.remote_device_mac + " "
								+ getString(R.string.btdev_devaddr_sav),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled
			} else {
				// User did not enable Bluetooth or an error occured
				Toast.makeText(this, getString(R.string.btdev_ne),
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

}
