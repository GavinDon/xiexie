package com.lhdz.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.adapter.UserInfoAdapter;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.AUTHUserSelfMoveInfoModReq;
import com.lhdz.publicMsg.MsgReceiveDef.AuthNetCommonResp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.CameraUtil;
import com.lhdz.util.ChangeUserIconUtil;
import com.lhdz.util.Define;
import com.lhdz.util.GetCityDataUtil;
import com.lhdz.util.HttpUploadImage;
import com.lhdz.util.ImageUtils;
import com.lhdz.util.IntentUtil;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 用户信息页面
 * @author wangf
 *
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private TextView title;
	private ListView mListview;
	private List<Map<String, Object>> userViewData = new ArrayList<Map<String, Object>>();
	private int icon[];
	private LinearLayout lay;
	private TextView serviceAddr;
	private List<Map<String, String>> serviceAddrData;// 服务地址数据
	private final static int REQ_CODE = 110;
	private UserInfoAdapter mAdapter;
	private DataBaseService ds = new DataBaseService(this);
	private List<Map<String, String>> list = null;
	private RelativeLayout rl_chageicon;// 修改头像的大布局;
	private CircleImageView iv_userIcon;

	private int seqConnAuth = -1;// 用于连接登录认证服务器的sequence
	private int seqModifyUserInfo = -1;// 用于修改用户信息的sequence

	private MyApplication myApplication;
	
	private String userIconPath = "";
	private String userIconFileName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		myApplication = (MyApplication) getApplication();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);

//		// 判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
//		if (myApplication.authSocketConn == null|| myApplication.authSocketConn.isClose()) {
//			myApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());
//			try {
//				Thread.sleep(100);
//				loadConnectAuthDataBroad();// 连接登录认证服务器
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}

		initViews();
		backArrow();
		mListview.setOnItemClickListener(this);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		rl_chageicon = (RelativeLayout) findViewById(R.id.rl_chageicon);
		rl_chageicon.setOnClickListener(this);
		iv_userIcon = (CircleImageView) findViewById(R.id.userinfo_iv);
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("个人信息");
		mListview = (ListView) findViewById(R.id.userinfo_lv);
		lay = (LinearLayout) findViewById(R.id.lay_address);
		lay.setOnClickListener(this);
		serviceAddr = (TextView) findViewById(R.id.tv_server_address_item);// 服务地址
		flateData();
		queryServiceAdd();
	}

	/**
	 * 查询数据库中的服务地址
	 */
	private void queryServiceAdd() {
		// DataBaseService ds = new DataBaseService(this);
//		String sql = DbOprationBuilder.queryAllBuilder("userAddr");
		//查询数据库中未删除的地址信息并显示--其中：isDelete状态标识为 0：未删除，1：已删除
		String sql = DbOprationBuilder.queryBuilderby("*", "userAddr", "isDelete", "0");
		serviceAddrData = ds.query(sql);
		serviceAddr.setText("");
		if (serviceAddrData.size() <= 0) {
			return;
		}
		int dbUserID = Integer.parseInt(serviceAddrData.get(0).get("userId"));
		if (dbUserID != MyApplication.userId) {
			return;
		}
		for (int i = 0; i < serviceAddrData.size(); i++) {
			if (serviceAddrData.get(i).get("selecState").equals("1")) {
				serviceAddr.setText(serviceAddrData.get(i).get("longAddr"));
			}
		}

	}

	/**
	 * 查询用户信息
	 */
	public void queryUserInfo() {
		String sql = DbOprationBuilder.queryAllBuilder("authInfo");
		list = ds.query(sql);
	}

	
	/**
	 * 设置界面数据
	 */
	private void flateData() {
		queryUserInfo();
		// DataBaseService ds = new DataBaseService(this);

		GetCityDataUtil cityDataUtil = new GetCityDataUtil(this);
		// if(UniversalUtils.isStringEmpty(list.get(0).get("area"))){
		//
		// }
		String strArea = cityDataUtil.areaIdToAddr(Integer.parseInt(list.get(0)
				.get("area")));

		String[] ModelData = new String[] { "帐号", "昵称", "性别", "地区", "个性签名", };
		// String[] content = new String[] { "849979276", "神呐", "男", "陕西西安",
		// "人、要懂得感恩。" };
		String[] content = new String[] { list.get(0).get("accout"),
				list.get(0).get("nickName"), list.get(0).get("sex"), strArea,
				list.get(0).get("autoGraph") };
		for (int i = 0; i < ModelData.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item", ModelData[i]);
			map.put("info", content[i]);
			userViewData.add(map);

		}
		mAdapter = new UserInfoAdapter(this);
		mAdapter.setData(userViewData);
		mListview.setAdapter(mAdapter);
//		list.get(0).get("headIcon")
		
		setUserIconView();
		
	}
	
	
	
	/**
	 * 加载用户图像
	 */
	private void setUserIconView(){
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.pic_me_gray)
			.showImageOnFail(R.drawable.pic_me_gray)
			.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
			.build();
		String IconPath = Define.URL_DOWNLOAD_USER_IMAGE + MyApplication.userId +".jpg";
		ImageLoader.getInstance().displayImage(IconPath, iv_userIcon, options);
	}
	
	
	

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.lay_address:
			intent = new Intent(this, AddressActivity.class);
			startActivityForResult(intent, REQ_CODE);
			break;
		case R.id.rl_chageicon:
			//修改个人图像
			
			// 打开相册
			CameraUtil c = new CameraUtil(this);
			c.openPhotos(this, 101);

			
