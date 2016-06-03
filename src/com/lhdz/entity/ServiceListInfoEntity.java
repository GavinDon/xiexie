package com.lhdz.entity;

import java.io.Serializable;

public class ServiceListInfoEntity implements Serializable{

	private String serviceName;
	private String servicePrice;
	private String serviceUnit;
	private int serviceNum;
	
	
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServicePrice() {
		return servicePrice;
	}
	public void setServicePrice(String servicePrice) {
		this.servicePrice = servicePrice;
	}
	public String getServiceUnit() {
		return serviceUnit;
	}
	public void setServiceUnit(String serviceUnit) {
		this.serviceUnit = serviceUnit;
	}
	public int getServiceNum() {
		return serviceNum;
	}
	public void setServiceNum(int serviceNum) {
		this.serviceNum = serviceNum;
	}

}
