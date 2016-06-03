package com.lhdz.util;

import android.content.Context;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

public class DialogUtil {
private Context context;
	
	public DialogUtil(Context context ) {
		this.context=context;
	
		
	}
	/*
	 * 网络不正常时打开对话框提示用户
	 */
	public void openWifiDialog(){
		//弹出自定义对话框、
		final SweetAlertDialog dialog=new SweetAlertDialog(context,SweetAlertDialog.ERROR_TYPE);
		dialog.setTitleText("您的网络未连接，是否要打开网络？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setContentText("打开wifi设置");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				dialog.dismissWithAnimation();
			}
		});
	
		dialog.show();
	}
	
}
