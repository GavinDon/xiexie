package com.lhdz.util;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class GetScreenInchUtil {
	public static int getDpi(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		int height = 0;
		@SuppressWarnings("rawtypes")
		Class c;
		try {
			c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
			height = dm.heightPixels;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return height;
	}

	public static int[] getScreenWH(Context poCotext) {
		WindowManager wm = (WindowManager) poCotext
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		return new int[] { width, height };
	}

	public static int getVrtualBtnHeight(Context poCotext) {
		int location[] = getScreenWH(poCotext);
		int realHeiht = getDpi((Activity) poCotext);
		int virvalHeight = realHeiht - location[1];
		return virvalHeight;
	}
}
