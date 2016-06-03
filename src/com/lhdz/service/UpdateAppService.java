package com.lhdz.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lhdz.activity.R;
import com.lhdz.fragment.HomePageFragment;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.util.Define;
import com.lhdz.util.UniversalUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

/**
 * 
 * @author ln
 * 
 */
public class UpdateAppService extends Service {
	// 标题
	private int titleId = 0;
	private Intent intent;
	// 路径
	private File mSavefile;// 保存APK的文件
	private File mSaveFileApk;
	// 通知栏
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	// 通知栏跳转Intent;
	private Intent updateIntent;
	private PendingIntent mPendingIntent;
	// 下载状态
	private static final int DOWNLOAD_COMPLETE = 1;// 下载成功
	private static final int DOWNLOAD_FAIL = 2;// 下载失败
	// 下载的参数
	int downloadCount = 0;
	int currentSize = 0;
	long totalSize = 0;
	int updateTotalSize = 0;
	// 创建通知栏
	private RemoteViews contentView;
	// 版本号与版本名
	private String version_name;
	private String version_path;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent != null) {

			version_name = intent.getStringExtra("mVersion_name");
			version_path = intent.getStringExtra("mVersion_path");

			// 判断是否挂载有SD卡
			if (android.os.Environment.MEDIA_MOUNTED
					.equals(android.os.Environment.getExternalStorageState())) {
//				String sdPath = Environment.getExternalStorageDirectory() + "/";// SD卡中的路径
//				String mSavePath = sdPath + "xiexieApk";// apk在SD卡中的保存路径mSavePath;
				String mSavePath = UniversalUtils.getSDPath() + Define.PATH_UPDATE_APK;// apk在SD卡中的保存路径mSavePath;
				mSavefile = new File(mSavePath);
				mSaveFileApk = new File(mSavefile.getPath(), version_name);// 版本名+apk
				System.out.println(mSaveFileApk);
				// 如果文件不存在则创建一个文件
				if (!mSavefile.exists()) {
					mSavefile.mkdir();
				}
				// 获取通知栏管理对象;
				mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				mNotification = new Notification();
				// 设置下载过程中点击通知栏跳转到主界面中
				updateIntent = new Intent(this, HomePageFragment.class);
				mPendingIntent = PendingIntent.getActivity(this, 0,
						updateIntent, 0);
				// 通知栏显示内容
				mNotification.icon = R.drawable.app_logo;
				mNotification.tickerText = "歇歇正在下载";
				mNotification.setLatestEventInfo(this, "歇歇", "0%",
						mPendingIntent);
				// 发出通知
				mNotificationManager.notify(0, mNotification);
				// 开启线程下载
				new Thread(new updateRunable()).start();
			}

		}
		return super.onStartCommand(intent, flags, startId);

	}

	/**
	 * @param downloadUpdateFile通过Http下载文件
	 */

	public long downLoadUpdateFile(String downloadUrl, File saveFileName)
			throws IOException {

		HttpURLConnection httpConnection = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			URL url = new URL(downloadUrl);
			httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection
					.setRequestProperty("Accept-Encoding", "identity");
//			httpConnection
//			.setRequestProperty("User-Agent", "PacificHttpClient");
			if (currentSize > 0) {
				httpConnection.setRequestProperty("RANGE", "bytes="
						+ currentSize + "-");
			}
			httpConnection.setConnectTimeout(10000);
			if (httpConnection.getResponseCode() == 404) {
				throw new Exception("fail!");
			}
			is = httpConnection.getInputStream();
			fos = new FileOutputStream(saveFileName, false);
			updateTotalSize = httpConnection.getContentLength();
			byte buffer[] = new byte[1024];
			int readsize = 0;
			while ((readsize = is.read(buffer)) != -1) {
				totalSize += readsize;

				fos.write(buffer, 0, readsize);

				if ((downloadCount == 0)
						|| (int) (totalSize * 100 / updateTotalSize) - 2 > downloadCount) {
					downloadCount += 2;

//					mNotification.setLatestEventInfo(UpdateAppService.this,
//							"正在下载", (int) totalSize * 100 / updateTotalSize
//									+ "%", mPendingIntent);

					/***
					 * 在这里我们用自定的view来显示Notification
					 */
					mNotification.contentView = new RemoteViews(getPackageName(), R.layout.notification_item);
//					mNotification.contentView.setTextViewText(
//							R.id.notificationTitle, "正在下载  "+(int) totalSize * 100 / updateTotalSize+ "%");
					mNotification.contentView.setTextViewText(R.id.notificationTitle, "正在下载···");
					mNotification.contentView.setProgressBar(R.id.notificationProgress, 100, downloadCount,false);

					mNotificationManager.notify(0, mNotification);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
			if (is != null) {
				is.close();
			}
			if (fos != null) {
				fos.close();
			}
		}
		if(fos != null){
			fos.flush();
		}
		return totalSize;
	}

	//
	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				Uri uri = Uri.fromFile(mSaveFileApk);// APK文件URI
				// 安装APK intent;
//				Intent installIntent = new Intent(Intent.ACTION_VIEW);
//				installIntent.setDataAndType(uri,
//						"application/vnd.android.package-archive");
				// 通知栏跳转Intent
//				mPendingIntent = PendingIntent.getActivity(
//						UpdateAppService.this, 0, installIntent, 0);
				mNotification.setLatestEventInfo(UpdateAppService.this, "歇歇","下载完成", mPendingIntent);
				mNotification.flags = Notification.FLAG_AUTO_CANCEL;
				mNotificationManager.notify(0, mNotification);
				
				sendInstallApkBroad(uri.toString());//发送广播--弹出安装页面
				
				break;
			case DOWNLOAD_FAIL:
			default:
				stopService(updateIntent);
				break;
			}
		};
	};

	class updateRunable implements Runnable {
		Message mMessage = updateHandler.obtainMessage();

		@Override
		public void run() {

			mMessage.what = DOWNLOAD_COMPLETE;
			if (!mSavefile.exists()) {
				mSavefile.mkdir();
			}
			if (!mSaveFileApk.exists()) {
				try {
					mSaveFileApk.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				long downLoad = downLoadUpdateFile(version_path, mSaveFileApk);
				if (downLoad > 0) {
					// 下载成功
					updateHandler.sendMessage(mMessage);
				} else {
					// 下载失败
					mMessage.what = DOWNLOAD_FAIL;
					updateHandler.sendMessage(mMessage);
				}

			} catch (IOException e) {
				e.printStackTrace();
				// 下载失败
				mMessage.what = DOWNLOAD_FAIL;
				updateHandler.sendMessage(mMessage);
			}
		}

	}
	
	
	
	/**
	 * 发送广播，安装apk文件
	 */
	public void sendInstallApkBroad(String installApkPath) {
		Intent intent = new Intent();
		intent.setAction(Define.BROAD_SERVICE_UPDATE_VERSION);
		intent.putExtra("InstallApkPath", installApkPath);
		sendBroadcast(intent);
	}
	
}
