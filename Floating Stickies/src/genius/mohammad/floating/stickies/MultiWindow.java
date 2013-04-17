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

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.FrameLayout;

public class MultiWindow extends StandOutWindow {

	@Override
	public String getAppName() {
		return "Floating Stickies";
	}

	@Override
	public int getAppIcon() {
		return R.drawable.icon;
	}

	@Override
	public String getTitle(int id) {
		return "Window" + id;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		EditText et = (EditText) frame.findViewById(R.id.editText);
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				save();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	// every window is initially the same size
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String s = prefs.getString("_id" + id, "");
		try {
			if (!s.equals("")) {
				int x = Integer.parseInt(s.split(",")[0]);
				int y = Integer.parseInt(s.split(",")[1]);
				int w = Math.max((int) pxFromDp(120), Integer.parseInt(s.split(",")[2]));
				int h = Math.max((int) pxFromDp(120), Integer.parseInt(s.split(",")[3]));
				return new StandOutLayoutParams(id, w, h, x, y, (int) pxFromDp(120), (int) pxFromDp(120));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new StandOutLayoutParams(id, (int) pxFromDp(150), (int) pxFromDp(150), StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER, (int) pxFromDp(120), (int) pxFromDp(120));
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	@Override
	public Notification getPersistentNotification(int id) {
		int icon = getAppIcon();
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getPersistentNotificationTitle(id);
		String contentText = getPersistentNotificationMessage(id);

		Intent notificationIntent = getPersistentNotificationIntent(id);

		PendingIntent contentIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// 4.1+ Low priority notification
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 16) {
			Notification.Builder mBuilder = new Notification.Builder(this).setSmallIcon(getAppIcon()).setContentTitle(contentTitle).setContentText(contentText).setPriority(Notification.PRIORITY_MIN).setContentIntent(contentIntent);
			return mBuilder.build();
		}

		String tickerText = String.format("%s: %s", contentTitle, contentText);

		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(c, contentTitle, contentText, contentIntent);

		return notification;
	}

	/**
	 * Changes the stickies to transparent when unfocused (commented out)
	 */
	@Override
	public boolean onFocusChange(int id, Window window, boolean focus) {
		if (focus) {
			window.findViewById(R.id.body).getBackground().setAlpha(255);
			window.findViewById(R.id.window).getBackground().setAlpha(100);
			window.findViewById(R.id.titlebar).getBackground().setAlpha(255);
		} else {
			// window.findViewById(R.id.body).getBackground().setAlpha(160);
			// window.findViewById(R.id.window).getBackground().setAlpha(80);
			// window.findViewById(R.id.titlebar).getBackground().setAlpha(160);
		}
		return false;
	}

	private float pxFromDp(float dp) {
		return dp * MainActivity.density;
	}

	@Override
	public int getFlags(int id) {
		return StandOutFlags.FLAG_DECORATION_SYSTEM | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP | StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH | StandOutFlags.FLAG_WINDOW_PINCH_RESIZE_ENABLE;
	}

	@Override
	public String getPersistentNotificationTitle(int id) {
		return getAppName();
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "Click to save & close all stickies";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return StandOutWindow.getCloseAllIntent(this, getClass());
	}

	/**
	 * Close sticky on back button (commented out)
	 */
	@Override
	public boolean onKeyEvent(int id, Window window, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				// close(id);
			}
		}
		return false;
	}

	@Override
	public Runnable getAddRunnable(int id) {
		return new Runnable() {
			@Override
			public void run() {
				StandOutWindow.show(MultiWindow.this.getApplicationContext(), MultiWindow.class, getUniqueId());
			}
		};
	}
}
