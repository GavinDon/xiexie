package com.lhdz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class OclockActivity extends BaseActivity implements OnClickListener {

	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oclock);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// 使输入框位于键盘之上;
		initViews();
		listenCenter();
		backArrow();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("钟点服务");

	}

	private void listenCenter() {
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.appointment_confirm:
			intent = new Intent(this, ConfirmIndentActivity.class);
			startActivity(intent);
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
					OclockActivity.this.finish();

					break;
				}
			}
		});
	}
}