//			WindowManager.LayoutParams lp = getWindow().getAttributes();
//			lp.alpha = 0.6f;
//			getWindow().setAttributes(lp);
//			new ChangeUserIconUtil(this).showPopwindow(v);
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
					UserInfoActivity.this.finish();

					break;
				}
			}
		});
	}
	
	
	

	/**
	 * 上传图片至服务器
	 */
	Runnable uploadImageRunnable = new Runnable() {

		@Override
		public void run() {
			LogUtils.i("uploadFile Url = " + Define.URL_UPLOAD_USER_IMAGE);
			LogUtils.i("uploadFile Name = " + userIconPath);
			String result = HttpUploadImage.uploadFile(Define.URL_UPLOAD_USER_IMAGE, userIconPath);
			if (!TextUtils.isEmpty(result)) {
//				loadModifyUserInfo(4, Define.URL_DOWNLOAD_USER_IMAGE + userIconFileName);
				ImageLoader.getInstance().clearMemoryCache();
			} else {
//				handler.sendEmptyMessage(UPLOAD_IMAGE_FAIL);// 上传图片失败
			}

		}

	};

	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		getResult(requestCode, resultCode, data);// 改变头像的一系列操作
		
		userViewData.clear();
		
		if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
			// 获取相册中图片的位置
