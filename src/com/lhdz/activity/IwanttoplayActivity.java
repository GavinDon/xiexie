package com.lhdz.activity;
/**
 * 我要参赛
 * @author 王哲
 * @date 2015-8-26
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class IwanttoplayActivity extends BaseActivity implements OnClickListener{

	private TextView title;
	private RadioGroup mRb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_iwantjoin);
		//数据初始化
		initViews();
		backArrow();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("我要参赛");
		findViewById(R.id.bt_yulan).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.bt_yulan:
			intent = new Intent(this,MybaobaoActivity.class);
			startActivity(intent);
			break;
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
					IwanttoplayActivity.this.finish();

					break;
				}
			}
		});
	}
}
