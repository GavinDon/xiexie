package com.lhdz.activity;

/**
 * 用户投诉
 * @author 王哲
 * @date 2015-8-26
 */
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.publicMsg.MsgInncDef.HsUserAskAppeal_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ComplaintsActivity extends BaseActivity implements OnClickListener {

	private TextView title, randomcode;
	private EditText tscontext, yzcode;
	private Spinner sp;
	Intent intent;
	
	private ImageView compaIcon;//公司图标
	private TextView tv_comname;//公司名称
	private RatingBar complaint_rating;//星级
	private TextView tv_address;//公司地址
	private TextView complaint_no;//订单号
	private TextView complaint_state;//订单状态
	private TextView complaint_servicetype;//服务类型
	private TextView complaint_price;//订单金额
	
	private RadioButton rb_AuthFlag;
	private RadioButton rb_Filing;
	private RadioButton rb_OffLine;
	private RadioButton rb_Nominate;
	
	private CustomProgressDialog progressDialog;
	
	MyApplication myApplication = null;
	private Map<String, String> orderListInfo = null;
	private Map<String, String> companyDetailInfo = null;//公司信息
	
	private int iMathRandomCode ;
	
	private int seqComplaintsNo = -1;
	
	private final static int BTN_TIMER_OVER = 2;
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case BTN_TIMER_OVER:
				//设置按钮为可点击
				handler.removeCallbacks(btnTimerRunnable);
				//取消进度条的加载
				progressDialog.dismiss();
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
		setContentView(R.layout.activity_user_complaints);
		myApplication = (MyApplication) getApplication();
		orderListInfo = (Map<String, String>) getIntent().getSerializableExtra("orderListInfo");
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		queryCompanyData();
		initViews();
		backArrow();
		
		setViewData();
	}

	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("用户投诉");
		randomcode = (TextView) findViewById(R.id.yz_randomcode);
		randomcode.setOnClickListener(this);
		tscontext = (EditText) findViewById(R.id.complaintsContext);//投诉内容
		yzcode = (EditText) findViewById(R.id.yz_code);//验证码
		sp = (Spinner) findViewById(R.id.sp_style);
		findViewById(R.id.bt_addcomplaints).setOnClickListener(this);
		
		compaIcon = (ImageView) findViewById(R.id.compaIcon);//公司图标
		tv_comname = (TextView) findViewById(R.id.tv_comname);//公司名称
		complaint_rating = (RatingBar) findViewById(R.id.complaint_rating);//星级
		tv_address = (TextView) findViewById(R.id.tv_address);//公司地址
		complaint_no = (TextView) findViewById(R.id.complaint_no);//订单号
		complaint_state = (TextView) findViewById(R.id.complaint_state);//订单状态
		complaint_servicetype = (TextView) findViewById(R.id.complaint_servicetype);//服务类型
		complaint_price = (TextView) findViewById(R.id.complaint_price);//订单金额
		
		rb_AuthFlag = (RadioButton) findViewById(R.id.rb_AuthFlag);//身份认证
		rb_Filing = (RadioButton) findViewById(R.id.rb_Filing);//证件备案
		rb_OffLine = (RadioButton) findViewById(R.id.rb_OffLine);//线下验证
		rb_Nominate = (RadioButton) findViewById(R.id.rb_Nominate);//官方推荐
		
		setRandomCode();
	}
	
	
	/**
	 * 设置验证码
	 */
	private void setRandomCode(){
		iMathRandomCode = (int) ((Math.random() * 9 + 1) * 100000);//初始化验证码
		randomcode.setText(iMathRandomCode+"");
	}
	
	
	
	/**
	 * 为view设置数据
	 */
	private void setViewData(){
		
		if (companyDetailInfo != null) {
			complaint_rating.setRating(UniversalUtils.processRatingLevel(companyDetailInfo.get("iStarLevel")));// 星级
			tv_address.setText("地址：" + companyDetailInfo.get("szAddr"));// 公司地址
			ImageLoader.getInstance().displayImage(Define.URL_COMPANY_IMAGE + companyDetailInfo.get("szCompanyUrl"), compaIcon);
			setRbState();
		}
		tv_comname.setText(orderListInfo.get("szCompanyName"));//公司名称
		complaint_no.setText(orderListInfo.get("szOrderValue"));//订单号
		complaint_state.setText(orderListInfo.get("szOrderStateName"));//订单状态
		complaint_servicetype.setText(orderListInfo.get("szServiceTypeName"));//服务类型
		complaint_price.setText(UniversalUtils.getString2Float(orderListInfo.get("szOrderPrice"))+"元");//订单金额
	}
	
	
	/**
	 * 为公司标识设置数据
	 */
	private void setRbState(){
		rb_AuthFlag.getBackground().setLevel(UniversalUtils.parseString2Int(companyDetailInfo.get("iAuthFlag")));
		rb_Filing.getBackground().setLevel(UniversalUtils.parseString2Int(companyDetailInfo.get("iFiling")));
		rb_OffLine.getBackground().setLevel(UniversalUtils.parseString2Int(companyDetailInfo.get("iOffLine")));
		rb_Nominate.getBackground().setLevel(UniversalUtils.parseString2Int(companyDetailInfo.get("iNominate")));
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_addcomplaints:
			
			String complaintContext = tscontext.getText().toString();
			String complaintType = sp.getSelectedItem().toString();
			String strRandomCode = yzcode.getText().toString();
			
			if(UniversalUtils.isStringEmpty(complaintContext)){
				ToastUtils.show(this, "投诉内容不能为空", 0);
				return;
			}
			if(UniversalUtils.isStringEmpty(complaintType)){
				ToastUtils.show(this, "投诉类别不能为空", 0);
				return;
			}
			if(UniversalUtils.isStringEmpty(strRandomCode) || Integer.parseInt(strRandomCode) != iMathRandomCode){
				ToastUtils.show(this, "验证码不合法", 0);
				return;
			}
			
//			if (companyDetailInfo == null) {
//				// 若根据公司名称未查询到公司信息时，投诉失败
//				ToastUtils.show(this, "投诉失败", 0);
//				return;
//			}
			
			loadData();

			break;
		case R.id.yz_randomcode:
			setRandomCode();
			break;

		default:
			break;
		}

	}

	
	/**
	 * 后退按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					
					Intent resultData = new Intent();
					resultData.putExtra("isComplaintSuccess", false);
					setResult(Define.RESULTCODE_COMPLAINT, resultData);
					ComplaintsActivity.this.finish();
					break;
				}
			}
		});
	}
	
	
	/**
	 * 查询数据库 中 公司信息
	 */
	private void queryCompanyData(){
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany","szName", orderListInfo.get("szCompanyName"));
		DataBaseService ds = new DataBaseService(this);
		List<Map<String, String>> companyList = ds.query(sql);
		
		if(!UniversalUtils.isStringEmpty(companyList)){
			companyDetailInfo = companyList.get(0);
		}
	}
	
	

	
	
	/**
	 * 投诉请求
	 */
	public void loadData() {
		
		//设置加载进度条
		progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		
		seqComplaintsNo = MyApplication.SequenceNo++;
		HsUserAskAppeal_Req userAskAppealReq = new MsgInncDef().new HsUserAskAppeal_Req();
		userAskAppealReq.uUserID = myApplication.userId;// 用户ID;
		userAskAppealReq.iOrderID = Integer.parseInt(orderListInfo.get("uOrderID"));// 订单ID
//		if(companyDetailInfo != null){
//			userAskAppealReq.uCompanyID =Integer.parseInt(companyDetailInfo.get("iCompanyID"));// 公司ID
//		}
		userAskAppealReq.uCompanyID = 0;
		userAskAppealReq.szApperlClass = (String) sp.getSelectedItem();// 投诉类别
		userAskAppealReq.szReason = tscontext.getText().toString().trim();// 投诉内容
		byte[] connData = HandleNetSendMsg
				.HandleHsUserAskAppeal_ReqToPro(userAskAppealReq,seqComplaintsNo);
		HouseSocketConn.pushtoList(connData);
		
		
		LogUtils.i("connData投诉的请求--sequence="+seqComplaintsNo +"/"+connData + "=============");
		
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

				if (seqComplaintsNo == iSequence) {
					processComplaintsData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理用户投诉响应的数据
	 * @param recvTime 接收数据的时间
	 */
	private void processComplaintsData(long recvTime){
		HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(commonResp == null){
			return;
		}
		
		handler.removeCallbacks(btnTimerRunnable);
		progressDialog.dismiss();
		
		LogUtils.i("recv投诉返回数据成功");
		if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			int userID = commonResp.iUserID;
			LogUtils.i("投诉成功"+"=============");
			
			//将投诉结果返回上一个页面
			Intent resultData = new Intent();
			resultData.putExtra("isComplaintSuccess", true);
			setResult(Define.RESULTCODE_COMPLAINT, resultData);
			ComplaintsActivity.this.finish();
		} else {
			String result = UniversalUtils.judgeNetResult_Hs(commonResp.eOperResult);
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
			LogUtils.i("投诉失败"+result+"=============");
		}

	}
	
	/**
	 *  用于对按钮不可点击进行计时
	 */
	Runnable btnTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(BTN_TIMER_OVER);
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
