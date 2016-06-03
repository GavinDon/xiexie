package com.lhdz.wediget;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.lhdz.activity.R;
import com.lhdz.activity.R.color;
import com.lhdz.publicMsg.MyApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TimeButton extends Button implements OnClickListener {
	private long lenght = 60 * 1000;// 倒计时长度,这里给了默认60秒
	private String textAfter = "秒后重新获取";
	private String textBefore = "点击获取验证码";
	private final String TIME = "time";
	private final String CTIME = "ctime";
	private OnClickListener mOnclickListener;
	private Timer timer;
	private TimerTask timerTask;
	private long time;
	private String mobile="123456" ;
	Map<String, Long> map = new HashMap<String, Long>();

	public TimeButton(Context context) {
		super(context);
		setOnClickListener(this);

	}

	public TimeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	@SuppressLint("HandlerLeak")
	// 返回获取验证码之前的按扭设置
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			TimeButton.this.setText(time / 1000 + textAfter);
			time -= 1000;
			if (time < 0) {
				TimeButton.this.setEnabled(true);
				TimeButton.this.setText(textBefore);
				TimeButton.this.setBackgroundResource((R.drawable.shape_getcaptcha));
				clearTimer();
			}
		};
	};

	private void initTimer() {
		time = lenght;
		timer = new Timer();
		timerTask = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0x01);
			}
		};
	}

	private void clearTimer() {
		if (timerTask != null) {
			timerTask.cancel();
			timerTask = null;
		}
		if (timer != null)
			timer.cancel();
		timer = null;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		if (l instanceof TimeButton) {
			super.setOnClickListener(l);
		} else
			this.mOnclickListener = l;
	}

	// 获取验证码之后的一些设置
	@Override
	public void onClick(View v) {
		if (mOnclickListener != null) {
			mOnclickListener.onClick(v);//点击按扭之后首先执行 
			if(mobile.length()<11){
				return;
			}
			initTimer();
			this.setText(time / 1000 + textAfter);
			this.setEnabled(false);
			this.setBackgroundResource(R.drawable.shape_after_getcaptcha);
			timer.schedule(timerTask, 0, 1000);
		}

	}

	/**
	 * 和activity的onDestroy()方法同步
	 */
	public void onDestroy() {
		if (MyApplication.map == null)
			MyApplication.map = new HashMap<String, Long>();
		MyApplication.map.put(TIME, time);
		MyApplication.map.put(CTIME, System.currentTimeMillis());
		clearTimer();
	}

	/**
	 * 和activity的onCreate()方法同步
	 */
	public void onCreate(Bundle bundle) {
		if (MyApplication.map == null)
			return;
		if (MyApplication.map.size() <= 0)// 这里表示没有上次未完成的计时
			return;
		long time = System.currentTimeMillis() - MyApplication.map.get(CTIME)
				- MyApplication.map.get(TIME);
		MyApplication.map.clear();
		if (time > 0)
			return;
		else {
			initTimer();
			this.time = Math.abs(time);
			timer.schedule(timerTask, 0, 1000);
			this.setText(time + textAfter);
			this.setBackgroundResource(R.drawable.shape_after_getcaptcha);
			this.setEnabled(false);
		}
	}

	public TimeButton settextAfter(String textAfter) {
		this.textAfter = textAfter;
		return this;
	}

	public TimeButton setTextBefore(String textBefore) {
		this.textBefore = textBefore;
		this.setText(textBefore);
		return this;
	}

	public TimeButton setLenght(long lenght) {
		this.lenght = lenght;
		return this;
	}

	public TimeButton setMobile(String mobile) {
		this.mobile = mobile;
		return this;
	}
	/*

*
*/
}