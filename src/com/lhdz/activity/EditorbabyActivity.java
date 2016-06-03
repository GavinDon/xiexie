package com.lhdz.activity;
/**
 * 编辑宝贝信息
 * @author 王哲
 * @date 2015-8-26
 */


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class EditorbabyActivity extends BaseActivity implements OnClickListener{

	private TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editorbaby);
		//数据初始化
		initViews();
		backArrow();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("编辑");
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
					EditorbabyActivity.this.finish();

					break;
				}
			}
		});
	}
}
