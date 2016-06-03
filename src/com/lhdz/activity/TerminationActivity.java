package com.lhdz.activity;

import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsUserQuitComany_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 退出明星公司
 * @author wangf
 *
 */
public class TerminationActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	Intent intent;
	
	private TextView tv_comname,tv_address;//公司名称，公司地址
	private RatingBar ter_rating;//公司星级
	private EditText quit_remark;//退出原因
	private Button btnTermination;//按钮
	private ImageView compaIcon;
	
	Map<String, String> starCompany = null;
	
	Handler handler ;
	private final static int msgQuitSuccess = 0;
	private final static int msgQuitError = 1;
	private final static int BTN_TIMER_OVER = 2;
	
	private int seqTermination = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_termination);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		starCompany = (Map<String, String>) getIntent().getSerializableExtra("starCompanyInfo");
		
		initviews();
		backArrow();

		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
				switch (msg.what) {
				case msgQuitSuccess:
					ToastUtils.show(TerminationActivity.this, "退出明星公司成功", Toast.LENGTH_SHORT);
					
					//若退出明星公司成功，则将数据库中的本条数据更新为 未加入状态
					DataBaseService ds = new DataBaseService(TerminationActivity.this);
					String updateStarCompany = DbOprationBuilder.updateStarCompanyBuider("0", "0", starCompany.get("iCompanyID"));
					ds.update(updateStarCompany);
					
//					Intent intent = new Intent(TerminationActivity.this,JoinStarActivity.class);
//					startActivity(intent);
					Intent data = new Intent();
					data.putExtra("iCompanyID", starCompany.get("iCompanyID"));
					setResult(Define.RESULTCODE_TERMINATION, data);
					TerminationActivity.this.finish();
					break;
				case msgQuitError:
					//设置按钮为可点击
					btnTermination.setClickable(true);
					btnTermination.setBackgroundResource(R.drawable.selector_oppointment2);
					ToastUtils.show(TerminationActivity.this, "退出明星公司失败", Toast.LENGTH_SHORT);
					break;
				case BTN_TIMER_OVER:
					//设置按钮为可点击
					btnTermination.setClickable(true);
					btnTermination.setBackgroundResource(R.drawable.selector_oppointment2);
					handler.removeCallbacks(btnTimerRunnable);
					break;
				default:
					break;
				}
				
				super.handleMessage(msg);
			}
		};
	}

	/**
	 * 初始化页面控件
	 */
	private void initviews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("解约");
		
		compaIcon = (ImageView) findViewById(R.id.compaIcon);
		tv_comname = (TextView) findViewById(R.id.tv_comname);//公司名称
		tv_address = (TextView) findViewById(R.id.tv_address);//公司地址
		ter_rating = (RatingBar) findViewById(R.id.ter_rating);//公司星级
		
		quit_remark = (EditText) findViewById(R.id.quit_remark);
		
		btnTermination = (Button) findViewById(R.id.bt_termination);//按钮
		btnTermination.setOnClickListener(this);
		
		setViewData();
	}

	
	/**
	 * 为界面view设置数据
	 */
	private void setViewData(){
		tv_comname.setText(starCompany.get("szName"));
		tv_address.setText(starCompany.get("szAddr"));
		ter_rating.setRating(UniversalUtils.processRatingLevel(starCompany.get("iStarLevel")));
		ImageLoader.getInstance().displayImage(Define.URL_COMPANY_IMAGE + starCompany.get("szCompanyUrl"), compaIcon);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_termination:
			
			//若未输入解约原因，提示用户输入
			if(TextUtils.isEmpty(quit_remark.getText().toString().trim())){
				ToastUtils.show(this, "请输入解约原因", Toast.LENGTH_SHORT);
				return;
			}
			
			//设置按钮为不可点击
			btnTermination.setClickable(false);
			btnTermination.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
			
			loadQuitCompanyData();
			
			break;

		default:
			break;
		}
	}

	
	/**
	 * 返回按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					TerminationActivity.this.finish();
					break;
				}
			}
		});
	}
	
	
	/**
	 * 用于对按钮不可点击进行计时
	 */
	Runnable btnTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(BTN_TIMER_OVER);
		}
	};
	

//	/**
//	 * 解约请求
//	 */
//	public void loadQuitCompanyData() {
//		HsUserQuitComany_Req quitComany_Req = new MsgInncDef().new HsUserQuitComany_Req();
//		quitComany_Req.uUserID = MyApplication.userId;// 用户ID;
//		quitComany_Req.uCompanyID = Integer.parseInt(starCompany.get("iCompanyID"));// 公司ID
//		quitComany_Req.szRemark = quit_remark.getText().toString().trim();
//		byte[] connData = HandleNetSendMsg
//				.HandleHsUserQuitCompany_ReqToPro(quitComany_Req);
//		SocketConn.pushtoList(connData);
//		LogUtils.i("connData解约的请求"+connData + "=============");
//		SocketConn.callbackRecData(new CallBackMsg() {
//
//			@Override
//			public void RecvMsgSuccess(byte[] msg) {
//				LogUtils.i("recv解约返回数据成功"+ msg + "=============");
//				
//				byte[] proBufHead = UniversalUtils.getNetMsgHead(msg);
//				HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
//				if(headMsg.uiMsgType != NetHouseMsgType.NETAPP_USER_QUITCOMPANY_RESP) return;
//				
//				HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleNetReceiveMsg
//						.getParseMsgType(msg);
//				//若服务端返回数据，将按钮不可点击的计时去掉
//				handler.removeCallbacks(btnTimerRunnable);
//				if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
//					int userID = commonResp.iUserID;
//					 
//					Message message = new Message();
//					message.what = msgQuitSuccess;
//					handler.sendMessage(message);
//					LogUtils.i("解约成功" + "=============");
//				} else {
//					Message message = new Message();
//					message.what = msgQuitError;
//					handler.sendMessage(message);
//					String result = UniversalUtils.judgeNetResult_Hs(commonResp.eOperResult);
//					LogUtils.i("解约失败"+result+ "=============");
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
	 * 解约请求
	 */
	public void loadQuitCompanyData() {
		seqTermination = MyApplication.SequenceNo++;
		HsUserQuitComany_Req quitComany_Req = new MsgInncDef().new HsUserQuitComany_Req();
		quitComany_Req.uUserID = MyApplication.userId;// 用户ID;
		quitComany_Req.uCompanyID = Integer.parseInt(starCompany
				.get("iCompanyID"));// 公司ID
		quitComany_Req.szRemark = quit_remark.getText().toString().trim();
		byte[] connData = HandleNetSendMsg.HandleHsUserQuitCompany_ReqToPro(
				quitComany_Req, seqTermination);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("解约的请求--sequence=" + seqTermination + "/" + connData
				+ "=============");

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

				if (seqTermination == iSequence) {
					processTerminationData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理解约请求的数据
	 * 
	 * @param recvTime
	 */
	private void processTerminationData(long recvTime) {
		HsNetCommon_Resp commonResp = (HsNetCommon_Resp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (commonResp == null) {
			return;
		}
		// 若服务端返回数据，将按钮不可点击的计时去掉
		handler.removeCallbacks(btnTimerRunnable);
		if (commonResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			int userID = commonResp.iUserID;

			Message message = new Message();
			message.what = msgQuitSuccess;
			handler.sendMessage(message);
			LogUtils.i("解约成功" + "=============");
		} else {
			Message message = new Message();
			message.what = msgQuitError;
			handler.sendMessage(message);
			String result = UniversalUtils
					.judgeNetResult_Hs(commonResp.eOperResult);
			LogUtils.i("解约失败" + result + "=============");
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
