package com.lhdz.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 用户首次启动应用时的欢迎界面
 * @author wangf
 *
 */
public class WelcomeActivity extends BaseActivity {
	private List<View> views;
	private ImageView iv1, iv2, iv3;
	private ImageView[] imageView;
	private ViewPager viewPager;
	private boolean isEntry = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		init();
		SharedPreferences spp = getSharedPreferences("config", MODE_PRIVATE);
		isEntry = spp.getBoolean("isEntry", false);
		Log.i("isEntry", String.valueOf(isEntry));
		if (isEntry) {
			Intent intent = new Intent(this, MainFragment.class);
			startActivity(intent);
			this.finish();

		} else {
			viewPager.setAdapter(new Myadapter());// 给viewPager设置适配器
			viewPager.setOnPageChangeListener(new myOnPageChangeListene());

		}

	}

	/**
	 * 使用PagerAdapter必须重写以下4个方法
	 */
	class Myadapter extends PagerAdapter {

		@Override
		public int getCount() {

			return views.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(views.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(views.get(position));

			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}

	}

	class myOnPageChangeListene implements OnPageChangeListener {

		@Override
		public void onPageSelected(int position) {
			imageView[position].setImageDrawable(getResources().getDrawable(
					R.drawable.solid_bigcircle));
			if (position != 0) {
				imageView[position - 1].setImageDrawable(getResources()
						.getDrawable(R.drawable.bigcircle));
			}
			if (position != 2) {
				imageView[position + 1].setImageDrawable(getResources()
						.getDrawable(R.drawable.bigcircle));
			}

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 *  点击体验时进入首页；
	 * @param v
	 */
	public void startExperience(View v) {
		SharedPreferences sp = getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("isEntry", true);
		editor.commit();
		Intent intent = new Intent(this, MainFragment.class);
		startActivity(intent);
		this.finish();
	}

	public void init() {
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv3 = (ImageView) findViewById(R.id.iv3);
		viewPager = (ViewPager) findViewById(R.id.vp);
		imageView = new ImageView[] { iv1, iv2, iv3 };
		views = new ArrayList<View>();
		LayoutInflater flater = LayoutInflater.from(this);
		for (int i = 1; i <= imageView.length; i++) {
			String name = "welcome" + i;
			View view = flater.inflate(
					getResources().getIdentifier(name, "layout",
							getPackageName()), null);

			views.add(view);

		}

	}
}
