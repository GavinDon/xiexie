package com.lhdz.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhdz.activity.CollectActivity;
import com.lhdz.activity.CouponActivity;
import com.lhdz.activity.HelpActivity;
import com.lhdz.activity.LoginActivity;
import com.lhdz.activity.MsgNofifyActivity;
import com.lhdz.activity.R;
import com.lhdz.activity.RegisteredActivity;
import com.lhdz.activity.ServiceProtoActivity;
import com.lhdz.activity.SettingActivity;
import com.lhdz.activity.UserInfoActivity;
import com.lhdz.adapter.MineAdapter;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.util.ComputeListViewHeight;
import com.lhdz.util.Define;
import com.lhdz.wediget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MineFragment extends Fragment implements OnClickListener,
		OnItemClickListener {
	private TextView back;
	private TextView title;
	private View view;
	private ListView mListview;
	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private RelativeLayout userAccount;
	private RelativeLayout settings;
	private TextView name, account;
	private CircleImageView micon;
	private String uname, accout;
	private LinearLayout ll_NotLogin;// 未登录时的布局
	private TextView login_button; // 登录按扭;
	private FrameLayout mFramLayout;// 我页面登录头
	private TextView quickRegister;

	private int icon[];
	private static final int COLLECT = 0;// 收藏
	private static final int COUPON = 1;// 优惠券
	private static final int MSGNOTIFY = 2;// 消息通知
	private static final int SEVISEPROTO = 3;// 服务协议
	private static final int HELP = 4;// 帮助反馈
	private static final int SETTING = 5;// 设置

	MyApplication mApp = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_mine, null);
		data.clear();
		mApp = (MyApplication) this.getActivity().getApplication();

		initViews();
		listenCenter();
		mListview.setAdapter(new MineAdapter(getActivity(), data));// 给ListView设置适配器来填充数据：
		ComputeListViewHeight.setListViewHeightBasedOnChildren(mListview);// 计算ListView的高度来适应scorllView;
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mApp.loginState) {
			queryAuthInfoData();
			userAccount.setVisibility(View.VISIBLE);
			ll_NotLogin.setVisibility(View.GONE);
			name.setText(uname);
			account.setText(accout);
		} else {
			userAccount.setVisibility(View.GONE);
			ll_NotLogin.setVisibility(View.VISIBLE);
			uname = "点击登录";
			accout = "";
		}

	}

	private void initViews() {
		title = (TextView) view.findViewById(R.id.tv_title);
		back = (TextView) view.findViewById(R.id.public_back);
		name = (TextView) view.findViewById(R.id.mine_name);
		name.setText(uname);
		account = (TextView) view.findViewById(R.id.tv_mine_account);
		account.setText(accout);
		micon = (CircleImageView) view.findViewById(R.id.iv_mine_icon);
		title.setText("我");
		mListview = (ListView) view.findViewById(R.id.lv_mine);
		flateData();// "我"页面listview列表内容
		userAccount = (RelativeLayout) view.findViewById(R.id.rl_mine);

		settings = (RelativeLayout) view.findViewById(R.id.rl_set);
		ll_NotLogin = (LinearLayout) view.findViewById(R.id.notlogin);// 未登录时显示的UI界面
		login_button = (TextView) view.findViewById(R.id.click_login);
		mFramLayout = (FrameLayout) view.findViewById(R.id.fl_login);
		quickRegister = (TextView) view.findViewById(R.id.tv_register);
	}

	/*
	 * 监听事件 。对于控件触发后的效果;
	 */
	private void listenCenter() {
		back.setOnClickListener(this);
		mListview.setOnItemClickListener(this);
		login_button.setOnClickListener(this);// 登录按钮
		quickRegister.setOnClickListener(this);//注册按钮;
		settings.setOnClickListener(this);
		mFramLayout.setOnClickListener(this);
	}

	/*
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.public_back:
			getActivity().finish();
			break;
		case R.id.rl_set:
			intent.setClass(getActivity(), SettingActivity.class);
			startActivity(intent);
			break;
		// case R.id.rl_mine:
		// // 判断用户是否登录
		// if (mApp.loginState) {
		// intent.setClass(getActivity(), UserInfoActivity.class);
		// startActivity(intent);
		// } else {
		// intent = new Intent(getActivity(), LoginActivity.class);
		// startActivity(intent);
		// }
		// break;
		case R.id.fl_login:// 若已经登录跳入个人信息页面
			if (mApp.loginState) {
				intent.setClass(getActivity(), UserInfoActivity.class);
				startActivity(intent);
			}
			break;

		case R.id.click_login:// 点击登录
			intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_register:
			intent = new Intent(getActivity(), RegisteredActivity.class);
			startActivity(intent);
			break;

		}

	}

	/*
	 * 加载数据
	 */
	public void queryAuthInfoData() {
		// 查询数据库
		DataBaseService ds = new DataBaseService(getActivity());
		String sql = DbOprationBuilder.queryAllBuilder("authInfo");
		List<Map<String, String>> dataList = ds.query(sql);
		for (int i = 0; i < dataList.size(); i++) {
			uname = dataList.get(i).get("nickName");
			accout = dataList.get(i).get("accout");
			setUserIconView(dataList.get(i).get("headIcon"));
		}
	}

	/*
	 * 填充数据源
	 */
	private void flateData() {
		String[] ModelData = new String[] { "收藏", "优惠卡券", "消息通知", "服务协议",
				"帮助反馈" };
		icon = new int[] { R.drawable.icon_28_favorite,
				R.drawable.icon_28_card, R.drawable.icon_28_info,
				R.drawable.icon_28_agree, R.drawable.icon_28_help };
		for (int i = 0; i < ModelData.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", ModelData[i]);
			map.put("icons", icon[i]);
			data.add(map);
		}
	}
	
	
	
	
	/**
	 * 加载用户图像
	 */
	private void setUserIconView(String loadUrl){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.pic_me_gray)
			.showImageOnFail(R.drawable.pic_me_gray)
			.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
			.build();
		String IconPath = Define.URL_DOWNLOAD_USER_IMAGE + MyApplication.userId +".jpg";
		ImageLoader.getInstance().displayImage(IconPath, micon, options);
	}
	
	
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		switch (position) {
		case COLLECT:
			
			if(mApp.loginState){
				intent.setClass(getActivity(), CollectActivity.class);
				startActivity(intent);
			}else{
				intent.setClass(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
			
			break;
		case COUPON:
			intent.setClass(getActivity(), CouponActivity.class);
			startActivity(intent);
			break;
		case MSGNOTIFY:
			intent.setClass(getActivity(), MsgNofifyActivity.class);
			startActivity(intent);
			break;
		case SEVISEPROTO:
			intent.setClass(getActivity(), ServiceProtoActivity.class);
			startActivity(intent);
			break;
		case HELP:
			intent.setClass(getActivity(), HelpActivity.class);
			startActivity(intent);
			break;
		case SETTING:
			intent.setClass(getActivity(), SettingActivity.class);
			startActivity(intent);
			break;

		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onStop() {
		super.onStop();

	}

}
