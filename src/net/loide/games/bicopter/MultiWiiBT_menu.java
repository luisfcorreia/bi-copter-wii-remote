package net.loide.games.bicopter;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class MultiWiiBT_menu extends Activity implements OnClickListener {

	public static String remote_device_mac = "";
	public static String MY_PREFS_FILE_NAME = "net.loide.games.bicopter.multiwiibt.conf";
	public static SharedPreferences prefs;
	public static String UI_VERSION = "0.3";
	public static String adID = "a14eced1c1c11aa";
	PopupWindow pw;
	View lay_about;
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Ads stuff
		LinearLayout layout = (LinearLayout) findViewById(R.id.adThing);
		AdView adView = new AdView(this, AdSize.BANNER, adID);
		layout.addView(adView);
		AdRequest request = new AdRequest();
		adView.loadAd(request);

		prefs = new ObscuredSharedPreferences(this, this.getSharedPreferences(
				MY_PREFS_FILE_NAME, Context.MODE_PRIVATE));
		remote_device_mac = prefs.getString("remote_device", "");

		if (remote_device_mac != "") {
			Toast.makeText(
					this,
					getString(R.string.btdev) + " " + remote_device_mac + " "
							+ getString(R.string.btdev_cfg), Toast.LENGTH_SHORT)
					.show();
		}

		Button button1 = (Button) findViewById(R.id.startgameBtn);
		Button button2 = (Button) findViewById(R.id.configBtn);
		Button button3 = (Button) findViewById(R.id.exitBtn);

		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);

		// Get local Bluetooth adapter
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, getString(R.string.btdev_na),
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		// If BT is not on, request that it be enabled.
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
			if (remote_device_mac == "") {
				Toast.makeText(this, getString(R.string.btdev_sel),
						Toast.LENGTH_LONG).show();
			} else {

				myIntent = new Intent(MultiWiiBT_menu.this, Controller.class);
				MultiWiiBT_menu.this.startActivity(myIntent);
			}
			break;

		case R.id.configBtn:
			myIntent = new Intent(MultiWiiBT_menu.this, Config.class);
			MultiWiiBT_menu.this.startActivity(myIntent);
			break;

		case R.id.exitBtn:
			// handle button B click;
			MultiWiiBT_menu.this.finish();
			break;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				remote_device_mac = data.getExtras().getString(
						BTDeviceList.EXTRA_DEVICE_ADDRESS);

				// Save preference in config file
				prefs.edit().putString("remote_device", remote_device_mac)
						.commit();

				Toast.makeText(
						this,
						getString(R.string.btdev_devaddr) + " "
								+ remote_device_mac + " "
								+ getString(R.string.btdev_devaddr_sav),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
			} else {
				// User did not enable Bluetooth or an error occured
				Toast.makeText(this, getString(R.string.btdev_ne),
						Toast.LENGTH_SHORT).show();
				finish();
			}
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
		case R.id.about:
			AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
			helpBuilder.setTitle(getString(R.string.about_title));
			helpBuilder.setMessage(getString(R.string.about_lc) + "\n"
					+ getString(R.string.about_rp) + "\n"
					+ getString(R.string.about_web));
			helpBuilder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			// Remember, create doesn't show the dialog
			AlertDialog helpDialog = helpBuilder.create();
			helpDialog.show();
			return true;
		default:
			return true;
		}
	}
}