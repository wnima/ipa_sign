package com.bootdo.signature.service;

import com.bootdo.common.utils.R;
import com.bootdo.signature.entity.po.HeavyVolume;

public interface HeavyVolumeService {
	void configuration(long appId,int enable, long startTime,long intervals,int downNum,int addNum);
	void addUseNumber(long appId,long userId);
	R getConfig(int appId);
	HeavyVolume getConfigAndInit(int appId);
}
