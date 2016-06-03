package com.lhdz.activity;
/**
 * 预览
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

public class MybaobaoActivity extends BaseActivity implements OnClickListener{

	private RelativeLayout lay;
	private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybaobao);
		initViews();
		backArrow();
	}

	private void initViews() {
		lay=(RelativeLayout) findViewById(R.id.relativelayout);
		lay.setBackgroundDrawable(getResources().getDrawable(R.color.ucolor));
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("预览");
	}

	@Override
	public void onClick(View v) {
		Intent intent;
	}
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					MybaobaoActivity.this.finish();

					break;
				}
			}
		});
	}
}
