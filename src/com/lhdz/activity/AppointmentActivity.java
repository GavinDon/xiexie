//package com.lhdz.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Selection;
//import android.text.Spannable;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.View.OnClickListener;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
///**
// * 预约下单页面
// * @author wangf
// *
// */
//public class AppointmentActivity extends BaseActivity implements OnClickListener {
//	private TextView title;
//	private EditText phoneNubmer;
//	private TextView confirm;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_appointment);
//		getWindow().setSoftInputMode(
//				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
//						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);//使输入框位于键盘之上;
//		initViews();
//		listenCenter();
//		backArrow();
//	}
//
//	
//	/**
//	 * 初始化页面控件
//	 */
//	private void initViews() {
//		title = (TextView) findViewById(R.id.tv_title);
//		phoneNubmer = (EditText) findViewById(R.id.et_phonenumber);
//		title.setText("预约下单");
//		confirm = (TextView) findViewById(R.id.appointment_confirm);
//
//	}
//
//	
//	/**
//	 * 页面控件的监听
//	 */
//	private void listenCenter() {
//		confirm.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//		Intent intent = null;
//		switch (v.getId()) {
//		case R.id.appointment_confirm:
//			intent = new Intent(this, ConfirmIndentActivity.class);
//			startActivity(intent);
//			break;
//		}
//	}
//	
//	
//	
//	public void backArrow() {
//		TextView back;
//		back = (TextView) findViewById(R.id.public_back);
//		back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				switch (v.getId()) {
//				case R.id.public_back:// 后退键
//					AppointmentActivity.this.finish();
//
//					break;
//				}
//			}
//		});
//	}
//}
