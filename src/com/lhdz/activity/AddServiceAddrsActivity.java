package com.lhdz.activity;



import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.cityDao.CityModel;
import com.lhdz.cityDao.DBhelper;
import com.lhdz.cityDao.DistrictModel;
import com.lhdz.cityDao.ProvinceModel;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsUserServiceAddrInfo_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.GetCityDataUtil;
import com.lhdz.util.GetScreenInchUtil;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.CityPop;
import com.lhdz.wediget.CityPop.CityCallBack;
import com.lhdz.wheel.OnWheelChangedListener;
import com.lhdz.wheel.WheelView;
import com.lhdz.wheel.adapters.ArrayWheelAdapter;

/**
 * 添加新地址
 * @author wangf
 *
 */
public class AddServiceAddrsActivity extends BaseActivity implements OnClickListener,OnWheelChangedListener{
	
	private TextView title;//标题
	private TextView back;//返回
	
	private EditText etUserName;//姓名
	private EditText etUserPhone;//手机号
	private EditText etUserAddr;//详细地址
	private LinearLayout area_ll;//所在区域  
	private TextView area_txt;//所在区域 
	
	private Button btCitySure;//保存地址按钮
	
	private View popView;
	private ColorDrawable cd;
	
	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;
	
	private DBhelper dBhelper;
	protected String[] mProvinceDatas;//所有省
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();//key - 省 value - 市
	protected Map<String, String[]> mCitisCodeDatasMap = new HashMap<String, String[]>();//key - 省 value - 市的code
	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();// key - 市 values - 区
	protected Map<String, String[]> mZipcodeDatasMap = new HashMap<String, String[]>(); // key - 区 values - 邮编
	protected String mCurrentProviceName;//当前省的名称
	protected String mCurrentCityName;//当前市的名称
	protected String mCurrentCityCode;//当前市的code
	protected String mCurrentDistrictName ="";//当前区的名称
	protected String mCurrentZipCode ="";//当前区的邮政编码
	
	private String selectAreaId = "";//用于请求数据时的areaid
	
	private static boolean isModifyAddr = false;//用于标识修改服务地址 
	Map<String, String> modifyAddr = null;//用于修改服务地址
	MyApplication myApplication = null;
	
	List<ProvinceModel> provinceList = null;//所有省份数据
	
	private final static int LOAD_DATA_FAIL = 0;
	private final static int LOAD_DATA_SUCCESS = 1;
	private final static int BTN_TIMER_OVER = 3;
	
	private CustomProgressDialog progressDialog = null;
	
	private int seqAddServiceNo = -1;
	
