package com.lhdz.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.adapter.SettingAdapter;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MyApplication;


/**
 * 设置页面
 * @author wangf
 *
 */
public class SettingActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	
	public static SettingActivity instanceSettingActivity = null;
	
	private TextView title;
	private TextView exitLogin;
	private TextView versionCode;
//	private ListView mListview;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private int icon[];
	
	private RelativeLayout rl_accoutSafe;
	private RelativeLayout rl_about;
	
	private MyApplication myApplication;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		myApplication = (MyApplication) getApplication();
		
		instanceSettingActivity = this;
		
		initView();
		listenCenter();
//		mListview.setAdapter(new SettingAdapter(this, data));
		backArrow();
		


	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (MyApplication.loginState) {
			exitLogin.setVisibility(View.VISIBLE);
		}else{
			exitLogin.setVisibility(View.GONE);
		}
		
	}
	
	

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("设置");
//		mListview = (ListView) findViewById(R.id.lv_mine_setting);
		versionCode = (TextView) findViewById(R.id.tv_version_code);
//		flateData();
		
		rl_accoutSafe = (RelativeLayout) findViewById(R.id.rl_accoutsafe);//账号安全
		rl_about = (RelativeLayout) findViewById(R.id.rl_about);//关于
		
		exitLogin = (TextView) findViewById(R.id.tv_exitlogin);

	}
	
	
//	Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//
//			switch (msg.what) {
//			case MSG_EDIT_FAIL:
//				// 设置按钮为可点击
//				exitLogin.setClickable(true);
//				exitLogin
//						.setBackgroundResource(R.drawable.sel_exitlogin_button);
//				Toast.makeText(SettingActivity.this, "退出失败", 0).show();
//				break;
//			case BTN_TIMER_OVER:
//				// 设置按钮为可点击
//				exitLogin.setClickable(true);
//				exitLogin.setBackgroundResource(R.drawable.sel_exitlogin_button);
//				handler.removeCallbacks(btnTimerRunnable);
//				
//				break;
//			default:
//				break;
//			}
//
//			super.handleMessage(msg);
//		}
//	};
	

	private void flateData() {
		String[] ModelData = new String[] { "隐私", "通用", "通知" };

		icon = new int[] { R.drawable.icon_28_privacy,
				R.drawable.icon_28_tongy, R.drawable.icon_28_notice };
		for (int i = 0; i < ModelData.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", ModelData[i]);
			map.put("icons", icon[i]);
			data.add(map);
		}

//		try {
//
//			String versionName = this.getPackageManager().getPackageInfo(
//					"com.lhdz.activity", 0).versionName;
//			versionCode.setText(versionName);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/**
	 *  对控件的监听事件
	 */
	private void listenCenter() {
//		mListview.setOnItemClickListener(this);
		rl_accoutSafe.setOnClickListener(this);
		rl_about.setOnClickListener(this);
		exitLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {

		case R.id.rl_accoutsafe:
			intent.setClass(this, AccoutSafeActivity.class);
			startActivity(intent); 
			break;
		case R.id.tv_exitlogin:

			//显示退出对话框，在对话框中进行退出操作
			showExitDialog();
			
			break;
		case R.id.rl_about:
			intent.setClass(this, AboutActivity.class);
			startActivity(intent);
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		switch (position) {
		// case 0:
		// intent.setClass(this, OrderActivity.class);
		// startActivity(intent);
		// break;

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
					SettingActivity.this.finish();
					break;
				}
			}
		});
	}
	
	
	
	/**
	 * 确定退出对话框
	 */
	private void showExitDialog(){
		SweetAlertDialog dialog = new SweetAlertDialog(this,
				SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("确定要退出吗？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				// 点击退出登录按钮清除帐号信息
				MyApplication.loginState = false;
				MyApplication.userId = 0;
				String sql = DbOprationBuilder.deleteBuilder("authInfo");
				DataBaseService ds = new DataBaseService(SettingActivity.this);
				ds.delete(sql);
				
				if(MyApplication.houseSocketConn != null){
					MyApplication.houseSocketConn.closeHouseSocket();
				}
				
				sweetAlertDialog.dismissWithAnimation();
				
				finish();
			}
		});
		dialog.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		
		dialog.show();
	}
	
	


}
