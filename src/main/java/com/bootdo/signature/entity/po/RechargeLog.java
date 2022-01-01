package com.bootdo.signature.entity.po;

public class RechargeLog {
	private Long id;
	private Long userId;
	private Long number;
	private Long createTime;
	private Long createrId;
	private String createrName;
	private String userName;
	
	public String getCreaterName() {
		return createrName;
	}
	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getNumber() {
		return number;
	}
	public void setNumber(Long number) {
		this.number = number;
	}
	public Long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getCreaterId() {
		return createrId;
	}
	public void setCreaterId(Long createrId) {
		this.createrId = createrId;
	}
	@Override
	public String toString() {
		return "RechargeLog [id=" + id + ", userId=" + userId + ", number=" + number + ", createTime=" + createTime
				+ ", createrId=" + createrId + "]";
	}
}
