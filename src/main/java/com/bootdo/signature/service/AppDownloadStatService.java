package com.bootdo.signature.service;

import java.util.List;
import java.util.Map;

import com.bootdo.signature.entity.po.AppDownloadStat;

public interface AppDownloadStatService {
	// 获取app下载详细信息
	Map<String,Object> getAllDetails(int appId);
	
	List<AppDownloadStat> getMonthStat(int appId,int offset,int item);
	List<AppDownloadStat> getDayStat(int appId,int offset,int item);
	List<AppDownloadStat> getHourStat(int appId,int offset,int item);
}
