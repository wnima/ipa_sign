package com.bootdo.signature.service;

import java.util.List;
import java.util.Map;

import com.bootdo.signature.entity.po.DownloadLog;

public interface DownloadLogService {
	public void save(long appId,String udid,long userId,boolean real);
	
	public long countByUserId(long userId);
	
	public long distinctCountByUserId(long userId);

	public Map<Integer, List<DownloadLog>> getWeekByUserId(long userId);

	public long distinctToDayCountByUserId(long userId);

	public long countByAppId(long appId);

	public void changeLogs(String udid);

	public Map<String,Object> list(Integer f, Integer n,long userId);

	public Map<String, Object> statlist(Integer f, Integer n, Long userId);

	public long distinctCountByAppId(Long appId);
	
	public long realDistinctCountByAppId(Long appId,long startTime,long endTime);

	public boolean isContainByUserIdAndUdid(Long userId, String udid);
	
	public void changeUdidByUdid(String newUdid, String udid);

	public long fakeLastDownloadLogByAppId(long appId,long startTime,long endTime);
}
