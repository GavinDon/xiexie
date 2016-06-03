package com.lhdz.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {
	private ArrayList<View> viewLists;

	public ViewPagerAdapter(ArrayList<View> viewLists) {
		super();
		this.viewLists = viewLists;
	}

	@Override
	public int getCount() {
		// 获取size
		return viewLists.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}
	@Override
	public void destroyItem(View view, int position, Object object) {
		// 销毁item
		 ((ViewPager) view).removeView(viewLists.get(position));
    }
	@Override
	public Object instantiateItem(View view, int position) {
		// 实例化item
		((ViewPager) view).addView(viewLists.get(position), 0);
        return viewLists.get(position);
	}

}