//			String picturePath = CameraUtil.getPhotoPathByLocalUri(this, data);
			String picturePath;
			// 获取相册中图片的位置
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				picturePath = CameraUtil.getImageAbsolutePath(this,
						data.getData());
			} else {
				picturePath = CameraUtil.getPhotoPathByLocalUri(this, data);
			}

			// 压缩并展示图片
			ImageUtils.showPhoto(iv_userIcon, picturePath);
			// 压缩图片
			Bitmap bmp = ImageUtils.resize(picturePath,ImageUtils.BASE_SIZE_160);
			
			if(bmp == null){
				ToastUtils.show(this, "图片上传失败，请检查图片是否完整", 1);
			}else{
				userIconFileName = String.valueOf(MyApplication.userId)+ ".jpg";
				String dstPath = UniversalUtils.getSDPath()+ Define.PATH_USER_IMAGE_CACHE;
				// 保存压缩后的文件
				ImageUtils.saveBitmap(bmp, dstPath, userIconFileName);
				userIconPath = dstPath + userIconFileName;
				new Thread(uploadImageRunnable).start();// 启动线程上传图片
			}
		}
		
		
		switch (resultCode) {
		case Define.RESULTCODE_SEX_MAN:// 性别 男
			String sex = data.getExtras().getString("edit");

			if (UniversalUtils.isStringEmpty(sex)
					|| !sex.equals(list.get(0).get("sex"))) {
				try {
					// 判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
					if (myApplication.authSocketConn != null) {
						myApplication.authSocketConn.closeAuthSocket();
						Thread.sleep(5);
					}
					myApplication.authSocketConn = new AuthSocketConn(
							PushData.getAuthIp(), PushData.getAuthPort());
					Thread.sleep(100);
					loadConnectAuthDataBroad();// 连接登录认证服务器
					Thread.sleep(100);
					String sql = DbOprationBuilder.updateBuider("authInfo","sex", sex);
					ds.update(sql);
					loadModifyUserInfo(5, "0");// 5: 性别, 男：0
					flateData();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			break;
		case Define.RESULTCODE_SEX_WOMAN:// 性别 女
			String girl = data.getExtras().getString("edit");

			if (UniversalUtils.isStringEmpty(girl)
					|| !girl.equals(list.get(0).get("sex"))) {
				try {
					// 判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
					if (myApplication.authSocketConn != null) {
						myApplication.authSocketConn.closeAuthSocket();
						Thread.sleep(5);
					}
					myApplication.authSocketConn = new AuthSocketConn(
							PushData.getAuthIp(), PushData.getAuthPort());
					Thread.sleep(100);
					loadConnectAuthDataBroad();// 连接登录认证服务器
					Thread.sleep(100);

					String sqlstate = DbOprationBuilder.updateBuider("authInfo", "sex", girl);
					ds.update(sqlstate);
					loadModifyUserInfo(5, "1");// 5: 性别, 女：1
					flateData();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;
		case Define.RESULTCODE_NICK:// 昵称
			String saveNickName = data.getStringExtra("saveNick");

			if (UniversalUtils.isStringEmpty(saveNickName)
					|| !saveNickName.equals(list.get(0).get("nickName"))) {

				try {
					// 判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
					if (myApplication.authSocketConn != null) {
						myApplication.authSocketConn.closeAuthSocket();
						Thread.sleep(5);
					}
					myApplication.authSocketConn = new AuthSocketConn(
							PushData.getAuthIp(), PushData.getAuthPort());
					Thread.sleep(100);
					loadConnectAuthDataBroad();// 连接登录认证服务器
					Thread.sleep(100);

					String nickSql = DbOprationBuilder.updateBuider("authInfo","nickName", saveNickName);
					ds.update(nickSql);
					loadModifyUserInfo(1, saveNickName);
					flateData();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;
		case Define.RESULTCODE_AREA:// 区域
			// String saveArea=data.getStringExtra("saveArea");
			String areaId = data.getStringExtra("saveArea");

			if (UniversalUtils.isStringEmpty(areaId)
					|| !areaId.equals(list.get(0).get("area"))) {

				try {
					// 判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
					if (myApplication.authSocketConn != null) {
						myApplication.authSocketConn.closeAuthSocket();
						Thread.sleep(5);
					}
					myApplication.authSocketConn = new AuthSocketConn(
							PushData.getAuthIp(), PushData.getAuthPort());
					Thread.sleep(100);
					loadConnectAuthDataBroad();// 连接登录认证服务器
					Thread.sleep(100);

					String areaSql = DbOprationBuilder.updateBuider("authInfo","area", areaId);
					ds.update(areaSql);
					loadModifyUserInfo(6, areaId);
					flateData();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;

		case Define.RESULTCODE_GRAPH:// 个性签名
			String saveGraph = data.getStringExtra("saveGraph");

			if (UniversalUtils.isStringEmpty(saveGraph)
					|| !saveGraph.equals(list.get(0).get("autoGraph"))) {
				try {
					// 判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
					if (myApplication.authSocketConn != null) {
						myApplication.authSocketConn.closeAuthSocket();
						Thread.sleep(5);
					}
					myApplication.authSocketConn = new AuthSocketConn(
							PushData.getAuthIp(), PushData.getAuthPort());
					Thread.sleep(100);
					loadConnectAuthDataBroad();// 连接登录认证服务器
					Thread.sleep(100);

					String graphSql = DbOprationBuilder.updateBuider("authInfo", "autoGraph", saveGraph);
					ds.update(graphSql);
					loadModifyUserInfo(2, saveGraph);
					flateData();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;
		case Define.RESULTCODE_SERVERADDR:// 设置默认地址
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) data
					.getSerializableExtra("serviceAdd");
			if (map == null) {
				serviceAddr.setText("");
			}else{
				String addr = map.get("longAddr");
				serviceAddr.setText(addr);
			}
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, UserInfoEdit.class);
		Bundle bundle = new Bundle();
		switch (position) {
		case 0:
			bundle.putInt("edit", 0);
			intent.putExtras(bundle);
			// startActivityForResult(intent, 0);
			break;
		case 1:
			// 修改昵称
			bundle.putInt("edit", 1);
			bundle.putString("editnick", list.get(0).get("nickName"));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		case 2:
			// 修改性别
			bundle.putInt("edit", 2);
			bundle.putString("editsex", list.get(0).get("sex"));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		case 3:
			// 修改地区
			bundle.putInt("edit", 3);
			bundle.putInt("deitAreaId",
					Integer.parseInt(list.get(0).get("area")));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		case 4:
			// 修改签名
			bundle.putInt("edit", 4);
			bundle.putString("editgraph", list.get(0).get("autoGraph"));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
			break;
		}

	}

	/**
	 * 加载 连接请求 -- 登录认证服务器
	 */
	public void loadConnectAuthDataBroad() {
		seqConnAuth = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleConnectToPro(
				new MsgInncDef().new NetConnectReq(), seqConnAuth);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("连接登录服务器请求数据--sequence=" + seqConnAuth + "/"
				+ Arrays.toString(connData) + "----------");
	}

	/**
	 * 修改个人信息的请求
	 * 
	 * @param iModType
	 *            修改类别：:1：昵称, 2:个性签名, 3：手机号码,4：头像,5: 性别, 6:地区
	 * @param szModInfo
	 */
	private void loadModifyUserInfo(int iModType, String szModInfo) {
		seqModifyUserInfo = MyApplication.SequenceNo++;
		AUTHUserSelfMoveInfoModReq infoModReq = new MsgInncDef().new AUTHUserSelfMoveInfoModReq();
		infoModReq.iuserid = myApplication.userId;
		infoModReq.iModType = iModType;
		infoModReq.szModInfo = szModInfo;

		byte[] connData = HandleNetSendMsg.HandleUserInfoModReqToPro(
				infoModReq, seqModifyUserInfo);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("修改用户信息的请求数据--sequence=" + seqModifyUserInfo + "/"
				+ Arrays.toString(connData) + "----------");
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

				if (seqConnAuth == iSequence) {
					processConnAuthData(recvTime);
				} else if (seqModifyUserInfo == iSequence) {
					processModifyUserInfoData(recvTime);
				}
			}
		}
	};

	/**
	 *  处理连接登录认证服务器的数据
	 * @param recvTime
	 */
	private void processConnAuthData(long recvTime) {
		NetConnectResp connectResp = (NetConnectResp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (connectResp == null) {
			return;
		}
		if (connectResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int time = connectResp.iSrvTime;
			int userId = connectResp.iUserid;

			LogUtils.i("连接登录认证服务器--" + "time = " + time + "/ userId = "
					+ userId);
		}
	}

	/**
	 * 处理修改用户信息的响应数据
	 * 
	 * @param recvTime
	 */
	private void processModifyUserInfoData(long recvTime) {
		AuthNetCommonResp commonResp = (AuthNetCommonResp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (commonResp == null) {
			return;
		}
		myApplication.authSocketConn.closeAuthSocket();//关闭登录认证socket
		if (commonResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			LogUtils.i("修改用户信息成功");
		} else {
			String result = UniversalUtils
					.judgeNetResult_Auth(commonResp.eResult);
			LogUtils.i(result);
		}

	}

	private Uri imgUri;

	public void getResult(int requestCode, int resultCode, Intent data) {
		// if (resultCode != RESULT_OK) {
		// return;
		// }
		switch (requestCode) {
		case Define.PICK_FROM_ALBUM:
			imgUri = data.getData();
			crop(imgUri);
			break;
		case Define.PICK_FROM_CAMERA:
			// File file = new File(Environment.getExternalStorageDirectory(),
			// Define.PHOTO_FILE_NAME);
			// imgUri = data.getData();
			imgUri = ChangeUserIconUtil.imgUri;
			// if (imgUri == null) {
			// imgUri = Uri.fromFile(new File(file, "avatar_"
			// + String.valueOf(System.currentTimeMillis()) + ".png"));
			// }

			crop(imgUri);
			break;
		case Define.CROP_FROM_CAMERA:
			if (data != null) {
				setCropImg(data);
			}
			break;

		}
	}

	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = IntentUtil.openCrop();
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, Define.CROP_FROM_CAMERA);
	}

	/**
	 * set the bitmap
	 * 
	 * @param picdata
	 */
	// 必须放在根路径下。不能放在具体文件夹下
	String cropImgPath = Environment.getExternalStorageDirectory()
			+ "/userIcon_" + System.currentTimeMillis() + ".png";

	private void setCropImg(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (null != bundle) {
			Bitmap mBitmap = bundle.getParcelable("data");
			iv_userIcon.setImageBitmap(mBitmap);// 给控件上显示图片;
			saveBitmap(cropImgPath, mBitmap);
			// f.deleteOnExit();
		}
	}

	public void saveBitmap(String fileName, Bitmap mBitmap) {
		File f = new File(fileName);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fOut.close();
				Toast.makeText(this, "save success", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		// 关闭登录认证服务器的socket
		unregisterReceiver(mReceiver);
		myApplication.authSocketConn.closeAuthSocket();
		super.onDestroy();
	}

}
