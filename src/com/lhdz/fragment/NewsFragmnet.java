package com.lhdz.fragment;

/**
 * 资讯界面
 * @author 王哲
 * @data 2015-8-26
 */
import java.io.File;

import android.graphics.AvoidXfermode.Mode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lhdz.activity.R;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;

public class NewsFragmnet extends Fragment {
	// private ListView mLv;
	// // 资讯列表适配器
	// private NewsAdapter adapter;
	// private ArrayList<String> mList;
	// private ArrayList<String> mList1;
	// private TextView title;
	// private RadioGroup mRg;
	// private RadioButton environment;
	// private RadioButton health;
	// private RadioButton healthy;
	// private RadioButton ambulance;

	private WebView newsWebView;
	private WebSettings mWebSettings;
	// private String newsUrl = "http://imxiexie.com/Information/index.html";
	private CustomProgressDialog mCustomProgressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_webview, null);
		newsWebView = (WebView) view.findViewById(R.id.mv_news);

		return view;
	}

	@Override
	public void onResume() {
		newsWebView.loadUrl(Define.URL_NEWS);
		super.onResume();
	}
	private String cacheDirPath;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mWebSettings = newsWebView.getSettings();
		mWebSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		cacheDirPath = getActivity().getDir("webview", getActivity().MODE_PRIVATE).getAbsolutePath();
		mWebSettings.setDatabasePath(cacheDirPath);
		mWebSettings.setAppCachePath(cacheDirPath);
		mWebSettings.setAppCacheMaxSize(5*1024*1024);
		LogUtils.i("cacheDirPath=" + cacheDirPath);
		mWebSettings.setJavaScriptEnabled(true);
		// 设置自适应屏幕
		mWebSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		if (MyApplication.isFresh) {
			clearWebViewCache();
			MyApplication.isFresh = false;
		}

		mCustomProgressDialog = new CustomProgressDialog(getActivity());
		mCustomProgressDialog.show();
		newsWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// 应该有一个进度条。如果加载完成在此处关掉进度框
				mCustomProgressDialog.dismiss();
			}
		});
	}

	/**
	 * 清除WebView缓存
	 */
	public void clearWebViewCache() {

		// 清理Webview缓存数据库
		try {
			getActivity().deleteDatabase("webview.db");
			getActivity().deleteDatabase("webviewCache.db");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		File webviewCacheDir = new File(cacheDirPath);

		// 删除webview 缓存目录
		if (webviewCacheDir.exists()) {
			deleteFile(webviewCacheDir);
		}
	}

	/*
	 * 删除缓存文件
	 */
	public void deleteFile(File file) {

		if (file.exists()) {
			if (file.isFile()) {
				file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					deleteFile(files[i]);
				}
			}
			file.delete();
		} else {
		}
	}

}
