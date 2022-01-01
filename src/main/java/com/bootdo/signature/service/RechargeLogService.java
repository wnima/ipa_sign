package com.bootdo.signature.service;

import com.bootdo.common.utils.R;

public interface RechargeLogService {
	long selectCountByUserId(long appId);
	void save(long userId,String userName,long number,long createrId,String createrName);
	R selectListByUserId(int p, int i,Long userId);
	R selectListLimit(int p, int i);
}
