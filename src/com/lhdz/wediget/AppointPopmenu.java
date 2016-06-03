package com.lhdz.wediget;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lhdz.activity.HomeAppointmentActivity;
import com.lhdz.activity.HomebjActivity;
import com.lhdz.activity.R;
import com.lhdz.adapter.AdapterShotcutAppoint;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSeeCmpServiceInfo_Pro;
import com.lhdz.util.GetScreenInchUtil;

public class AppointPopmenu extends PopupWindow implements OnClickListener,
		OnItemClickListener {
	PopupWindow myPop;
	private View view;
	private Activity context;
	private TextView popCancel;
	private GridView pop_appointment_btn;
	private List<HsUserSeeCmpServiceInfo_Pro> serviceInfoList;
	private Map<String, String>  companyDetail;

	public AppointPopmenu(Activity context,
			List<HsUserSeeCmpServiceInfo_Pro> serviceInfoList, Map<String, String> starCompanyDetail) {
		this.context = context;
		this.serviceInfoList = serviceInfoList;
		this.companyDetail=starCompanyDetail;
		view = LayoutInflater.from(context).inflate(R.layout.pop_appointment,
				null);
		myPop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		ColorDrawable cd = new ColorDrawable(R.color.white);
		myPop.setBackgroundDrawable(cd);
		clikIndent();
	}

	public void showPopwindow(View parent) {
		myPop.setOutsideTouchable(true);
		myPop.setAnimationStyle(R.style.mypopwindow_anim_style);
		myPop.showAtLocation(parent, Gravity.BOTTOM, 0, GetScreenInchUtil.getVrtualBtnHeight(context));
		myPop.update();
		myPop.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = context.getWindow()
						.getAttributes();
				lp.alpha = 1.0f;
				context.getWindow().setAttributes(lp);
			}
		});
	}

	public void clikIndent() {
		pop_appointment_btn = (GridView) view.findViewById(R.id.pop_gv_appoint);
		pop_appointment_btn.setAdapter(new AdapterShotcutAppoint(context,serviceInfoList));
		popCancel = (TextView) view.findViewById(R.id.pop_cacel);
		popCancel.setOnClickListener(this);
		pop_appointment_btn.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.pop_cacel:
			myPop.dismiss();
		}
		myPop.dismiss();
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(context, HomeAppointmentActivity.class);
		intent.putExtra("starCompanyDetail",(Serializable)companyDetail );
		intent.putExtra("cmpServiceInfo", (Serializable)serviceInfoList.get(position));
//		String map = appointBtnData.get(position).get("Sonid");
//		intent.putExtra("stringData", map);
		context.startActivity(intent);
		
		myPop.dismiss();
	}

}
