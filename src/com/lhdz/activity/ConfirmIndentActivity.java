package com.lhdz.activity;
/**
 * 确认订单第一个界面
 * @author 王哲
 * @date 2015-8-26
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConfirmIndentActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	private Button pay;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm_indent);
		initViews();
		listenCenter();
		backArrow();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("确认订单");
		pay = (Button) findViewById(R.id.bt_pay);
	}

	private void listenCenter() {
		pay.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.bt_pay:
			intent=new Intent(this, OrderActivity.class);
			startActivity(intent);
			break;
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
					ConfirmIndentActivity.this.finish();

					break;
				}
			}
		});
	}
}
