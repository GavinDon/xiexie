package com.lhdz.util;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.content.res.AssetManager;

import com.lhdz.cityDao.CityModel;
import com.lhdz.cityDao.DistrictModel;
import com.lhdz.cityDao.ProvinceModel;
import com.lhdz.cityDao.XmlParserHandler;

/**
 * 获取城市数据 
 */
public class GetCityDataUtil {
	
	private Context context;
	List<ProvinceModel> provinceList = null;//解析province_data.xml后存放的城市数据
	
	
	public GetCityDataUtil(Context context) {
		this.context = context;
		getProvinceData();
	}
	
	
	/**
	 * 获取province_data.xml中所有城市的数据
	 */
	private void getProvinceData(){
    	AssetManager asset = context.getAssets();
            InputStream input;
			try {
				input = asset.open("province_data.xml");
				 // 创建一个解析xml的工厂对象
				SAXParserFactory spf = SAXParserFactory.newInstance();
				// 解析xml
				SAXParser parser = spf.newSAXParser();
				XmlParserHandler handler = new XmlParserHandler();
				parser.parse(input, handler);
				input.close();
				// 获取解析出来的数据
				provinceList = handler.getDataList();
			} catch (Throwable e) {
				e.printStackTrace();
			}
	}
	
	
	/**
	 * 获取所有省份数据
	 * @return
	 */
	public List<ProvinceModel> getProvinceList(){
		return provinceList;
	}
	
	
	
	/**
	 * 将areaid转换为具体地址名称
	 */
	public String  areaIdToAddr(int areaId){
		
		for (int i = 0; i < provinceList.size(); i++) {
			String strProvince = provinceList.get(i).getName();
			List<CityModel> cityList = provinceList.get(i).getCityList();
			for (int j = 0; j < cityList.size(); j++) {
				String strCity = cityList.get(j).getName();
				List<DistrictModel> districtList = cityList.get(j).getDistrictList();
				for (int k = 0; k < districtList.size(); k++) {
					String strDistrict = districtList.get(k).getName();
					String zipcode = districtList.get(k).getZipcode();
					if(Integer.parseInt(zipcode) == areaId){
						String addr = strProvince + strCity + strDistrict;
						return addr;
					}
				}
			}
		}
		
		return	"";
	}
	
	
	
	/**
	 * 将具体的城市名称转换为areaId
	 * @param strAddr
	 * @return
	 */
	public int addrToAreaId(String strAddr) {

		int iAreaId = 610100;

		if (UniversalUtils.isStringEmpty(strAddr)) {
			return iAreaId;
		}

		for (int i = 0; i < provinceList.size(); i++) {
			String strProvince = provinceList.get(i).getName();
			List<CityModel> cityList = provinceList.get(i).getCityList();
			for (int j = 0; j < cityList.size(); j++) {
				String strCity = cityList.get(j).getName();
				if (strAddr.equals(strCity)) {
					iAreaId = Integer.parseInt(cityList.get(j).getCityZipcode());
					return iAreaId;
				}
			}
		}

		return iAreaId;
	}
	
	
}
