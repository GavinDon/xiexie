package com.lhdz.activity;

import com.lhdz.util.Config;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 关于页面
 * @author wangf
 *
 */
public class AboutActivity extends BaseActivity {

	private TextView title;
	private TextView tv_about_version;
	private String versionName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		backArrow();
		initView();
		setViewData();
		
	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("关于");
		
		tv_about_version = (TextView) findViewById(R.id.tv_about_version);
	}

	
	
	/**
	 * 为页面控件设置数据
	 */
	private void setViewData(){
		
		try {
			
			versionName = this.getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			if(Config.isDebug){
				tv_about_version.setText("版本号："+"Beat "+versionName);
			}else{
				tv_about_version.setText("版本号："+versionName);
			}
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
					finish();
					break;
				}
			}
		});
	}

}
