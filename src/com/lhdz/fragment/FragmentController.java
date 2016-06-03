package com.lhdz.fragment;

import java.util.ArrayList;

import com.lhdz.activity.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class FragmentController {

	private int containerId;
	private FragmentManager fm;
	private ArrayList<Fragment> fragments;

	private static FragmentController controller;

	public static FragmentController getInstance(FragmentActivity activity,
			int containerId) {
		if (controller == null) {
			controller = new FragmentController(activity, containerId);
		}
		return controller;
	}

	public static void onDestroy() {
		controller = null;
	}

	private FragmentController(FragmentActivity activity, int containerId) {
		this.containerId = containerId;
		fm = activity.getSupportFragmentManager();
		 initFragment();
	}

	private void initFragment() {
		fragments = new ArrayList<Fragment>();
		fragments.add(new HomePageFragment());
		fragments.add(new IndentFragment());
		fragments.add(new NewsFragmnet());
		fragments.add(new MineFragment());

//		FragmentTransaction ft = fm.beginTransaction();
//		for (Fragment fragment : fragments) {
//			ft.add(containerId, fragment);
//		}
//		ft.commitAllowingStateLoss();
	}

	public void showFragment(int position) {
//		 hideFragments();
		replaceFragment(position);
		Fragment fragment = fragments.get(position);
		FragmentTransaction ft = fm.beginTransaction();
		ft.show(fragment);
		ft.commitAllowingStateLoss();
	}

	public void hideFragments() {
		FragmentTransaction ft = fm.beginTransaction();
		for (Fragment fragment : fragments) {
			if (fragment != null) {
				ft.hide(fragment);
			}
		}
		ft.commitAllowingStateLoss();
	}

	public Fragment getFragment(int position) {
		return fragments.get(position);
	}

	public void replaceFragment(int position) {
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(containerId, fragments.get(position));
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}

}