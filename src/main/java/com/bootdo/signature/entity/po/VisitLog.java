package com.bootdo.signature.entity.po;

public class VisitLog {
	private Long id;
	private Long appId;
	private String ip;
	private String sessionId;
	private Long time;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	@Override
	public String toString() {
		return "VisitLog [id=" + id + ", appId=" + appId + ", ip=" + ip + ", sessionId=" + sessionId + ", time=" + time
				+ "]";
	}
}
