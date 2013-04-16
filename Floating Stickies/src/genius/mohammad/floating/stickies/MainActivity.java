package genius.mohammad.floating.stickies;

/**
 * Copyright 2013 Mohammad Adib
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import java.util.Map;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnTouchListener {

	public static float density, width, height;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		boolean key = prefs.getBoolean("key", false);
		if (!key) {
			setContentView(R.layout.tutorial);
			final ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
			final ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
			final ImageView iv3 = (ImageView) findViewById(R.id.imageView3);
			final ImageView iv4 = (ImageView) findViewById(R.id.done);
			final CheckBox cb = (CheckBox) findViewById(R.id.checkBox);

			final Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.show2);
			final Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.show2);
			final Animation anim3 = AnimationUtils.loadAnimation(this, R.anim.show2);
			final Animation anim4 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
			final Animation anim5 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
			final Animation anim6 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

			anim1.setDuration(500);
			anim2.setDuration(500);
			anim3.setDuration(500);

			anim1.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					iv1.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					iv2.startAnimation(anim2);
				}
			});

			anim2.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					iv2.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					iv3.startAnimation(anim3);
				}
			});

			anim3.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					iv3.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					iv4.startAnimation(anim4);
					iv4.setOnTouchListener(MainActivity.this);
				}
			});

			anim4.setDuration(1000);
			anim4.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					iv4.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					iv4.startAnimation(anim5);
				}
			});

			anim5.setDuration(1000);
			anim5.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					iv4.startAnimation(anim4);
				}
			});

			iv1.startAnimation(anim1);
			cb.startAnimation(anim6);
		} else {
			finish();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Intent serviceIntent = new Intent();
		serviceIntent.setAction("wei.mark.standout.StandOutWindow");
		this.stopService(serviceIntent);
		density = getResources().getDisplayMetrics().density;
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();

		new Thread(new Runnable() {

			@Override
			public void run() {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				Map<String, ?> keys = prefs.getAll();

				boolean none = true;
				for (Map.Entry<String, ?> entry : keys.entrySet()) {
					try {
						if (entry.getKey().toString().contains("_id") && !entry.getValue().equals("")) {
							none = false;
							Log.d("startup", entry.getKey().toString());
							StandOutWindow.show(MainActivity.this, MultiWindow.class, Integer.parseInt(entry.getKey().toString().replace("_id", "")));
							Thread.sleep(1500);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (none) {
					StandOutWindow.show(MainActivity.this, MultiWindow.class, StandOutWindow.DEFAULT_ID);
				}
			}

		}).start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {

					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					SharedPreferences.Editor editor = prefs.edit();
					CheckBox cb = (CheckBox) findViewById(R.id.checkBox);
					editor.putBoolean("key", cb.isChecked());
					editor.commit();
					finish();
				}
			});

			findViewById(R.id.ll0).startAnimation(anim);
		}
		return true;
	}
}