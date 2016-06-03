package com.lhdz.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ShotcutAdapter extends FragmentStatePagerAdapter {
	ArrayList<Fragment> list;
	FragmentManager fm;

	public ShotcutAdapter(FragmentManager fm) {
		super(fm);
	}

	public ShotcutAdapter(FragmentManager fm, ArrayList<Fragment> list) {
		super(fm);
		this.fm = fm;
		this.list = list;
	}

	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}

//	@Override
//	public Object instantiateItem(ViewGroup arg0, int arg1) {
//		return super.instantiateItem(arg0, arg1);
//	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);

	}

}
