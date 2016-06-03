package com.lhdz.wediget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.lhdz.activity.R;

public class CityPop implements OnClickListener{

	private Activity activity;
	private View view;
	private PopupWindow myPopmenu;
	private TextView back;
	private TextView confirm;
	private CityCallBack listener;

	public CityPop(Activity activity, View view, ColorDrawable cd) {
		this.activity = activity;
		this.view = view;
		myPopmenu = new PopupWindow(view);
		myPopmenu.setBackgroundDrawable(cd);
		clickIndent();
	}

	private void clickIndent() {
		back=(TextView) view.findViewById(R.id.pop_city_cacel);
		confirm=(TextView) view.findViewById(R.id.pop_city_confirm);
		back.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	public void showPopmenu(View parent, int width, int height, int gravity,
			int x, int y) {
		myPopmenu.setWidth(width);
		myPopmenu.setHeight(height);
		myPopmenu.setTouchable(true);
//		myPopmenu.setOutsideTouchable(true);
		myPopmenu.setFocusable(false);
		myPopmenu.showAtLocation(parent, gravity, x, y);
		myPopmenu.update();
		myPopmenu.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = activity.getWindow()
						.getAttributes();
				lp.alpha = 1f;
				activity.getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_city_cacel:
			listener.callBackCacel(myPopmenu);
			break;
		case R.id.pop_city_confirm:
			listener.callBackConfirm(myPopmenu);
			break;

		}
	} 
	
	
	public void setCityOnClickListener(CityCallBack listener){
		this.listener = listener;
	}
	
	
	public interface CityCallBack {
		public void callBackConfirm(PopupWindow myPopmenu);
		public void callBackCacel(PopupWindow myPopmenu);
	}
	
	
	public void setOutsideTouchable(boolean b){
		myPopmenu.setOutsideTouchable(b);
	}
	

}
