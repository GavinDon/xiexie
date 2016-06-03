package com.lhdz.util;

import android.content.Intent;
import android.provider.MediaStore;

public class IntentUtil {

	/*
	 * 打开相册
	 */
	public static Intent openAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// // 方式2，会先让用户选择接收到该请求的APP，可以从文件系统直接选取图片
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// intent.setType("image/*");
		return intent;

	}

	/*
	 * 打开拍照功能
	 */
	public static Intent openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		return intent;

	}

	/*
	 * 打开裁剪意图
	 */
	public static Intent openCrop() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		return intent;

	}

}
