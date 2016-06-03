package com.lhdz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lhdz.activity.R;
import com.lhdz.service.NetWorkStateService;
import com.lhdz.service.UpdateAppService;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager {

	private ProgressBar mProgressBar;
	private Dialog mDownloadDialog;

	private String mSavePath;
	private int mProgress;

	private boolean mIsCancel = false;

	private static final int DOWNLOADING = 1;
	private static final int DOWNLOAD_FINISH = 2;

//	private static final String PATH = "http://updateversion.imxiexie.com/download/android/aUpdate.txt"; // 服务器路径

	private String mVersion_code;// 版本号
	private String mVersion_name;// 版本名
	private String mVersion_desc;// 新版本增加的内容
	private String mVersion_path;// 新版本APK路径

	private Context mContext;
	int localVersion = 1;

	public UpdateManager(Context context) {
		mContext = context;
	}

	private Handler mGetVersionHandler = new Handler() {
		public void handleMessage(Message msg) {

			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(msg.obj.toString());
				mVersion_code = jsonObject.getString("version_code");
				mVersion_name = jsonObject.getString("version_name")
						+ ".apk".trim();
				mVersion_desc = jsonObject.getString("version_desc");
				mVersion_path = jsonObject.getString("version_path");

				if (isUpdate()) {
					// Toast.makeText(mContext, "需要更新",
					// Toast.LENGTH_SHORT).show();
					// 显示提示更新对话框
					showNoticeDialog();
				} else {
					// Toast.makeText(mContext, "已安装的版本号" + localVersion,
					// Toast.LENGTH_SHORT).show();
					// showNoticeDialog();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		};
	};

	private Handler mUpdateProgressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOADING:
				// 设置进度条
				mDownloadDialog.dismiss();
				mProgressBar.setProgress(mProgress);
				break;
			case DOWNLOAD_FINISH:
				// 隐藏当前下载对话框
				ToastUtils.showToast(mContext, "更新完成", 0);
				mDownloadDialog.dismiss();
				// 安装 APK 文件
				installAPK();
			}
		};
	};

	/*
	 * 检测软件是否需要更新
	 */
	public void checkUpdate() {

		String updateUrl = Config.isDebug ? Define.URL_TEST_UPDATE_VERSION : Define.URL_UPDATE_VERSION;
		RequestQueue requestQueue = Volley.newRequestQueue(mContext);
		StringRequest strReq = new StringRequest(Request.Method.GET, updateUrl,
				new StrSuccessListener(), new StrErrorListener()) {
			protected final String TYPE_UTF8_CHARSET = "charset=UTF-8";

			// 重写parseNetworkResponse方法改变返回头参数解决乱码问题
			// 主要是看服务器编码，如果服务器编码不是UTF-8的话那么就需要自己转换，反之则不需要
			@Override
			protected Response<String> parseNetworkResponse(
					NetworkResponse response) {
				try {
					String type = response.headers.get(HTTP.CONTENT_TYPE);
					if (type == null) {
						type = TYPE_UTF8_CHARSET;
						response.headers.put(HTTP.CONTENT_TYPE, type);
					} else if (!type.contains("UTF-8")) {
						type += ";" + TYPE_UTF8_CHARSET;
						response.headers.put(HTTP.CONTENT_TYPE, type);
					}
				} catch (Exception e) {
				}
				return super.parseNetworkResponse(response);
			}
		};
		strReq.setShouldCache(false); // 控制是否缓存
		requestQueue.add(strReq);

	}

	// Str请求成功回调
	private class StrSuccessListener implements Listener<String> {

		@Override
		public void onResponse(String arg0) {
			Message msg = Message.obtain();
			msg.obj = arg0;
			mGetVersionHandler.sendMessage(msg);
		}

	}

	// 更新调用 请求数据 失败
	private class StrErrorListener implements ErrorListener {

		@Override
		public void onErrorResponse(VolleyError arg0) {
			LogUtils.i(arg0.toString() + "请求出错了");
		}
	}

	/*
	 * 与本地版本比较判断是否需要更新
	 */
	protected boolean isUpdate() {
		int serverVersion = Integer.parseInt(mVersion_code);

		try {
			localVersion = mContext.getPackageManager().getPackageInfo(
					"com.lhdz.activity", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (serverVersion > localVersion)
			return true;
		else
			return false;
	}

	/*
	 * 有更新时显示提示对话框
	 */
	public void showNoticeDialog() {
		// 弹出自定义对话框、
		final SweetAlertDialog dialog = new SweetAlertDialog(mContext,
				SweetAlertDialog.SUCCESS_TYPE);
		String strVersion = mVersion_name.substring(
				mVersion_name.indexOf("-") + 1, mVersion_name.lastIndexOf("."));
		dialog.setTitleText("发现新版本(" + strVersion + ")");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setContentText(mVersion_desc);
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				Intent updateIntent = new Intent(mContext,
						UpdateAppService.class);
				updateIntent.putExtra("mVersion_path", mVersion_path);
				updateIntent.putExtra("mVersion_name", mVersion_name);
				mContext.startService(updateIntent);

				dialog.dismissWithAnimation();
			}
		});
		dialog.setCancelClickListener(new OnSweetClickListener() {

			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				dialog.dismissWithAnimation();
			}
		});

		dialog.show();
	}

	/*
	 * 显示正在下载对话框
	 */
	protected void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("下载中");
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_progress, null);
		mProgressBar = (ProgressBar) view.findViewById(R.id.id_progress);
		builder.setView(view);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 隐藏当前对话框
				dialog.dismiss();
				// 设置下载状态为取消

				mIsCancel = true;
			}
		});

		mDownloadDialog = builder.create();
		mDownloadDialog.show();

		// 下载文件
		// downloadAPK();
	}

	/*
	 * 开启新线程下载文件
	 */
	private void downloadAPK() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
//						String sdPath = Environment
//								.getExternalStorageDirectory() + "/";
//						mSavePath = sdPath + "xiexieApk";// 下载APK后保存的路径
						mSavePath = UniversalUtils.getSDPath() + Define.PATH_UPDATE_APK;// 下载APK后保存的路径
						File dir = new File(mSavePath);
						if (!dir.exists())
							dir.mkdir();

						// 下载文件
						HttpURLConnection conn = (HttpURLConnection) new URL(
								mVersion_path).openConnection();
						conn.connect();
						InputStream is = conn.getInputStream();
						int length = conn.getContentLength();

						File apkFile = new File(mSavePath, mVersion_name);
						FileOutputStream fos = new FileOutputStream(apkFile);

						int count = 0;
						byte[] buffer = new byte[1024];
						while (!mIsCancel) {
							int numread = is.read(buffer);
							count += numread;
							// 计算进度条的当前位置
							mProgress = (int) (((float) count / length) * 100);
							// 更新进度条
							mUpdateProgressHandler
									.sendEmptyMessage(DOWNLOADING);
							// 下载完成
							if (numread < 0) {
								mUpdateProgressHandler
										.sendEmptyMessage(DOWNLOAD_FINISH);
								break;
							}
							fos.write(buffer, 0, numread);
						}
						fos.close();
						is.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/*
	 * 下载到本地后执行安装
	 */
	protected void installAPK() {
		File apkFile = new File(mSavePath, mVersion_name);
		if (!apkFile.exists())
			return;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse("file://" + apkFile.toString());
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		mContext.startActivity(intent);

	}

}