	private GetCityDataUtil cityDataUtil;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_serviceaddrs);
		
		myApplication = (MyApplication) getApplication();
		modifyAddr = (Map<String, String>) getIntent().getSerializableExtra("modifyAddr");
		isModifyAddr = getIntent().getBooleanExtra("isModifyAddr", false);//用于标识是否为修改地址
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initProvinceDatasXml();//初始化城市数据
		initViews();
		
	}

	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case LOAD_DATA_SUCCESS:
				//加载数据成功
				if(isModifyAddr){
					ToastUtils.show(AddServiceAddrsActivity.this, "服务地址修改成功", 0);
				}else {
					ToastUtils.show(AddServiceAddrsActivity.this, "服务地址添加成功", 0);
				}
				finish();
				break;
			case LOAD_DATA_FAIL:
				//加载数据失败
				//设置按钮为可点击
				btCitySure.setClickable(true);
				btCitySure.setBackgroundResource(R.drawable.selector_oppointment);
				
				if(isModifyAddr){
					ToastUtils.show(AddServiceAddrsActivity.this, "服务地址修改失败", 0);
				}else{
					ToastUtils.show(AddServiceAddrsActivity.this, "服务地址添加失败", 0);
				}
				
				break;
			case BTN_TIMER_OVER://用于长时间网络无返回
				//设置按钮为可点击
				btCitySure.setClickable(true);
				btCitySure.setBackgroundResource(R.drawable.selector_oppointment);
				handler.removeCallbacks(btnTimerRunnable);
				//取消进度条
				progressDialog.dismiss();
				break;
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	
	
	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		/**
		 * 标题
		 */
		title = (TextView) findViewById(R.id.tv_title);
		if(isModifyAddr){
			title.setText("修改地址");
		}else{
			title.setText("添加新地址");
		}
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(this);
		
		etUserName = (EditText) findViewById(R.id.et_username);
		etUserPhone = (EditText) findViewById(R.id.et_userphone);
		etUserAddr = (EditText) findViewById(R.id.et_useraddr);
		
		btCitySure = (Button) findViewById(R.id.bt_city_sure);
		btCitySure.setOnClickListener(this);
		
		area_txt = (TextView) findViewById(R.id.area_txt);
		area_ll = (LinearLayout) findViewById(R.id.area_ll);
		area_ll.setOnClickListener(this);
		
		//当为修改服务地址时，需要为控件初始化数据
		if(isModifyAddr){
			setViewData();
		}
	}
	
	
	/**
	 * 为界面控件设置数据
	 */
	private void setViewData(){
		etUserName.setText(modifyAddr.get("objName"));//联系人名
		etUserPhone.setText(modifyAddr.get("objTel"));//联系电话
		etUserAddr.setText(modifyAddr.get("Addr"));//地址
		area_txt.setText(cityDataUtil.areaIdToAddr(Integer.parseInt(modifyAddr.get("areaId"))));//根据区域id获取城市信息
		selectAreaId = modifyAddr.get("areaId");
	}
	
	
	
	/**
	 * 初始化选择城市控件
	 */
	private void initPopView(View popv) {
		mViewProvince = (WheelView) popv.findViewById(R.id.citypop_id_province);
		mViewCity = (WheelView) popv.findViewById(R.id.citypop_id_city);
		mViewDistrict = (WheelView) popv.findViewById(R.id.citypop_id_district);
		
		mViewProvince.addChangingListener(this);
		mViewCity.addChangingListener(this);
		mViewDistrict.addChangingListener(this);
	}
	
	
	
	
	
	private void setUpData() {
//		initProvinceDatasXml();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(AddServiceAddrsActivity.this, mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}
	
	
	/**
	 * 解析省市区的XML数据 
	 */
	
    protected void initProvinceDatasXml()
	{
//    	AssetManager asset = getAssets();
//        try {
//            InputStream input = asset.open("province_data.xml");
//            // 创建一个解析xml的工厂对象
//			SAXParserFactory spf = SAXParserFactory.newInstance();
//			// 解析xml
//			SAXParser parser = spf.newSAXParser();
//			XmlParserHandler handler = new XmlParserHandler();
//			parser.parse(input, handler);
//			input.close();
//			// 获取解析出来的数据
//			provinceList = handler.getDataList();
			//*/ 初始化默认选中的省、市、区
    	cityDataUtil = new GetCityDataUtil(this);
    	provinceList = cityDataUtil.getProvinceList();
			if (provinceList!= null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList!= null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					mCurrentCityCode = cityList.get(0).getCityZipcode();
					List<DistrictModel> districtList = cityList.get(0).getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			//*/
			mProvinceDatas = new String[provinceList.size()];
        	for (int i=0; i< provinceList.size(); i++) {
        		// 遍历所有省的数据
        		mProvinceDatas[i] = provinceList.get(i).getName();
        		List<CityModel> cityList = provinceList.get(i).getCityList();
        		String[] cityNames = new String[cityList.size()];
        		String[] cityZipCodes = new String[cityList.size()];
        		for (int j=0; j< cityList.size(); j++) {
        			// 遍历省下面的所有市的数据
        			cityNames[j] = cityList.get(j).getName();
        			cityZipCodes[j] = cityList.get(j).getCityZipcode();
        			List<DistrictModel> districtList = cityList.get(j).getDistrictList();
        			String[] distrinctNameArray = new String[districtList.size()];
        			String[] distrinctCodeArray = new String[districtList.size()];
        			DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
        			for (int k=0; k<districtList.size(); k++) {
        				// 遍历市下面所有区/县的数据
        				DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
        				// 区/县对于的邮编，保存到mZipcodeDatasMap
//        				mZipcodeDatasMap.put(cityNames[j], districtList.get(k).getZipcode());
        				distrinctArray[k] = districtModel;
        				distrinctNameArray[k] = districtModel.getName();
        				distrinctCodeArray[k] = districtModel.getZipcode();
        			}
        			// 市-区/县的数据，保存到mDistrictDatasMap
        			mDistrictDatasMap.put(cityZipCodes[j], distrinctNameArray);
        			mZipcodeDatasMap.put(cityZipCodes[j], distrinctCodeArray);
//        			mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
//        			mZipcodeDatasMap.put(cityNames[j], distrinctCodeArray);
        		}
        		// 省-市的数据，保存到mCitisDatasMap
        		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
        		mCitisCodeDatasMap.put(provinceList.get(i).getName(), cityZipCodes);
        	}
//        } catch (Throwable e) {  
//            e.printStackTrace();  
//        } finally {
//        	
//        } 
	}
	
	
	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		mCurrentCityCode = mCitisCodeDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityCode);
//		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);
		
//		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
//		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentCityName)[0];
		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityCode)[0];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentCityCode)[0];
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}
	

	/**
	 * 弹出的选择城市控件
	 * @param v
	 */
	private void popWindowCity(View v){
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		popView = LayoutInflater.from(this).inflate(R.layout.city_wheel,null);
		lp.alpha = 0.7f;
		cd = new ColorDrawable(Color.WHITE);
		CityPop pop = new CityPop(this, popView, cd);
		pop.setOutsideTouchable(true);
		pop.showPopmenu(v, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, Gravity.BOTTOM, 0, GetScreenInchUtil.getVrtualBtnHeight(this));
		getWindow().setAttributes(lp);
		
		
		pop.setCityOnClickListener(new CityCallBack() {
			
			@Override
			public void callBackConfirm(PopupWindow myPopmenu) {
				area_txt.setText(mCurrentProviceName+""+mCurrentCityName+""+mCurrentDistrictName+"");
				selectAreaId = mCurrentZipCode;
				myPopmenu.dismiss();
			}
			
			@Override
			public void callBackCacel(PopupWindow myPopmenu) {
				myPopmenu.dismiss();
			}
		});
		
		
		initPopView(popView);
		setUpData();
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.area_ll:
			
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(etUserName.getWindowToken(), 0);
			
			popWindowCity(v);
			break;
		case R.id.public_back:
			finish();
			break;
		case R.id.bt_city_sure:
			String strUserName = etUserName.getText().toString().trim();
			String strUserPhone = etUserPhone.getText().toString().trim();
			String strUserAddr = etUserAddr.getText().toString().trim();
			//判断输入内容是否合法
			if(UniversalUtils.isStringEmpty(strUserName)){
				ToastUtils.show(this, "请输入联系人名", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(strUserPhone)){
				ToastUtils.show(this, "请输入联系电话", Toast.LENGTH_SHORT);
				return;
			}
			if(strUserPhone.length() < 11){
				ToastUtils.show(this, "请输入正确的手机号码", 0);
				return;
			}
			if(UniversalUtils.isStringEmpty(strUserAddr)){
				ToastUtils.show(this, "请输入详细地址", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.parseString2Int(selectAreaId) == 0){
				ToastUtils.show(this, "选择正确的地区", Toast.LENGTH_SHORT);
				return;
			}
			
			
			//设置按钮为不可点击
			btCitySure.setClickable(false);
			btCitySure.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
			
			//加载进度条
			progressDialog = new CustomProgressDialog(this);
			progressDialog.show();
			
			loadAddServiceAddrData();
			break;

		default:
			break;
		}
		
	}
	
	
	// 用于对按钮不可点击进行计时
	Runnable btnTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(BTN_TIMER_OVER);
		}
	};
	


	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		// TODO Auto-generated method stub
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
//			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
//			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentCityName)[newValue];
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityCode)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentCityCode)[newValue];
		}
	}
	
	
	
