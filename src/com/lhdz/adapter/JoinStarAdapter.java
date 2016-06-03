package com.lhdz.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lhdz.activity.R;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserAddCompanyInfo_Pro;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.util.Define;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class JoinStarAdapter extends BaseAdapter {
	Context context;
	List<Map<String, String>> starCompanyDataList;
	List<Map<String, String>> appHomeDataList = null;// 首页快捷下单数据
	ImageLoader loader = ImageLoader.getInstance();

	public JoinStarAdapter(Context context,
			List<Map<String, String>> appHomeDataList) {
		this.context = context;
		this.appHomeDataList = appHomeDataList;
		this.starCompanyDataList = new ArrayList<Map<String, String>>();
	}

	@Override
	public int getCount() {
		if (starCompanyDataList != null) {
			return starCompanyDataList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (starCompanyDataList != null) {
			return starCompanyDataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(List<Map<String, String>> list) {
		this.starCompanyDataList.clear();
		this.starCompanyDataList.addAll(list);
		notifyDataSetChanged();
	}

	public void addData(List<Map<String, String>> list) {
		this.starCompanyDataList.addAll(list);
		notifyDataSetChanged();
	}

	public void clear() {
		this.starCompanyDataList.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, String> starCompany = starCompanyDataList.get(position);
		if (starCompany == null) {
			return null;
		}

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_joinstar, null);
			holder = new ViewHolder();
			holder.vhImage = (ImageView) convertView.findViewById(R.id.iv_);
			holder.zhuangtai = (ImageView) convertView
					.findViewById(R.id.zhuangtai);
			holder.vhName = (TextView) convertView
					.findViewById(R.id.tv_comname);
			holder.vhServer = (TextView) convertView
					.findViewById(R.id.tv_server);
			holder.star = (RatingBar) convertView
					.findViewById(R.id.joinstar_rating);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		//将家政公司的服务类型 由编号转为汉字
		List<Map<String, String>> serviceTypeList = UniversalUtils
				.getServiceType(appHomeDataList,starCompany.get("szServiceInfo"));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < serviceTypeList.size(); i++) {
			builder.append(serviceTypeList.get(i).get("code") + " ");
		}

		
		//初始化加入状态的图标，全部设置为隐藏，再根据状态码显示出来
		holder.zhuangtai.setVisibility(View.GONE);
		
		//只有登录状态才能显示出加入状态标识，未登录时将所有公司显示，且不显示标识
		if(MyApplication.loginState){
			//取得该条数据的加入状态
			int iVerifyFlag = 0;
			if(!UniversalUtils.isStringEmpty(starCompany.get("iVerifyFlag"))){
				iVerifyFlag = Integer.parseInt(starCompany.get("iVerifyFlag"));
			}
			//申请
			if (NetHouseMsgType.JOINSTATE_APPLY == iVerifyFlag) {
				holder.zhuangtai.setImageResource(R.drawable.tag_04);
				holder.zhuangtai.setVisibility(View.VISIBLE);
			}
			//审核
			if(NetHouseMsgType.JOINSTATE_SHENHE == iVerifyFlag){
				holder.zhuangtai.setImageResource(R.drawable.tag_03);
				holder.zhuangtai.setVisibility(View.VISIBLE);
			}
			//加入
			if(NetHouseMsgType.JOINSTATE_JOIN == iVerifyFlag){
				holder.zhuangtai.setImageResource(R.drawable.tag_01);
				holder.zhuangtai.setVisibility(View.VISIBLE);
			}
			//拒绝
			if(NetHouseMsgType.JOINSTATE_JUJUE == iVerifyFlag){
				
			}
			//解约
			if (NetHouseMsgType.JOINSTATE_JIEYUE == iVerifyFlag) {
				
			}
		}

		
//		holder.vhServer.setText(builder.toString());// 服务类型
		holder.vhServer.setText(starCompany.get("szAddr"));// 服务类型修改为地址
		holder.vhName.setText(starCompany.get("szName").toString());// 公司名称
		holder.star.setRating(UniversalUtils.processRatingLevel(starCompany.get("iStarLevel")));// 公司星级
		loader.displayImage(Define.URL_COMPANY_IMAGE + starCompany.get("szCompanyUrl"), holder.vhImage);

		return convertView;
	}
}
