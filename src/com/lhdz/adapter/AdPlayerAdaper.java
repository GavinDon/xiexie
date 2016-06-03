package com.lhdz.adapter;

import java.util.List;

import com.lhdz.activity.HelpWebActivity;
import com.lhdz.activity.R;
import com.lhdz.activity.StarbabyActivity;
import com.lhdz.util.Define;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class AdPlayerAdaper extends PagerAdapter {
	private List<Integer> imgs;
	private Context context;

	public AdPlayerAdaper(Context context, List<Integer> imgs) {
		this.context = context;
		this.imgs = imgs;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = LayoutInflater.from(context).inflate(R.layout.item_pic_ad,
				null);
		ImageView imageView = (ImageView) view.findViewById(R.id.pic);
		imageView.setBackgroundDrawable(context.getResources().getDrawable(
				imgs.get(position % (imgs.size()))));
		container.addView(view);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				switch ((position % (imgs.size()))) {
				case 0:
					//广告跨年
					intent.setClass(context, HelpWebActivity.class);
					intent.putExtra("title", "歇歇");
					intent.putExtra("url", Define.URL_AD_FIRST);
					context.startActivity(intent);
					break;
				case 1:
					intent.setClass(context, HelpWebActivity.class);
					intent.putExtra("title", "活动详情");
					intent.putExtra("url", Define.URL_ACTIVITY);
					context.startActivity(intent);
					break;
				case 2:
					intent.setClass(context, HelpWebActivity.class);
					intent.putExtra("title", "歇歇");
					intent.putExtra("url", Define.URL_AD_THREE);
					context.startActivity(intent);
					break;
				case 3:
//					intent.setClass(context, StarbabyActivity.class);
//					context.startActivity(intent);
					break;

				}

			}

		});
		return view;
	}
}
