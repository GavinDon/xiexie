package com.lhdz.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MsgNofifyActivity extends BaseActivity implements
OnClickListener{

	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_nofify);
		initViews();
		listenCenter();
		backArrow();
	}
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("消息通知");

	}
	private void listenCenter() {
	}
	@Override
	public void onClick(View v) {
	
	}
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					MsgNofifyActivity.this.finish();

					break;
				}
			}
		});
	}
	
}
