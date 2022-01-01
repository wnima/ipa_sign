package com.bootdo.signature.entity.po;

public class SignaturePackage {
	private long id;
	private String udid;
	private long appId;
	private String fileUrl;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	@Override
	public String toString() {
		return "SignaturePackage [id=" + id + ", udid=" + udid + ", appId=" + appId + ", fileUrl=" + fileUrl + "]";
	}
	
}
