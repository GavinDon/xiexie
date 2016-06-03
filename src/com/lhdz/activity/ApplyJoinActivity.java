package com.lhdz.activity;

import java.util.ArrayList;
import java.util.HashMap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsUserAddCompany_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 申请加入明星公司
 * @author wangf
 *
 */
public class ApplyJoinActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	private EditText name, phone, address, hoby;
	
	private TextView tv_comname,tv_address;//公司名称，公司地址
	private RatingBar apply_join_rating;//公司星级
	private ImageView iv_join_companyicon;
	private Button btnApplyJoin;
	
	private RadioButton rb_AuthFlag;
	private RadioButton rb_Filing;
	private RadioButton rb_OffLine;
	private RadioButton rb_Nominate;
	
	Map<String, String> starCompany = null;
	List<Map<String, String>> dbStarCompanyList = new ArrayList<Map<String, String>>();//数据库中 明星公司列表数据
	
	private final static int msgJoinSuccess = 0;
	private final static int msgJoinError = 1;
	private final static int BTN_TIMER_OVER = 2;
	
	private int seqApplyJoinNo = -1;
	
	private CustomProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_join);
		starCompany = (Map<String, String>) getIntent().getSerializableExtra("starCompanyInfo");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initViews();
		backArrow();
		queryStarCompanyData();
		
		
	}

	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("申请加入");
		
		name = (EditText) findViewById(R.id.et_myname);//我的姓名
		phone = (EditText) findViewById(R.id.et_apply_phone);//我的电话
		address = (EditText) findViewById(R.id.et_apply_address);//我的地址
		hoby = (EditText) findViewById(R.id.et_myhoby);//特长说明
		
		tv_comname = (TextView) findViewById(R.id.tv_comname);//公司名称
		tv_address = (TextView) findViewById(R.id.tv_address);//公司地址
		apply_join_rating = (RatingBar) findViewById(R.id.apply_join_rating);//公司星级
		iv_join_companyicon = (ImageView) findViewById(R.id.iv_join_companyicon);
		
		rb_AuthFlag = (RadioButton) findViewById(R.id.rb_AuthFlag);//身份认证
		rb_Filing = (RadioButton) findViewById(R.id.rb_Filing);//证件备案
		rb_OffLine = (RadioButton) findViewById(R.id.rb_OffLine);//线下验证
		rb_Nominate = (RadioButton) findViewById(R.id.rb_Nominate);//官方推荐
		
		btnApplyJoin = (Button) findViewById(R.id.applyjoin_confirm);
		btnApplyJoin.setOnClickListener(this);

		setViewData();//为view设置数据
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case msgJoinSuccess:
				ToastUtils.show(ApplyJoinActivity.this, "申请加入明星公司成功", Toast.LENGTH_SHORT);
				
				setDbStarCompanyData(starCompany);
				//若申请加入成功，则将数据库中的本条数据更新为 申请状态
				DataBaseService ds = new DataBaseService(ApplyJoinActivity.this);
				String updateStarCompany = DbOprationBuilder.updateStarCompanyBuider(NetHouseMsgType.JOINSTATE_APPLY+"", "申请", starCompany.get("iCompanyID"));
				ds.update(updateStarCompany);
				
				Intent data = new Intent();
				data.putExtra("iCompanyID", starCompany.get("iCompanyID"));
				setResult(Define.RESULTCODE_APPLYJOIN, data);
				finish();
				break;
			case msgJoinError:
				//设置按钮为可点击
				btnApplyJoin.setClickable(true);
				btnApplyJoin.setBackgroundResource(R.drawable.selector_oppointment);
				ToastUtils.show(ApplyJoinActivity.this, "申请加入明星公司失败", Toast.LENGTH_SHORT);
				break;
			case BTN_TIMER_OVER:
				//设置按钮为可点击
				btnApplyJoin.setClickable(true);
				btnApplyJoin.setBackgroundResource(R.drawable.selector_oppointment);
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
	
	
	
	/**
	 * 将明星公司数据写入数据库中
	 * @param starCompany 用户已经加入的明星公司数据
	 */
	private void setDbStarCompanyData(Map<String, String> starCompany){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iCompanyID", starCompany.get("iCompanyID")+"");
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		if(starCompanyDataList.size() != 0){
			return;
		}
		
		String insertSql = DbOprationBuilder.insertStarCompanyAllBuilder(MyApplication.userId, starCompany);
		ds.insert(insertSql);
	}
	
	
	/**
	 * 为界面view设置数据
	 */
	private void setViewData(){
		tv_comname.setText(starCompany.get("szName"));
		tv_address.setText(starCompany.get("szAddr"));
		apply_join_rating.setRating(UniversalUtils.processRatingLevel(starCompany.get("iStarLevel")));
		ImageLoader.getInstance().displayImage(Define.URL_COMPANY_IMAGE + starCompany.get("szCompanyUrl"), iv_join_companyicon);
		setRbState();
	}
	
	
	/**
	 * 为公司标识设置数据
	 */
	private void setRbState(){
		rb_AuthFlag.getBackground().setLevel(UniversalUtils.parseString2Int(starCompany.get("iAuthFlag")));
		rb_Filing.getBackground().setLevel(UniversalUtils.parseString2Int(starCompany.get("iFiling")));
		rb_OffLine.getBackground().setLevel(UniversalUtils.parseString2Int(starCompany.get("iOffLine")));
		rb_Nominate.getBackground().setLevel(UniversalUtils.parseString2Int(starCompany.get("iNominate")));
	}
	
	
	/**
	 * 加载明星公司数据
	 */
	private void queryStarCompanyData() {
		dbStarCompanyList.clear();
		// 查询数据库
		String sql = DbOprationBuilder.queryAllBuilder("starcompany");
		DataBaseService ds = new DataBaseService(this);
		dbStarCompanyList = ds.query(sql);
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.applyjoin_confirm:
			
			if(UniversalUtils.isStringEmpty(name.getText().toString().trim())){
				ToastUtils.show(this, "姓名不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(phone.getText().toString().trim())){
				ToastUtils.show(this, "手机号码不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(address.getText().toString().trim())){
				ToastUtils.show(this, "地址不能为空", Toast.LENGTH_SHORT);
				return;
			}
			
//			if (name.getText().toString().equals("")
//					|| phone.getText().toString().equals("")
//					|| address.getText().toString().equals("")) {
//				ToastUtils.show(ApplyJoinActivity.this, "用户信息不能为空，请重新输入!", 0)
//						.show();
//				return;
//			}
			
			//设置按钮为不可点击
			btnApplyJoin.setClickable(false);
			btnApplyJoin.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
			//设置加载进度条
			progressDialog = new CustomProgressDialog(this);
			progressDialog.show();
			// 发送申请加入消息
			loadData();
			break;

		default:
			break;
		}
	}

	
	/**
	 * 页面返回按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					ApplyJoinActivity.this.finish();

					break;
				}
			}
		});
	}
	
	
	// 用于对按钮不可点击进行计时
		Runnable btnTimerRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(BTN_TIMER_OVER);
			}
		};
	

//	/**
//	 * 申请加入明星公司请求
//	 */
//	public void loadData() {
//		HsUserAddCompany_Req addCompanyReq = new MsgInncDef().new HsUserAddCompany_Req();
//		addCompanyReq.uUserID = MyApplication.userId;// 用户ID;
//		addCompanyReq.uCompanyID = Integer.parseInt(starCompany.get("iCompanyID"));// 公司ID
//		addCompanyReq.iSrvType = 0;// 服务类型
//		addCompanyReq.szName = name.getText().toString().trim();// 用户名
//		addCompanyReq.szLinkTel = phone.getText().toString().trim();// 联系电话
//		addCompanyReq.szCurAddr = address.getText().toString().trim();// 现居地址
//		addCompanyReq.szRemark = hoby.getText().toString().trim();// 个人介绍
//		byte[] connData = HandleNetSendMsg
//				.HandleHsUserAddCompany_ReqToPro(addCompanyReq);
//		SocketConn.pushtoList(connData);
//		LogUtils.i("connData申请加入明星公司的请求"+connData + "=============");
//		SocketConn.callbackRecData(new CallBackMsg() {
//
//			@Override
//			public void RecvMsgSuccess(byte[] msg) {
//				LogUtils.i("recv申请加入明星公司返回数据成功"+ msg + "=============");
//				
//				byte[] proBufHead = UniversalUtils.getNetMsgHead(msg);
//				HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
//				if(headMsg.uiMsgType != NetHouseMsgType.NETAPP_USERADDCOMPANY_RESP) return;
//				
//				HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleNetReceiveMsg
//						.getParseMsgType(msg);
//				handler.removeCallbacks(btnTimerRunnable);
//				progressDialog.dismiss();
//				if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
//					int userID = commonResp.iUserID;
//					
//					Message message =new Message();
//					message.what = msgJoinSuccess;
//					handler.sendMessage(message);
//					LogUtils.i("申请加入成功"+ "=============");
//					
//				} else {
//					Message message =new Message();
//					message.what = msgJoinError;
//					handler.sendMessage(message);
//					String result = UniversalUtils.judgeNetResult_Hs(commonResp.eOperResult);
//					LogUtils.i("申请加入失败"+ result+"=============");
//				}
//
//			}
//
//			@Override
//			public void RecvMsgLose() {
//
//			}
//		});
//	}
	
		
		
		/**
		 * 申请加入明星公司请求
		 */
		public void loadData() {
			seqApplyJoinNo = MyApplication.SequenceNo++;
			HsUserAddCompany_Req addCompanyReq = new MsgInncDef().new HsUserAddCompany_Req();
			addCompanyReq.uUserID = MyApplication.userId;// 用户ID;
			addCompanyReq.uCompanyID = Integer.parseInt(starCompany.get("iCompanyID"));// 公司ID
			addCompanyReq.iSrvType = 0;// 服务类型
			addCompanyReq.szName = name.getText().toString().trim();// 用户名
			addCompanyReq.szLinkTel = phone.getText().toString().trim();// 联系电话
			addCompanyReq.szCurAddr = address.getText().toString().trim();// 现居地址
			addCompanyReq.szRemark = hoby.getText().toString().trim();// 个人介绍
			byte[] connData = HandleNetSendMsg
					.HandleHsUserAddCompany_ReqToPro(addCompanyReq,seqApplyJoinNo);
			HouseSocketConn.pushtoList(connData);
			LogUtils.i("connData申请加入明星公司的请求--sequence="+seqApplyJoinNo +"/"+connData + "=============");
			
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

				if (seqApplyJoinNo == iSequence) {
					processApplyJoinData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理申请加入响应的数据
	 * @param iSequence
	 */
	private void processApplyJoinData(long recvTime){
		HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(commonResp == null){
			return;
		}
		handler.removeCallbacks(btnTimerRunnable);
		progressDialog.dismiss();
		if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			int userID = commonResp.iUserID;
			
			Message message =new Message();
			message.what = msgJoinSuccess;
			handler.sendMessage(message);
			LogUtils.i("申请加入成功"+ "=============");
			
		} else {
			Message message =new Message();
			message.what = msgJoinError;
			handler.sendMessage(message);
			String result = UniversalUtils.judgeNetResult_Hs(commonResp.eOperResult);
			LogUtils.i("申请加入失败"+ result+"=============");
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
