package com.lhdz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CouponActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupon);
		backArrow();
	}

	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.tv_retreat);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_retreat:// 后退键
					CouponActivity.this.finish();

					break;
				}
			}
		});
	}
}
