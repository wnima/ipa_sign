package com.bootdo.signature.service;

public interface SignatureService {
	public void exec(String key, Long appId, String udid);

	public void addTask(String key);
	
	public void addDevice(String key,Long appId, String udid);
}
