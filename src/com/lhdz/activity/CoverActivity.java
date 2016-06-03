package com.lhdz.activity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ServerInfo_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.AuthNetCommonReq;
import com.lhdz.publicMsg.MsgInncDef.LoginReq;
import com.lhdz.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.lhdz.publicMsg.MsgReceiveDef.GetServerInfoResp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.DNSParsing;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.Config;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.NetWorkUtil;
import com.lhdz.util.UniversalUtils;


/**
 * 欢迎页面
 * @author wangf
 *
 */
public class CoverActivity extends BaseActivity {
	private ImageView imageView;
	private Animation myAnimation;
	private MyApplication app;
	private int userId;
	private DataBaseService ds;

	List<Map<String, String>> authInfoDataList;// 用户信息数据--从数据库来

	/**
	 * 发送请求的sequenceNo
	 */
	private int seqConn = -1;
	private int seqSer = -1;
	private int seqAutoLogin = -1;
	private int seqConnHouse = -1;
	
	private TextView tv_domain;
	private TextView tv_versionName;
	private String pwdMd5;
	
	private final static int MSG_SERVER_LIST_SUCCESS = 1;
	private final static int MSG_AUTO_LOGIN_SUCCESS = 2;
	
//	Handler handler = new Handler(){
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case MSG_SERVER_LIST_SUCCESS:
//				new Thread(runnable).start();
//				break;
//			case MSG_AUTO_LOGIN_SUCCESS:
//				app.houseSocketConn = new HouseSocketConn(PushData.getHouseIp(), PushData.getHousePort());
//				// 发送连接家政服务器请求
//				loadConnectHsDataBroad();
//				break;
//
//			default:
//				break;
//			}
//		}
//	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cover);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		
		ds = new DataBaseService(CoverActivity.this);
		ds.createTableLinkInfo();// 创建表
		app = (MyApplication) getApplication();
//		initStarCompanyData();
		// new GpsUtil(getApplicationContext());

		// // 从authInfo中取出userid
		// String sql = DbOprationBuilder.queryBuilder("userId", "authInfo");
		// List<Map<String, String>> list = ds.query(sql);
		// // 判断如果数据库中没有userid的情况下。默认userid为0;
		//
		// if (list.size() > 0) {
		// for (int i = 0; i < list.size(); i++) {
		// String data = list.get(i).get("userId");
		// app.userId = Integer.parseInt(data);
		// }
		// } else if (list.size() <= 0) {
		// userId = app.userId;
		// }
		initView();
		startAnimation();
		myAnimation.setAnimationListener(new listener());
		
		
//		socket = new SocketConn(ip, port);
//		if(MyApplication.authSocketConn != null){
//			MyApplication.authSocketConn.closeAuthSocket();
//		}
//		LogUtils.i("连接socket");
//		MyApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());

	}

	/**
	 * 封面域名和安卓版本号的设置
	 */
	private void initView() {
		try {
			String versionName = this.getPackageManager().getPackageInfo(
					"com.lhdz.activity", 0).versionName;// 获取版本名
			tv_domain = (TextView) findViewById(R.id.tv_domain);
			tv_versionName = (TextView) findViewById(R.id.tv_versionname);
			if(Config.isDebug){
				tv_versionName.setText("for android " + "Beat "+versionName);
			}else{
				tv_versionName.setText("for android " + versionName);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
//		CoreDbHelper core = new CoreDbHelper(this);
//		DataBaseService ds = new DataBaseService(this);
//		MyApplication mApplication = (MyApplication) this.getApplication();
		// 连接登录认证服务器
//		loadConnectAuthData();
		loadConnectAuthDataBroad();

	}
	
	
	//将存储的数据导入数据库中
	private void initStarCompanyData(){
		String sql = DbOprationBuilder.queryAllBuilder("starcompany");
		DataBaseService ds = new DataBaseService(this);
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		
		if(UniversalUtils.isStringEmpty(starCompanyDataList)){
			String querySql = DbOprationBuilder.queryAllBuilder("starcompanytable");
			CoreDbHelper core1 = new CoreDbHelper(this);
			List<Map<String, String>> storageStarCompanyList = core1.queryCoredata(querySql);
			
			for (int i = 0; i < storageStarCompanyList.size(); i++) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("insert into starcompany (userId,iCompanyID,iOrderNum,iValuNum,iStarLevel,iAuthFlag,iFiling,iOffLine,iNominate" +
						",szName,szAddr,szServiceInfo,szCreateTime,szCompanyUrl,szCompanyInstr,time) values (")
				
				.append(0+",") //userid
				.append(0+",") //公司id
				.append(0+",") //公司成功订单数
				.append(0+",") //雇主评价条数
				.append(0+",") //公司星级
				.append(0+",") //身份认证标示
				.append(0+",") 	//线下备案标示
				.append(0+",") //线下验证
				.append(0+",") //官方推荐
				.append("'"+storageStarCompanyList.get(i).get("name")+"'"+",") //公司名称
				.append("'"+0+"'"+",") //公司地址
				.append("'"+""+"'"+",") //服务信息（服务类型）
				.append("'"+""+"'"+",") //公司创建时间
				.append("'"+""+"'"+",") //公司URL
				.append("'"+""+"'"+",") //公司简介
				.append("'"+0+"'") //系统时间
				.append(")");
				
				ds.insert(sb.toString());
			}
		}
		
		
		
	}
	

	private void startAnimation() {
		myAnimation = AnimationUtils.loadAnimation(this, R.anim.enter);
		imageView = (ImageView) findViewById(R.id.iv_cover);
		// myAnimation.setInterpolator(new BounceInterpolator());//
		// 给动画用插入器设置图片弹跳
		imageView.setAnimation(myAnimation);
		overridePendingTransition(R.anim.enter,
				R.anim.alpha_scale_translate_rotate);// 两个页面之间跳转时动画效果，第一个参数是退出时效果，第二个是进入另一个页面效果
	}

	// 动画监听事件
	class listener implements AnimationListener {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Intent intent = new Intent(CoverActivity.this,
					WelcomeActivity.class);
			startActivity(intent);
			CoverActivity.this.finish();

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
	}

	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
	/**
	 * 加载 连接请求 -- 登录认证服务器
	 */
	public void loadConnectAuthDataBroad() {
		seqConn = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),seqConn);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("连接登录服务器请求数据--sequence="+seqConn +"/"+ Arrays.toString(connData) + "----------");
	}
	
	
	
	
	/**
	 * 广播接收数据
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqConn == iSequence) {
					processConnAuthData(recvTime);
				}
//				else if(seqSer == iSequence){
//					processServerListData(iSequence);
//				}
//				else if(seqAutoLogin == iSequence){
//					processAutoLoginData(iSequence);
//				}
//				else if(seqConnHouse == iSequence){
//					processConnHouseData(iSequence);
//				}

			}
		}
	};
	
	
	//处理连接登录认证服务器的数据
	private void processConnAuthData(long recvTime){
		NetConnectResp connectResp = (NetConnectResp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if(connectResp == null){
			return;
		}
		if (connectResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int time = connectResp.iSrvTime;
			int userId = connectResp.iUserid;

			LogUtils.i("连接登录认证服务器--" + "time = " + time + "/ userId = "+ userId);
//			loadDataServerListBroad(userId);
		}
	}
	
	
	
}
