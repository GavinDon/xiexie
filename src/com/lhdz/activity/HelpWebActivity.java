package com.lhdz.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lhdz.util.UniversalUtils;

/**
 * WebView界面
 * 所有显示网页的界面使用该activity
 * @author wangf
 *
 */
public class HelpWebActivity extends BaseActivity implements OnClickListener {
	private WebView mWebview;
	private WebSettings mWebSettings;
	private TextView tvTitle;
	private TextView tvback;

	// 进度条
	private ProgressBar pbarMember = null;

	private String strTitle = "";
	private String strUrl = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sever_complaint);

		strTitle = getIntent().getStringExtra("title");
		strUrl = getIntent().getStringExtra("url");

		initView();
		initweb();

	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvback = (TextView) findViewById(R.id.public_back);
		tvback.setOnClickListener(this);
		pbarMember = (ProgressBar) findViewById(R.id.rbar_webview);

		if (!UniversalUtils.isStringEmpty(strTitle)) {
			tvTitle.setText(strTitle);
		}
	}

	
	/**
	 * 初始化WebView
	 */
	private void initweb() {
		mWebview = (WebView) findViewById(R.id.wv_sevcomplaint);
		mWebSettings = mWebview.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		mWebview.setWebChromeClient(new WebChromeClienter());
		mWebview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			};
		});

		if (!UniversalUtils.isStringEmpty(strUrl)) {
			mWebview.loadUrl(strUrl);
		}
	}

	class WebChromeClienter extends WebChromeClient {
		public void onProgressChanged(WebView view, int newProgress) {

			if (pbarMember.getProgress() < newProgress) {
				pbarMember.setProgress(newProgress);
				pbarMember.postInvalidate();
			}

			if (newProgress == 100) {
				pbarMember.setVisibility(View.GONE);
			}

			super.onProgressChanged(view, newProgress);

		};
	};


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.public_back:
			this.finish();
			break;
		}

	}

}
