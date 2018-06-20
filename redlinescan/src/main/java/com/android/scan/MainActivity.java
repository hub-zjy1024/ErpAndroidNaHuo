package com.android.scan;


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.android.dev.BarcodeAPI;

public class MainActivity extends Activity {

	private static final String TAG = "BarcodeScan";
 
	private TextView mLog;
	private Button btn_clear;
	private Switch switchPower;
	private Switch switchSanMode;
	private Vibrator mvibrator;
	private boolean g_mvibrator = true;
	private boolean g_sound = true;
	private int test_ts = 0;
	private MediaPlayer mmediaplayer;
 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mLog = (TextView) findViewById(R.id.log);

		btn_clear = (Button) findViewById(R.id.btn_clear);

		switchPower = (Switch) findViewById(R.id.PowerSwh);
		switchSanMode = (Switch) findViewById(R.id.ScanSwh);

		mvibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	 

		// first, apply to the scan class and you should have SERIAL_PORT
		// permission.
		 

		BarcodeAPI.getInstance().open();
		BarcodeAPI.getInstance().m_handler=mHandler;

		String machineCode = BarcodeAPI.getInstance().getMachineCode();
		Log.e("zjy", "MainActivity->onDestroy(): code==" + machineCode);
		mediaPlayerInit();

		btn_clear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mLog.setText("");
				setVibratortime(200);
			}
		});
		switchPower.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			int oldStat=0;
			
	 
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					BarcodeAPI.getInstance().open();
				} else {
					BarcodeAPI.getInstance().close();
				}

			}
		});
		if(BarcodeAPI.getInstance().isContinueModel()==1){
			switchSanMode.setChecked(true);
		}
		
		switchSanMode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				BarcodeAPI.getInstance().setScanMode(isChecked);
			 

			}
		});
		
		
	}

	private void mediaPlayerInit() {
		mmediaplayer = new MediaPlayer();
		mmediaplayer = MediaPlayer.create(this, R.raw.scanok);
		mmediaplayer.setLooping(false);
	}

	private void mediaPlayer() {
		if (g_sound) {
			mmediaplayer.start();
		}
	}

	private void mediaPlayerfinish() {
		mmediaplayer.stop();
		mmediaplayer.release();
	}

	private void setVibratortime(int times) {
		if (mvibrator.hasVibrator() && g_mvibrator)
			mvibrator.vibrate(times);
	}

	@Override
	public void onResume() {

		super.onResume();
 

	}

	public void onStop() {
		super.onStop();
		// close scanner 

	}

	@Override
	public void onDestroy() {
 
		mediaPlayerfinish();

		BarcodeAPI.getInstance().m_handler=null;
		BarcodeAPI.getInstance().close();

		super.onDestroy();
	}

 

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
			case KeyEvent.KEYCODE_INFO:
		case KeyEvent.KEYCODE_MUTE:
			// scanning
			if (event.getRepeatCount() == 0) {
				BarcodeAPI.getInstance().scan();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BarcodeAPI.BARCODE_READ:
				test_ts++;
				if (test_ts == 15) {
					mLog.setText("");
					test_ts = 0;
				}
				mLog.setText(mLog.getText() + (String) msg.obj + "\n");
				setVibratortime(50);
				mediaPlayer();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
//
//		case R.id.action_sound:
//			if (g_sound) {
//				g_sound = false;
//				item.setChecked(false);
//			} else {
//				g_sound = true;
//				item.setChecked(true);
//			}
//			break;
//		case R.id.action_Vibrator:
//			if (g_mvibrator) {
//				g_mvibrator = false;
//				item.setChecked(false);
//			} else {
//				g_mvibrator = true;
//				item.setChecked(true);
//			}
//
//			break;
		}
		return true;
	}

}
