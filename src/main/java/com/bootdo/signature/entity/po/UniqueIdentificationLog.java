package com.bootdo.signature.entity.po;

public class UniqueIdentificationLog {
	private String udid;
	private long accountId;
	private String mobileprovision;
	
	public String getMobileprovision() {
		return mobileprovision;
	}
	public void setMobileprovision(String mobileprovision) {
		this.mobileprovision = mobileprovision;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	@Override
	public String toString() {
		return "UniqueIdentificationLog [udid=" + udid + ", accountId=" + accountId + ", mobileprovision="
				+ mobileprovision + "]";
	}
}