//	/**
//	 * 将areaid转换为具体地址名称
//	 */
//	private String  areaIdToAddr(int areaId){
//		for (int i = 0; i < provinceList.size(); i++) {
//			String strProvince = provinceList.get(i).getName();
//			List<CityModel> cityList = provinceList.get(i).getCityList();
//			for (int j = 0; j < cityList.size(); j++) {
//				String strCity = cityList.get(j).getName();
//				List<DistrictModel> districtList = cityList.get(j).getDistrictList();
//				for (int k = 0; k < districtList.size(); k++) {
//					String strDistrict = districtList.get(k).getName();
//					String zipcode = districtList.get(k).getZipcode();
//					if(Integer.parseInt(zipcode) == areaId){
//						String addr = strProvince +"-"+ strCity +"-"+strDistrict;
//						return addr;
//					}
//				}
//			}
//			
//		}
//		return	"";
//	}
	
	
	
//	//此方法不要删 wangf
	
//	/**
//	 * 初始化省份数据 使用数据库数据
//	 */
//	private void initProvinceDatasDb(){
//		
//		ArrayList<Area> provinceList;
//		
//		dBhelper = new DBhelper(this);
//		provinceList = dBhelper.getProvince();
//		
//		// 初始化默认选中的省、市、区
//		if (provinceList != null && !provinceList.isEmpty()) {
//			mCurrentProviceName = provinceList.get(0).getName();
//			ArrayList<Area> cityList = dBhelper.getCity(provinceList.get(0).getCode());
//			if (cityList != null && !cityList.isEmpty()) {
//				mCurrentCityName = cityList.get(0).getName();
//				ArrayList<Area> districtList = dBhelper.getDistrict(cityList.get(0).getCode());
//				if (districtList != null && !districtList.isEmpty()) {
//					mCurrentDistrictName = districtList.get(0).getName();
//				}
//			}
//		}
//		
//		mProvinceDatas = new String[provinceList.size()];
//		for (int i = 0; i < provinceList.size(); i++) {
//			//遍历所有省的数据
//			mProvinceDatas[i] = provinceList.get(i).getName();
//			List<Area> cityList = dBhelper.getCity(provinceList.get(i).getCode());
//			String[] cityNames = new String[cityList.size()];
//			for(int j = 0; j < cityList.size(); j++){
//				// 遍历省下面的所有市的数据
//    			cityNames[j] = cityList.get(j).getName();
//    			List<Area> districtList = dBhelper.getDistrict(cityList.get(j).getCode());
//    			String[] distrinctName = new String[districtList.size()];
//    			
//    			for (int k=0; k<districtList.size(); k++) {
//    				// 遍历市下面所有区/县的数据
//    				mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getCode());
//    				distrinctName[k] = districtList.get(k).getName();
//    			}
//    			// 市-区/县的数据，保存到mDistrictDatasMap
//    			mDistrictDatasMap.put(cityNames[j], distrinctName);
//			}
//			// 省-市的数据，保存到mCitisDatasMap
//    		mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
//			
//		}
//	}
	
	
	/**
	 * 用户添加服务地址 请求
	 */
	public void loadAddServiceAddrData() {
		seqAddServiceNo = MyApplication.SequenceNo++;
		HsUserServiceAddrInfo_Req addrInfo_Req = new MsgInncDef().new HsUserServiceAddrInfo_Req();
		addrInfo_Req.uUserID = myApplication.userId;//用户id
//		addrInfo_Req.uAreaID = Integer.parseInt(selectAreaId);// 地址区域id
		addrInfo_Req.uAreaID = UniversalUtils.parseString2Int(selectAreaId);// 地址区域id
		if(isModifyAddr){
			addrInfo_Req.uAddrIndex = UniversalUtils.parseString2Int(modifyAddr.get("sqn"));//地址序号
//			addrInfo_Req.uAddrIndex = Integer.parseInt(modifyAddr.get("sqn"));//地址序号
		}else{
			addrInfo_Req.uAddrIndex = 0;
		}
		addrInfo_Req.szUserName = etUserName.getText().toString();//联系人名
		addrInfo_Req.szUserTel = etUserPhone.getText().toString();//联系人电话
		addrInfo_Req.szUserAddr = etUserAddr.getText().toString();//联系人地址
		
		byte[] connData = HandleNetSendMsg
				.HandleHsUserAddServiceAddr_ReqToPro(addrInfo_Req,seqAddServiceNo);
		
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("用户添加服务地址 请求--sequence="+seqAddServiceNo +"/"+ Arrays.toString(connData) + "----------");
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
				
				if (seqAddServiceNo == iSequence) {
					processAddServiceData(recvTime);
				}
				
			}
		}
	};
	
	
	
	/**
	 * 处理添加服务地址响应数据
	 * @param iSequence
	 */
	private void processAddServiceData(long recvTime){
		HsNetCommon_Resp netConn = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(netConn == null){
			return;
		}
		
		LogUtils.i("用户添加服务地址 返回数据成功");
		//若服务端返回数据，将按钮不可点击的计时去掉
		handler.removeCallbacks(btnTimerRunnable);
		progressDialog.dismiss();
		if (netConn.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			LogUtils.i("用户添加地址成功,添加成功=======");
			
			DataBaseService ds = new DataBaseService(getApplicationContext());
			
			if(isModifyAddr){
				String deleteSql = DbOprationBuilder.deleteBuilderby("userAddr", "id", modifyAddr.get("id"));
				ds.delete(deleteSql);
//				String updateSql = DbOprationBuilder.updateBuider("userAddr", "isDelete", "1","id", modifyAddr.get("id"));
//				ds.update(updateSql);
			}
			
			String insertSql = DbOprationBuilder.insertUserServiceAddAllBuilder(
					myApplication.userId,//userid
					UniversalUtils.parseString2Int(selectAreaId),//地址区域id
					etUserAddr.getText().toString(),//地址
//					mCurrentProviceName + mCurrentCityName + mCurrentDistrictName + etUserAddr.getText().toString(),//详细地址(长地址)
					area_txt.getText().toString()+ "" + etUserAddr.getText().toString(),//详细地址(长地址)
					0,//选中状态
					0,//是否删除状态
					etUserPhone.getText().toString(),//电话
					etUserName.getText().toString(),//联系人名
					netConn.iSelectID);//地址序号--使用通用响应的数据
			ds.insert(insertSql);
			
			Message message =new Message();
			message.what = LOAD_DATA_SUCCESS;
			handler.sendMessage(message);
			
		} else {
			String result = UniversalUtils.judgeNetResult_Hs(netConn.eOperResult);
			LogUtils.i("用户添加地址失败,失败原因"+result+"=======");
			
			Message message =new Message();
			message.what = LOAD_DATA_FAIL;
			handler.sendMessage(message);
			
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
