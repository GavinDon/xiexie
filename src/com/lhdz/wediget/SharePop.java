package com.lhdz.wediget;

import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.activity.R;
import com.lhdz.util.GetScreenInchUtil;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.wechat.Constants;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

public class SharePop implements OnClickListener {

	private Activity activity;
	private View view;
	private PopupWindow myPopmenu;
	private ImageView back;

	private TextView pop_weixin;
	private TextView pop_circle;
	private TextView pop_qzone;
	private TextView pop_sina;

	public SharePop(Activity activity, View view, ColorDrawable cd) {
		this.activity = activity;
		this.view = view;
		myPopmenu = new PopupWindow(view);
		myPopmenu.setBackgroundDrawable(cd);
		clickIndent();
	}

	private void clickIndent() {
		back = (ImageView) view.findViewById(R.id.pop_cacel);
		back.setOnClickListener(this);

		pop_weixin = (TextView) view.findViewById(R.id.pop_wechat);
		pop_circle = (TextView) view.findViewById(R.id.pop_circle);
		pop_qzone = (TextView) view.findViewById(R.id.pop_qzone);
		pop_sina = (TextView) view.findViewById(R.id.pop_sina);

		pop_weixin.setOnClickListener(this);
		pop_circle.setOnClickListener(this);
		pop_qzone.setOnClickListener(this);
		pop_sina.setOnClickListener(this);
	}

	public void showPopmenu(View parent, int width, int height, int gravity,
			int x, int y) {
		myPopmenu.setWidth(width);
		myPopmenu.setHeight(height);
		myPopmenu.setTouchable(true);
		myPopmenu.setAnimationStyle(R.style.mypopwindow_anim_style);
		myPopmenu.setOutsideTouchable(true);
		myPopmenu.setFocusable(true);
		myPopmenu.showAtLocation(parent, gravity, x,
				GetScreenInchUtil.getVrtualBtnHeight(activity));
		myPopmenu.update();
		myPopmenu.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = activity.getWindow()
						.getAttributes();
				lp.alpha = 1f;
				activity.getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.pop_cacel:
			myPopmenu.dismiss();
			break;
		case R.id.pop_wechat:
			// 分享给微信朋友
			 UMImage image = new UMImage(activity,Constants.shareContent.UMImage);
			new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
					.setCallback(umShareListener)
					.withTitle("歇歇生活")
					.withText("歇歇生活不止于服务！")
					.withTargetUrl(Constants.shareContent.TargetUrl)
					.withMedia(image)
					.share();
			myPopmenu.dismiss();

			break;
		case R.id.pop_circle:
			// 分享到微信朋友圈
			UMImage image2 = new UMImage(activity,Constants.shareContent.UMImage);
			new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
					.setCallback(umShareListener)
					.withTitle("歇歇生活")
					.withText("歇歇生活不止于服务！")
					.withTargetUrl(Constants.shareContent.TargetUrl)
					.withMedia(image2)
					.share();
			myPopmenu.dismiss();
			break;
		case R.id.pop_qzone:
			ToastUtils.show(activity, "敬请期待", 1);
//			// 授权登录
//			UMShareAPI umShareAPI = UMShareAPI.get(activity);
//			umShareAPI.doOauthVerify(activity, SHARE_MEDIA.WEIXIN,
//					umAuthListener);
//			// umShareAPI.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN,
//			// umAuthListener);

			myPopmenu.dismiss();
			break;
		case R.id.pop_sina:
			ToastUtils.show(activity, "敬请期待", 1);
//			UMImage image3 = new UMImage(activity,Constants.shareContent.UMImage);
//			new ShareAction(activity)
//					.setPlatform(SHARE_MEDIA.SINA)
//					.setCallback(umShareListener)
//					// .withTitle("歇歇分享")
//					// //注意新浪分享的title是不显示的，URL链接只能加在分享文字后显示，并且需要确保withText()不为空
//					.withText("测试分享微博")
//					.withTargetUrl(Constants.shareContent.TargetUrl)
//					.withMedia(image3).share();
			myPopmenu.dismiss();
			break;

		}
	}
	
	

	/** auth callback interface **/
	private UMAuthListener umAuthListener = new UMAuthListener() {
		@Override
		public void onComplete(SHARE_MEDIA platform, int action,
				Map<String, String> data) {
			StringBuffer sb = new StringBuffer();
			ToastUtils.show(activity, "登录成功", Toast.LENGTH_SHORT);
			Set<String> keys = data.keySet();
			// 遍历拿出所有用户信息
			for (String key : keys) {
				sb.append(key + "=" + data.get(key).toString() + "\r\n");
			}
			LogUtils.i(sb.toString());

		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			ToastUtils.show(activity, "登录出错", Toast.LENGTH_SHORT);
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			ToastUtils.show(activity, "登录取消", Toast.LENGTH_SHORT);
		}
	};

	
	
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			ToastUtils.show(activity, "分享成功", Toast.LENGTH_SHORT);

		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			ToastUtils.show(activity, "分享失败", Toast.LENGTH_SHORT);
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			ToastUtils.show(activity, "你取消了分享", Toast.LENGTH_SHORT);
		}
	};

}
