package com.lhdz.activity;

import com.lhdz.util.Define;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 服务协议
 * 
 * @author wangf 
 * 
 */
public class ServiceProtoActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	private RelativeLayout userComplaint;
	private RelativeLayout rl_UserProtocol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_proto);
		initViews();
		listenCenter();
		backArrow();
	}

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("服务协议");
		userComplaint = (RelativeLayout) findViewById(R.id.user_complaint);
		rl_UserProtocol=(RelativeLayout) findViewById(R.id.rl_user_protocol);

	}

	/**
	 * 页面控件的监听
	 */
	private void listenCenter() {
		userComplaint.setOnClickListener(this);
		rl_UserProtocol.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.user_complaint:
			intent.setClass(ServiceProtoActivity.this,HelpWebActivity.class);
			intent.putExtra("title", "服务投诉");
			intent.putExtra("url", Define.URL_SERVICE_COMPLAINT);
			startActivity(intent);

			break;
		case R.id.rl_user_protocol:
			intent.setClass(ServiceProtoActivity.this,HelpWebActivity.class);
			intent.putExtra("title", "使用协议");
			intent.putExtra("url", Define.URL_USER_AGREEMENT);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	
	/**
	 * 返回按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					ServiceProtoActivity.this.finish();

					break;

				}
			}
		});
	}

}
