package com.lhdz.activity;

/**
 * 宝贝介绍
 * @author 王哲
 * @date 2015-8-26
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IncludeBabyActivity extends BaseActivity implements OnClickListener {

	private TextView title;
	private RelativeLayout lay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduced_baby);
		initViews();
		backArrow();
	}

	private void initViews() {
		lay=(RelativeLayout) findViewById(R.id.layss1);
		lay.setBackgroundDrawable(getResources().getDrawable(R.color.ucolor));
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("宝贝介绍");
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		default:
			break;
		}

	}
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					IncludeBabyActivity.this.finish();

					break;
				}
			}
		});
	}
}
