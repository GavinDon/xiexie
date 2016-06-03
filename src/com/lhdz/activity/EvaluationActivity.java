package com.lhdz.activity;

/**
 * 用户评价
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsUserOrderValuate_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;

public class EvaluationActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	
	private TextView tv_comname;//公司名称
	private RatingBar company_rating;//公司星级
	private TextView tv_address;//公司地址
	
	private RatingBar evaluation_star;//评价星级
	private TextView evaluation_context;//评价内容
	Intent intent;
	MyApplication myApplication = null;
	private int orderId = 0;//订单id
	private String companyName = "";//公司名称
	
	List<Map<String, String>> companyInfo = null;//公司

	private int seqEvaluationNo = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_evaluation);
		myApplication = (MyApplication) getApplication();
		orderId = getIntent().getIntExtra("orderId", -1);
		companyName = getIntent().getStringExtra("companyName");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		queryCompanyData();
		
		initViews();
		backArrow();
		setViewData();
	}

	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("用户评价");
		findViewById(R.id.bt_release).setOnClickListener(this);
		
		tv_comname = (TextView) findViewById(R.id.tv_comname);//公司名称
		company_rating = (RatingBar) findViewById(R.id.company_rating);//公司星级
		tv_address = (TextView) findViewById(R.id.tv_address);//公司地址
		
		evaluation_context = (TextView) findViewById(R.id.evaluation_context);//评价内容
		evaluation_star = (RatingBar) findViewById(R.id.evaluation_star);//评价星级

	}
	
	
	/**
	 * 为view设置数据
	 */
	private void setViewData(){
		tv_comname.setText(companyName);//公司名称
		company_rating.setRating(Float.parseFloat(companyInfo.get(0).get("rating")));//公司星级
		tv_address.setText("地址："+companyInfo.get(0).get("addr"));//公司地址
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_release:

			if (evaluation_context.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "评价内容不能为空，请重新输入！", 0)
						.show();
				return;
			}
			loadData();
			break;

		default:
			break;
		}

	}

	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					EvaluationActivity.this.finish();

					break;
				}
			}
		});
	}
	
	
	
	/**
	 * 查询数据库 中 公司信息
	 */
	private void queryCompanyData(){
		String sql1 = DbOprationBuilder.queryBuilderby("*", "starcompanytable","name", companyName);
		CoreDbHelper core1 = new CoreDbHelper(this);
		companyInfo = core1.queryCoredata(sql1);
	}
	
	

//	/**
//	 * 评价请求
//	 */
//	public void loadData() {
//		HsUserOrderValuate_Req userOrderValuateReq = new MsgInncDef().new HsUserOrderValuate_Req();
//		userOrderValuateReq.uCompanyID =Integer.parseInt(companyInfo.get(0).get("buisnessid"));// 公司ID
//		userOrderValuateReq.uUserID = myApplication.userId;// 用户ID;
//		userOrderValuateReq.iOrderID = orderId;// 订单ID
//		userOrderValuateReq.iGiveStar = (int) evaluation_star.getRating();// 星级
//		userOrderValuateReq.szRemark = evaluation_context.getText().toString().trim();// 评价内容
//		userOrderValuateReq.szPicUrl = "小明";// 服务员人名
//		byte[] connData = HandleNetSendMsg
//				.HandleHsOrderValuate_ReqToPro(userOrderValuateReq);
//		SocketConn.pushtoList(connData);
//		LogUtils.i("connData评价的请求"+connData + "=============");
//		SocketConn.callbackRecData(new CallBackMsg() {
//
//			@Override
//			public void RecvMsgSuccess(byte[] msg) {
//				byte[] proBufHead = UniversalUtils.getNetMsgHead(msg);
//				HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
//				if(headMsg.uiMsgType != NetHouseMsgType.NETAPP_USER_USEREVALUATE_SET_RESP) return;
//				LogUtils.i("recv评价返回数据成功"+msg + "=============");
//				HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleNetReceiveMsg
//						.getParseMsgType(msg);
//				if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
//					int userID = commonResp.iUserID;
//					LogUtils.i("评价成功"+ "=============");
//					 EvaluationActivity.this.finish();
//				} else {
//					String result = UniversalUtils.judgeNetResult_Hs(commonResp.eOperResult);
//					LogUtils.i("评价失败"+ result+"=============");
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
	 * 评价请求
	 */
	public void loadData() {
		seqEvaluationNo = MyApplication.SequenceNo++;
		HsUserOrderValuate_Req userOrderValuateReq = new MsgInncDef().new HsUserOrderValuate_Req();
		userOrderValuateReq.uCompanyID =Integer.parseInt(companyInfo.get(0).get("buisnessid"));// 公司ID
		userOrderValuateReq.uUserID = myApplication.userId;// 用户ID;
		userOrderValuateReq.iOrderID = orderId;// 订单ID
		userOrderValuateReq.iGiveStar = (int) evaluation_star.getRating();// 星级
		userOrderValuateReq.szRemark = evaluation_context.getText().toString().trim();// 评价内容
		userOrderValuateReq.szPicUrl = "小明";// 服务员人名
		byte[] connData = HandleNetSendMsg
				.HandleHsOrderValuate_ReqToPro(userOrderValuateReq,seqEvaluationNo);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("connData评价的请求--sequence="+seqEvaluationNo +"/"+connData + "=============");
		
	}
	
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqEvaluationNo == iSequence) {
					processEvaluationNoData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理评价响应数据
	 * @param iSequence
	 */
	private void processEvaluationNoData(long recvTime){
		HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(commonResp == null){
			return;
		}
		LogUtils.i("recv评价返回数据成功");
		if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			int userID = commonResp.iUserID;
			LogUtils.i("评价成功"+ "=============");
			 EvaluationActivity.this.finish();
		} else {
			String result = UniversalUtils.judgeNetResult_Hs(commonResp.eOperResult);
			LogUtils.i("评价失败"+ result+"=============");
		}

	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
