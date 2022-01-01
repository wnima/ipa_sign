package com.bootdo.signature.entity.po;

public class DownloadLog {
	private Long id;
	private Long downloadTime;
	private String udid;
	private Long appId;
	private Long userId;
	private int isreal;
	public int getIsreal() {
		return isreal;
	}
	public void setIsreal(int isreal) {
		this.isreal = isreal;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getDownloadTime() {
		return downloadTime;
	}
	public void setDownloadTime(Long downloadTime) {
		this.downloadTime = downloadTime;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	@Override
	public String toString() {
		return "DownloadLog [id=" + id + ", downloadTime=" + downloadTime + ", udid=" + udid + ", appId=" + appId
				+ ", userId=" + userId + "]";
	}
}
