package com.bootdo.signature.service;

public interface RechargeService {

	boolean consume(Long appId, Long userId,int num,boolean real);

	long getRechargeCount(Long userId);
	
	void addRechargeNum(Long userId,String userName, int num, Long createId,String createrName);
	
	long getUseCount(Long userId);
	
}
