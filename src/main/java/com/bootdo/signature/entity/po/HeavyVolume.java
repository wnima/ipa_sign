package com.bootdo.signature.entity.po;

public class HeavyVolume {
	private Long id;
	private Integer enable;
	private Long startTime;
	private Long intervals;
	private int downNum;
	private int addNum;
	private Long appId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Integer getEnable() {
		return enable;
	}
	public void setEnable(Integer enable) {
		this.enable = enable;
	}
	public Long getStartTime() {
		return startTime;
	}
	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	public Long getIntervals() {
		return intervals;
	}
	public void setIntervals(Long intervals) {
		this.intervals = intervals;
	}
	public int getDownNum() {
		return downNum;
	}
	public void setDownNum(int downNum) {
		this.downNum = downNum;
	}
	public int getAddNum() {
		return addNum;
	}
	public void setAddNum(int addNum) {
		this.addNum = addNum;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}

}
