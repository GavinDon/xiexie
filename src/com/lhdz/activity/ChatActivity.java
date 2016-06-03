package com.lhdz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ChatActivity extends BaseActivity implements OnClickListener {
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		initViews();
		listenCenter();
		backArrow();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("聊天");

	}

	private void listenCenter() {
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

		case R.id.iv_retreat:
			this.finish();
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
					ChatActivity.this.finish();

					break;
				}
			}
		});
	}

}
