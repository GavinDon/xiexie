package com.lhdz.util;

import android.content.Intent;
import android.provider.MediaStore;

public class IntentUtil {

	/*
	 * �����
	 */
	public static Intent openAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// // ��ʽ2���������û�ѡ����յ��������APP�����Դ��ļ�ϵͳֱ��ѡȡͼƬ
		// Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// intent.setType("image/*");
		return intent;

	}

	/*
	 * �����չ���
	 */
	public static Intent openCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		return intent;

	}

	/*
	 * �򿪲ü���ͼ
	 */
	public static Intent openCrop() {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		return intent;

	}

}
