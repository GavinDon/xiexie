package com.lhdz.util;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.lhdz.activity.R;

public class ChangeUserIconUtil implements OnClickListener, OnDismissListener {
	private Activity context;
	private PopupWindow mPopupWindow;
	private View view;
	private TextView tv_camera;
	private TextView tv_album;
	private TextView tv_cancle;

	private Intent intent;
	public static Uri imgUri;

	public ChangeUserIconUtil(Activity context) {
		this.context = context;
		// activity.getLayoutInflater();三种方式获取layoutInflater;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService("layout_inflater");
		view = inflater.inflate(R.layout.chage_use_icon, null, false);
		// 设置popwindow窗口大小
		mPopupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		initView();
	}

	private void initView() {
		tv_cancle = (TextView) view.findViewById(R.id.tv_cancel);
		tv_camera = (TextView) view.findViewById(R.id.tv_camera);
		tv_album = (TextView) view.findViewById(R.id.tv_album);
		tv_camera.setOnClickListener(this);
		tv_album.setOnClickListener(this);
		tv_cancle.setOnClickListener(this);

	}

	/*
	 * 设置popwindow常用属性并弹出
	 */
	public void showPopwindow(View parent) {
		// 设置动画
		mPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		// 设置popwindow背景：
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xffffff));
		mPopupWindow.setOutsideTouchable(false);
		mPopupWindow.showAtLocation(parent, Gravity.BOTTOM, 0,
				GetScreenInchUtil.getVrtualBtnHeight(context));
		// 弹出窗口
		mPopupWindow.update();
		mPopupWindow.setOnDismissListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_camera:// 调用相机
			intent = IntentUtil.openCamera();
			// 下面这句指定调用相机拍照后的照片存储的路径
			File file = new File(Environment.getExternalStorageDirectory(),
					Define.PHOTO_FILE_NAME);

			// Uri imgUri = Uri.fromFile(file);
			if (!file.exists()) {
				file.mkdir();
			}

			imgUri = Uri.fromFile(new File(file, "avatar_"
					+ String.valueOf(System.currentTimeMillis()) + ".png"));
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
			// 由于一些手机禁止了系统公共的默认路径的访问,所以使用自己定义的路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
			context.startActivityForResult(intent, Define.PICK_FROM_CAMERA);
			mPopupWindow.dismiss();
			break;
		case R.id.tv_album:// 调用相册
			intent = IntentUtil.openAlbum();
			context.startActivityForResult(intent, Define.PICK_FROM_ALBUM);
			mPopupWindow.dismiss();
			break;
		case R.id.tv_cancel:
			mPopupWindow.dismiss();
			break;
		}

	}

	@Override
	public void onDismiss() {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.alpha = 1f;
		context.getWindow().setAttributes(lp);
	}

}
