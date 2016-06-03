package com.lhdz.cityDao;

import java.util.List;

public class CityModel {
	private String name;
	private String cityZipcode;
	private List<DistrictModel> districtList;
	
	public CityModel() {
		super();
	}

	public CityModel(String name, List<DistrictModel> districtList) {
		super();
		this.name = name;
		this.districtList = districtList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCityZipcode() {
		return cityZipcode;
	}

	public void setCityZipcode(String cityZipcode) {
		this.cityZipcode = cityZipcode;
	}

	public List<DistrictModel> getDistrictList() {
		return districtList;
	}

	public void setDistrictList(List<DistrictModel> districtList) {
		this.districtList = districtList;
	}

	@Override
	public String toString() {
		return "CityModel [name=" + name + ",cityZipcode="+cityZipcode+", districtList=" + districtList
				+ "]";
	}
	
}
