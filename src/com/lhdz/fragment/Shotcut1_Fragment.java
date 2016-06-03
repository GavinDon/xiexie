package com.lhdz.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.HomebjActivity;
import com.lhdz.activity.R;
import com.lhdz.adapter.AdapterShotcut1Button;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
public class Shotcut1_Fragment extends Fragment implements
		android.widget.AdapterView.OnItemClickListener {

	private View view;
	private GridView gv_shotcut1;
	private List<Map<String, String>> shotCut1Data = new ArrayList<Map<String, String>>();

	private List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
	
	private AdapterShotcut1Button adapterShotcut1Button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/*
	 * 获取首页查询出来的快捷下单的所有数据
	 */
	public void setShotcutFragmentData(List<Map<String, String>> dataList) {
		this.dataList = dataList;
		if(adapterShotcut1Button != null){
			shotCut1Data.clear();
			setViewData();
			adapterShotcut1Button.setData(shotCut1Data);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_shotcut1, null);
		setViewData();
		initViews();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		gv_shotcut1.setOnItemClickListener(this);

	}
	
	
	/**
	 * 为GridView设置数据
	 */
	private void setViewData(){
		if(dataList.size() < 8){
			return;
		}
		for (int i = 0; i < 8; i++) {
			shotCut1Data.add(dataList.get(i));
		}
	}
	

	/**
	 * 初始化控件
	 */
	private void initViews() {
		gv_shotcut1 = (GridView) view.findViewById(R.id.gv_shotcut1);
		adapterShotcut1Button = new AdapterShotcut1Button(getActivity()
				.getApplicationContext(), shotCut1Data);
		gv_shotcut1.setAdapter(adapterShotcut1Button);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), HomebjActivity.class);
		intent.putExtra("home", (Serializable) dataList.get(position));
		startActivity(intent);

	}

}
