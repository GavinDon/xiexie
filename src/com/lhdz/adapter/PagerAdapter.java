package com.lhdz.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {
	/*
	 * 主界面  —— viewpager的adapter;
	 */
	
	private ArrayList<Fragment> fragmentList;
	public PagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
		super(fm);
		fragmentList = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragmentList.get(arg0);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

}
