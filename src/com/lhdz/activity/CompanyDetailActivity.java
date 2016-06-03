package com.lhdz.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSeeCmpServiceInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserSeeCmpServiceInfoGet_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.AppointPopmenu;
import com.lhdz.wediget.SharePop;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


/**
 * 明星公司相详情
 * @author wangf
 *
 */
public class CompanyDetailActivity extends BaseActivity implements OnClickListener {
	private TextView back;
	private RadioButton btShare;
	private View popView;
	private ColorDrawable cd;
	private LinearLayout llAppoint;
	private LinearLayout llComment;
	private LinearLayout commentGood;
	private TextView Commentmore;
	private RadioButton btCollect; // 收藏按扭
	private RadioButton btAppointOreder;// 预约下单按扭
	private TextView tv_shares;

	private ImageView compaIcon;//公司图标
	private TextView tv_comname;// 公司名称
	private RatingBar company_rating;// 公司星级
	private TextView tv_address;// 公司地址
	private TextView tv_ltdintro;//公司简介
	private TextView success_order_num;// 成功订单数
	private TextView like_num;// 雇主喜欢数
	
	private RadioButton rb_AuthFlag;
	private RadioButton rb_Filing;
	private RadioButton rb_OffLine;
	private RadioButton rb_Nominate;
	
	
	private LinearLayout servie_ll;//服务分类的layout，需要动态的去加载

//	List<Map<String, String>> appHomeDataList = null;// 首页快捷下单数据
	private Map<String, String> starCompanyDetail;
	private List<List<Map<String, String>>> data;
	private List<HsUserSeeCmpServiceInfo_Pro> serviceInfoList = new ArrayList<HsUserSeeCmpServiceInfo_Pro>();//公司服务分类信息
	
	private CustomProgressDialog mCustomProgressDialog;
	
	private int seqCmpServiceInfo = -1;//请求公司服务信息的sequence
	
	private final static int BTNTIMEROVER = 0;
	private final static int MSG_LOAD_SUCCESS = 1;
	
	private boolean isCollect = false;
	
	
	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				
				setServiceClassifyData();//显示服务分类信息
				
				break;
			case BTNTIMEROVER:
				handler.removeCallbacks(btnTimerRunnable);
				mCustomProgressDialog.dismiss();
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_company_detail);

		starCompanyDetail = (Map<String, String>) getIntent().getSerializableExtra("starCompanyDetail");
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		

		queryCollectData();
		initviews();
		listenerCenter();// 监听中心
		
		setViewData();
		
//		if(MyApplication.loginState){
			loadCmpServiceInfoData();//请求公司服务信息
