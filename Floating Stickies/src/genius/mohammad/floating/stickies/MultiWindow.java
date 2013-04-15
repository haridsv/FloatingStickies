package genius.mohammad.floating.stickies;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

	// every window is initially same size
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

	@Override
	public boolean onFocusChange(int id, Window window, boolean focus) {
		if (focus) {
			window.findViewById(R.id.body).getBackground().setAlpha(255);
			window.findViewById(R.id.window).getBackground().setAlpha(127);
			window.findViewById(R.id.titlebar).getBackground().setAlpha(255);
		} else {
			window.findViewById(R.id.body).getBackground().setAlpha(127);
			window.findViewById(R.id.window).getBackground().setAlpha(0);
			window.findViewById(R.id.titlebar).getBackground().setAlpha(127);
		}
		Log.d("focus", "" + focus);
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

	@Override
	public boolean onKeyEvent(int id, Window window, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				close(id);
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
