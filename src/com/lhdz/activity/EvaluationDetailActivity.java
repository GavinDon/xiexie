package com.lhdz.activity;

import com.lhdz.wediget.AppointPopmenu;
import com.lhdz.wediget.SharePop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class EvaluationDetailActivity extends BaseActivity implements OnClickListener {
	private TextView back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluation_detail);
		initviews();
		listenerCenter();// 监听中心

	}

	private void initviews() {
		back = (TextView) findViewById(R.id.tv_reback);
		findViewById(R.id.bt_toevaluation).setOnClickListener(this);
		findViewById(R.id.bt_moreevaluation).setOnClickListener(this);
	}

	private void listenerCenter() {
		back.setOnClickListener(this);
//		toEva.setOnClickListener(this);
//		moreEva.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_reback:// 后退返回
			this.finish();
			break;
		case R.id.bt_toevaluation:// 进入评价界面
			intent.setClass(this, EvaluationActivity.class);
			startActivity(intent);
			break;
		case R.id.bt_moreevaluation: // 加载更多评论
//			intent.setClass(this, AllCommentActivity.class);
//			startActivity(intent);
			break;
		}
	}
}
