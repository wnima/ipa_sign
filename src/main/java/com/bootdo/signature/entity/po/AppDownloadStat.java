package com.bootdo.signature.entity.po;

public class AppDownloadStat {
	private Integer id;
	private Long tTime;
	private Integer appId;
	private Integer num;
	private String remark;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Long gettTime() {
		return tTime;
	}
	public void settTime(Long tTime) {
		this.tTime = tTime;
	}
	public Integer getAppId() {
		return appId;
	}
	public void setAppId(Integer appId) {
		this.appId = appId;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	@Override
	public String toString() {
		return "AppDownloadStat [id=" + id + ", tTime=" + tTime + ", appId=" + appId + ", num=" + num + ", remark="
				+ remark + "]";
	}
}
