package com.lhdz.activity;
/**
 * 明星宝贝
 * @author 王哲
 * @date 2015-8-27
 */

import java.util.ArrayList;

import com.lhdz.adapter.NewsAdapter;
import com.lhdz.adapter.StarbabyAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StarbabyActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	private ListView mLv;
	private TextView fenxiang;
	private RelativeLayout lay;
	private RadioGroup mRg;
	private RadioButton month;
	private RadioButton quarter;
	private RadioButton year;
	//宝贝排行列表适配器
	private StarbabyAdapter adapter;
	private ArrayList<String> mList;
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_starbaby);
		//数据初始化
				initData();
				backArrow();
				lay=(RelativeLayout) findViewById(R.id.layss1);
				lay.setBackgroundDrawable(getResources().getDrawable(R.color.gree_title));
				title = (TextView) findViewById(R.id.tv_title);
				title.setText("明星宝贝");
				fenxiang=(TextView) findViewById(R.id.bt_del);
				fenxiang.setText("分享");
				mLv=(ListView)findViewById(R.id.lv_starbaby);
				adapter=new StarbabyAdapter(this, mList);
				mLv.setAdapter(adapter);
				mRg=(RadioGroup) findViewById(R.id.rg_starbaby);
				mRg.setOnCheckedChangeListener(this);
				mRg.check(R.id.rb_starbaby_month);
				month=(RadioButton) findViewById(R.id.rb_starbaby_month);
				month.getPaint().setFakeBoldText(true);
				quarter=(RadioButton) findViewById(R.id.rb_starbaby_quarter);
				year=(RadioButton) findViewById(R.id.rb_starbaby_year);
				findViewById(R.id.bt_mybaby).setOnClickListener(this);
				findViewById(R.id.bt_join).setOnClickListener(this);
				
			}
			private void initData() {
				mList=new ArrayList<String>();
				mList.add("大宝");
				mList.add("小宝");
				mList.add("小明");
				mList.add("呵呵");
				
			}
			@Override
			public void onClick(View v) {
				Intent intent;
				switch (v.getId()) {
				case R.id.bt_mybaby:
					intent = new Intent(this,MybabyActivity.class);
					startActivity(intent);
					break;
				case R.id.bt_join:
					intent = new Intent(this,IwanttoplayActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
				
			}
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (month.getId()==checkedId) {
					month.getPaint().setFakeBoldText(true);	
					adapter.notifyDataSetChanged();
				}else {
					month.getPaint().setFakeBoldText(false);
				}
				if (quarter.getId()==checkedId) {
					quarter.getPaint().setFakeBoldText(true);
					mList.add("石头");
					adapter.notifyDataSetChanged();
				}else {
					quarter.getPaint().setFakeBoldText(false);
					mList.remove("石头");
					adapter.notifyDataSetChanged();

				}
				if (year.getId()==checkedId) {
					year.getPaint().setFakeBoldText(true);
					mList.add("阿德");
					adapter.notifyDataSetChanged();
				}else {
					year.getPaint().setFakeBoldText(false);
					mList.remove("阿德");
					adapter.notifyDataSetChanged();

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
							StarbabyActivity.this.finish();

							break;
						}
					}
				});
			}
}
