package com.lhdz.wediget;

import com.lhdz.activity.JoinStarActivity;
import com.lhdz.activity.R;
import com.lhdz.activity.StarCompanyActivity;
import com.lhdz.util.GetScreenInchUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopMenu extends PopupWindow implements OnClickListener {

	public interface onClickItemDissmiss {
		public void OnDissmiss(int id);
	}

	private LayoutInflater inflater;
	private PopupWindow popupWindow;
	private View view;
	private onClickItemDissmiss listener;
	private TextView starCom, joinStar, notice, nearby;
	private Activity context;
	private ImageView iv_close;

	public PopMenu(Activity context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.add_popup, null);
		// 需要计算魅族，华为某些手机下面虚拟键盘的高度。
		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				GetScreenInchUtil.getDpi(context)
						- GetScreenInchUtil.getVrtualBtnHeight(context));
		popupWindow.setBackgroundDrawable(new ColorDrawable(0xffffff));
		clickIndent();

	}

	public void clickIndent() {
		starCom = (TextView) view.findViewById(R.id.pop_starcom);
		joinStar = (TextView) view.findViewById(R.id.pop_joinstar);
		notice = (TextView) view.findViewById(R.id.pop_notice);
		nearby = (TextView) view.findViewById(R.id.pop_nearby);
		iv_close = (ImageView) view.findViewById(R.id.iv_close);
		iv_close.setOnClickListener(this);
		starCom.setOnClickListener(this);
		joinStar.setOnClickListener(this);

	}

	public void OnDissmiss(int id) {

	}

	public void showAsDropDown(View parent) {

		// popupWindow.showAsDropDown(parent, 0, 120);
		popupWindow.showAtLocation(parent, Gravity.CENTER | Gravity.BOTTOM, 0,
				120);
		popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.update();
		popupWindow.setOnDismissListener(new OnDismissListener() {

			// 在dismiss中恢复透明度
			public void onDismiss() {
				WindowManager.LayoutParams lp = context.getWindow()
						.getAttributes();
				lp.alpha = 1f;
				context.getWindow().setAttributes(lp);

			}
		});
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.pop_starcom:
			intent.setClass(context, StarCompanyActivity.class);
			context.startActivity(intent);

			break;
		case R.id.pop_joinstar:
			// if (((MyApplication)context.getApplication()).loginState) {
			intent.setClass(context, JoinStarActivity.class);
			context.startActivity(intent);
			// }else {
			// intent = new Intent(context, LoginActivity.class);
			// context.startActivity(intent);
			// }

			break;
		case R.id.iv_close:
			popupWindow.dismiss();

		}
		popupWindow.dismiss();
	}

}
