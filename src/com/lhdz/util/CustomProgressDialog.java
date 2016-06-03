package com.lhdz.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lhdz.activity.R;
import com.lhdz.dataUtil.HandleMsgDistribute;

public class CustomProgressDialog extends ProgressDialog {
	private Context mContext;
	private String contentTip;// 对话框下面的提示
	private int animotionId; // 动画Id
	private ImageView mImageView;
	private TextView mtextTip;
	private AnimationDrawable mAnimationDrawable;
	
	public final static int ANIMATION_TYPE_ONE = 1;
	public final static int ANIMATION_TYPE_TWO = 2;
	
	private final int iAniomtionId_1 = R.anim.customprogress_animation1;
	private final int iAniomtionId_2 = R.anim.customprogress_animation2;

//	private static CustomProgressDialog progressDialog;
//	
//	public static CustomProgressDialog getInstance(Context context){
//		if(progressDialog == null){
//			progressDialog = new CustomProgressDialog(context);
//		}
//		return progressDialog;
//	}
	
	public CustomProgressDialog(Context context, String content, int animotionId) {
		super(context, R.style.CustomProgressDialog);
		this.mContext = context;
		this.contentTip = content;
		this.animotionId = animotionId;
		setCanceledOnTouchOutside(false);//默认点击外面动画不消失
	}

	
	public CustomProgressDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		this.mContext = context;
		setCanceledOnTouchOutside(false);//默认点击外面动画不消失
		initDefaultData();
	}
	
	
	/**
	 * 设置默认的动画效果和提示文字信息
	 */
	private void initDefaultData(){
		this.contentTip = "正在玩命加载中···";
		this.animotionId = iAniomtionId_1;
	}
	
	
	/**
	 * 设置提示文字信息
	 * @param text
	 */
	public void setTextInfo(String text){
		if(!UniversalUtils.isStringEmpty(text)){
			contentTip = text;
		}
	}
	
	
	/**
	 * 设置动画显示内容
	 * 
	 * @param animotionId
	 */
	public void setAnimationInfo(int animotionType) {
		if (animotionType == ANIMATION_TYPE_ONE) {
			this.animotionId = iAniomtionId_1;
		} else if (animotionType == ANIMATION_TYPE_TWO) {
			this.animotionId = iAniomtionId_2;
		}
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Window mWindow = getWindow(); 
//		WindowManager.LayoutParams lp = mWindow.getAttributes(); 
//		lp.dimAmount = 0.7f;//黑暗度--灰度
		
		initView();
		initData();

	}

	
	/**
	 * 初始化页面控件
	 */
	private void initView() {
		setContentView(R.layout.customprogress_dialog);
		mImageView = (ImageView) findViewById(R.id.loadingIv);
		mtextTip = (TextView) findViewById(R.id.loadingTv);
	}

	
	/**
	 * 初始化动画显示
	 */
	private void initData() {
		mImageView.setBackgroundResource(animotionId);
		mAnimationDrawable = (AnimationDrawable) mImageView.getBackground();

		mtextTip.setText(contentTip);

		mImageView.post(new Runnable() {
			// 使用Post是为了解决只显示一帧
			@Override
			public void run() {
				mAnimationDrawable.start();
			}
		});
	}
	
	
}