//		}else{
//			Toast.makeText(this, "您未登录，请登录后查看更多精彩内容", 0).show();
//		}

	}


	/**
	 * 初始化view
	 */
	private void initviews() {
		back = (TextView) findViewById(R.id.tv_reback);
		btShare = (RadioButton) findViewById(R.id.bt_share);//分享按钮
		tv_shares=(TextView) findViewById(R.id.tv_shares);
		llAppoint = (LinearLayout) findViewById(R.id.ll_appoint);//预约下单layout
		btCollect = (RadioButton) findViewById(R.id.bt_collect);//收藏按钮
		btAppointOreder = (RadioButton) findViewById(R.id.bt_appoint);//预约下单按钮
		
		compaIcon = (ImageView) findViewById(R.id.compaIcon);//公司图标
		tv_comname = (TextView) findViewById(R.id.tv_comname);// 公司名称
		company_rating = (RatingBar) findViewById(R.id.company_rating);// 公司星级
		tv_address = (TextView) findViewById(R.id.tv_address);// 公司地址
		tv_ltdintro = (TextView) findViewById(R.id.tv_ltdintro);//公司简介
		success_order_num = (TextView) findViewById(R.id.success_order_num);// 成功订单数
		like_num = (TextView) findViewById(R.id.like_num);// 雇主喜欢数
		
		servie_ll = (LinearLayout) findViewById(R.id.servie_ll);
		
		rb_AuthFlag = (RadioButton) findViewById(R.id.rb_AuthFlag);//身份认证
		rb_Filing = (RadioButton) findViewById(R.id.rb_Filing);//证件备案
		rb_OffLine = (RadioButton) findViewById(R.id.rb_OffLine);//线下验证
		rb_Nominate = (RadioButton) findViewById(R.id.rb_Nominate);//官方推荐
		
		// commentGood = (LinearLayout) findViewById(R.id.comment_good);
		// Commentmore = (TextView) findViewById(R.id.tv_more_comment);

	}
	
	
	/**
	 * 设置控件数据
	 */
	private void setViewData(){
		tv_comname.setText(starCompanyDetail.get("szName"));//公司名称
		company_rating.setRating(UniversalUtils.processRatingLevel(starCompanyDetail.get("iStarLevel")));//公司星级
		tv_address.setText("地址："+starCompanyDetail.get("szAddr"));//公司地址
		tv_ltdintro.setText(starCompanyDetail.get("szCompanyInstr"));//公司简介
		success_order_num.setText(starCompanyDetail.get("iOrderNum"));////公司成功订单数
		like_num.setText("");
		
		setRbState();
		
		ImageLoader.getInstance().displayImage(Define.URL_COMPANY_IMAGE + starCompanyDetail.get("szCompanyUrl"), compaIcon);
		
		if(isCollect){
//			btCollect.setClickable(false);
			btCollect.setTextColor(getResources().getColor(R.color.white));
			btCollect.setBackgroundResource(R.drawable.shape_btn_click_not);
			btCollect.setText("取消收藏");
		}
	}
	
	
	/**
	 * 为公司标识设置数据
	 */
	private void setRbState(){
		rb_AuthFlag.getBackground().setLevel(UniversalUtils.parseString2Int(starCompanyDetail.get("iAuthFlag")));
		rb_Filing.getBackground().setLevel(UniversalUtils.parseString2Int(starCompanyDetail.get("iFiling")));
		rb_OffLine.getBackground().setLevel(UniversalUtils.parseString2Int(starCompanyDetail.get("iOffLine")));
		rb_Nominate.getBackground().setLevel(UniversalUtils.parseString2Int(starCompanyDetail.get("iNominate")));
	}
	
	

	/**
	 * 控件的监听
	 */
	private void listenerCenter() {
		back.setOnClickListener(this);
		btShare.setOnClickListener(this);
		tv_shares.setOnClickListener(this);
		llAppoint.setOnClickListener(this);
		btCollect.setOnClickListener(this);
		btAppointOreder.setOnClickListener(this);

		// commentGood.setOnClickListener(this);
		// Commentmore.setOnClickListener(this);

	}
	
	
	
	/**
	 * 显示服务分类信息
	 * 
	 */
	private void setServiceClassifyData() {

		servie_ll.removeAllViews();
		for (int i = 0; i < serviceInfoList.size(); i++) {
			HsUserSeeCmpServiceInfo_Pro serviceInfo_Pro = serviceInfoList.get(i);
			View view = LayoutInflater.from(this).inflate(R.layout.ll_service_classify, null);
			TextView serviceType = (TextView) view.findViewById(R.id.service_type);
			TextView serviceRemark = (TextView) view.findViewById(R.id.service_remark);
			final ImageView servicePic = (ImageView) view.findViewById(R.id.service_pic);
			
			serviceType.setText(serviceInfo_Pro.getSzServiceName());
			serviceRemark.setText(serviceInfo_Pro.getSzRemark());
			
			LogUtils.w("iServiceID="+serviceInfo_Pro.getIServiceID()+
					"/iCmpSrvID =" +serviceInfo_Pro.getICmpSrvID()+
					"/szServiceName="+serviceInfo_Pro.getSzServiceName()+
					"/szServiceItem="+serviceInfo_Pro.getSzServiceItem()+
					"/szRemark="+serviceInfo_Pro.getSzRemark()+
					"/szPicUrl="+serviceInfo_Pro.getSzPicUrl());
			
			//一期将此处图片全部隐藏
			if(UniversalUtils.isStringEmpty(serviceInfo_Pro.getSzPicUrl())){
				servicePic.setVisibility(View.GONE);
			}else{
//				servicePic.setVisibility(View.GONE);
//				ImageLoader.getInstance().displayImage(serviceInfo_Pro.getSzPicUrl(), servicePic);
				ImageLoader.getInstance().displayImage(serviceInfo_Pro.getSzPicUrl(), servicePic, new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						servicePic.setVisibility(View.GONE);
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						servicePic.setVisibility(View.GONE);
					}
				});
			}

			servie_ll.addView(view);
		}
	}
	
	
	

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		AppointPopmenu popmenu = new AppointPopmenu(this, serviceInfoList,starCompanyDetail);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		
		switch (v.getId()) {
		case R.id.tv_reback:// 后退返回
			this.finish();
			break;
		case R.id.ll_appoint:// 进入预约下单页面
			
			if(!MyApplication.loginState){
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				finish();
				
			}else{
				lp.alpha = 0.7f;
				getWindow().setAttributes(lp);
				popmenu.showPopwindow(v);
			}
			
			break;
		case R.id.tv_shares:// 弹出分享按扭：
			popView = LayoutInflater.from(this).inflate(R.layout.pop_share,
					null);
			lp.alpha = 0.7f;
			cd = new ColorDrawable(Color.WHITE);
			SharePop pop = new SharePop(this, popView, cd);
			pop.showPopmenu(v, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT, Gravity.BOTTOM, 0, 0);
			getWindow().setAttributes(lp);
			break;
		case R.id.bt_collect:
			
			collectCompany();//收藏
			
			break;
		case R.id.bt_appoint:
			
			
			if(!MyApplication.loginState){
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				finish();
				
			}else{
				lp.alpha = 0.7f;
				getWindow().setAttributes(lp);
				popmenu.showPopwindow(v);
			}
			
			 break;
		}
	}
	
	
	/**
	 * 收藏明星公司
	 */
	private void collectCompany(){
		
		if (!MyApplication.loginState) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		} else {
			if (isCollect) {
				DataBaseService ds = new DataBaseService(this);
				String sql = DbOprationBuilder.deleteBuilderby("collectCompany", "iCompanyID", starCompanyDetail.get("iCompanyID"));
				ds.insert(sql);
				ToastUtils.show(this, "取消收藏成功", 1);

				// btCollect.setClickable(false);
				btCollect.setBackgroundResource(R.drawable.sel_button_collect);
				btCollect.setTextColor(getResources().getColor(R.color.stroke_gree));
				btCollect.setText("收藏");
				isCollect = false;
				
			} else {
				setDbStarCompanyData(starCompanyDetail);
				DataBaseService ds = new DataBaseService(this);
				String sql = DbOprationBuilder.insertCollectCompanyAllBuilder(MyApplication.userId, starCompanyDetail);
				ds.insert(sql);
				ToastUtils.show(this, "收藏成功", 1);

				// btCollect.setClickable(false);
				btCollect.setBackgroundResource(R.drawable.shape_btn_click_not);
				btCollect.setTextColor(getResources().getColor(R.color.white));
				btCollect.setText("取消收藏");
				isCollect = true;
			}
		}
	}
	
	
	
	/**
	 * 收藏时需要将该公司的数据保存到数据库
	 * @param starCompanyDetail
	 */
	private void setDbStarCompanyData(Map<String, String> starCompanyDetail){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iCompanyID", starCompanyDetail.get("iCompanyID")+"");
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		if(starCompanyDataList.size() != 0){
			return;
		}
		
		String insertSql = DbOprationBuilder.insertStarCompanyAllBuilder(MyApplication.userId, starCompanyDetail);
		ds.insert(insertSql);
	}
	
	
	
	/**
	 * 查询收藏收据--该公司是否已收藏
	 */
	private void queryCollectData(){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryCollectByCompanyIdAndUserId(Integer.parseInt(starCompanyDetail.get("iCompanyID")), MyApplication.userId);
		List<Map<String, String>> collectCompanyList = ds.query(sql);
		if(collectCompanyList.size() == 0){
			isCollect = false;
		}else{
			Map<String, String> collectCompany = collectCompanyList.get(0);
			if(MyApplication.userId == Integer.parseInt(collectCompany.get("userId"))){
				isCollect = true;
			}
		}
		
	}
	
	
	
	/**
	 * 公司服务信息请求
	 */
	private void loadCmpServiceInfoData() {
		seqCmpServiceInfo = MyApplication.SequenceNo++;
		HsNetCommon_Req common_Req = new MsgInncDef().new HsNetCommon_Req();
		common_Req.iSrcID = MyApplication.userId;
		common_Req.iSelectID = Integer.parseInt(starCompanyDetail.get("iCompanyID"));
		
		byte[] connData = HandleNetSendMsg.HandleHsUserSeeCmpServiceInfo_ReqToPro(common_Req,seqCmpServiceInfo);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("公司服务信息  请求--sequence="+seqCmpServiceInfo +"/"+ Arrays.toString(connData) + "=============");
		
		handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
		//加载进度条
		mCustomProgressDialog = new CustomProgressDialog(this);
		mCustomProgressDialog.show();
	}
	
	
	/**
	 * 广播接收者
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqCmpServiceInfo == iSequence) {
					processCmpServiceInfoData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理 公司服务信息响应 数据
	 * @param iSequence
	 */
	private void processCmpServiceInfoData(long recvTime){
		HsUserSeeCmpServiceInfoGet_Resp serviceInfoGet_Resp = (HsUserSeeCmpServiceInfoGet_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(serviceInfoGet_Resp == null){
			return;
		}
		
		handler.removeCallbacks(btnTimerRunnable);
		mCustomProgressDialog.dismiss();
		if(serviceInfoGet_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			serviceInfoList = serviceInfoGet_Resp.cmpServiceList;//公司服务信息
			
			Message message = new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);
			
			LogUtils.i("获取公司服务信息成功");
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(serviceInfoGet_Resp.eOperResult);
			LogUtils.i("获取公司服务信息失败--"+result);
		}
	}
	
	
	
	/**
	 * 用于对按钮不可点击进行计时
	 */
	Runnable btnTimerRunnable = new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(BTNTIMEROVER);
		}
	};
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
	
}
