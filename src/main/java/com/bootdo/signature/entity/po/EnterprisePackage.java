package com.bootdo.signature.entity.po;

public class EnterprisePackage {
	private Integer id;
	private int appId;
	private String url;
	private int status;
	
	public Integer getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAppId() {
		return appId;
	}
	public void setAppId(int appId) {
		this.appId = appId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "EnterprisePackage [id=" + id + ", appId=" + appId + ", url=" + url + ", status=" + status + "]";
	}
}
