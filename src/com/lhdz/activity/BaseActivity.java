package com.lhdz.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

/**
 * BaseActivity 所有的Activity都需要继承该Activity
 * 
 * @author wangf
 * 
 */
public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityStackControlUtil.add(this);

	}

	
	/**
	 * Activity栈的控制
	 * 主要用于退出应用程序时完全关闭所有的Activity
	 * @author wangf
	 *
	 */
	public static class ActivityStackControlUtil {
		private static List<Activity> activityList = new ArrayList<Activity>();

		public static int getCounter() {
			return activityList.size();
		}

		public static void remove(Activity activity) {
			activityList.remove(activity);
		}

		public static void add(Activity activity) {
			activityList.add(activity);
		}

		public static void finishProgram() {
			for (Activity activity : activityList) {
				if (activity != null) {
					activity.finish();
				}
			}
			System.gc();
			// android.os.Process.killProcess(android.os.Process.myPid());
		}

		public static void finishAllActivityWithout(Activity withoutActivity) {

			for (int index = activityList.size() - 1; index >= 0; index--) {
				if (withoutActivity != activityList.get(index)) {
					activityList.get(index).finish();
					activityList.remove(index);
				}
			}
		}
	}

}
