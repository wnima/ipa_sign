package com.bootdo.signature.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.signature.dao.DownloadLogDao;
import com.bootdo.signature.dao.OriginalApplicationDao;

import com.bootdo.signature.entity.po.DownloadLog;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.DownloadLogService;

@Service
public class DownloadLogServiceImpl implements DownloadLogService {

	@Autowired
	private DownloadLogDao dao;
	
	@Autowired
	OriginalApplicationDao originalApplicationDao;
	
	public void save(long appId, String udid, long userId,boolean real) {
		DownloadLog log = new DownloadLog();
		log.setAppId(appId);
		log.setDownloadTime(System.currentTimeMillis());
		log.setUdid(udid);
		log.setUserId(userId);
		log.setIsreal(real ? 1 : 0);
		dao.save(log);
	}
	
	/**
	 * 	获取指定userId总记录数
	 */
	@Override
	public long distinctCountByUserId(long userId) {
		List<DownloadLog> list = dao.selectByUserId(userId);
		Map<Long, Set<String>> map = new HashMap<Long,Set<String>>();
		list.forEach(item->{
			Long appId = item.getAppId();
			if(!map.containsKey(appId)) {
				map.put(appId, new HashSet<String>());
			}
			map.get(appId).add(item.getUdid());
		});
		int count = 0;
		for(Entry<Long, Set<String>> item:map.entrySet()){
			count+=item.getValue().size();
		}
		return count;
	}
	
	/**
	 * 	获取指定userId总记录数
	 */
	public long countByUserId(long userId) {
		Long count = dao.selectCountByUserId(userId);
		if(count == null) {
			return 0;
		}
		return count;
	}
	
	/**
	 * 	获取指定userId一周记录数
	 */
	public Map<Integer, List<DownloadLog>> getWeekByUserId(long userId) {
		Map<Integer, List<DownloadLog>> downloadData = new HashMap<Integer,List<DownloadLog>>();
		long currTime = System.currentTimeMillis();
		for(int i = 0; i < 7 ; i++) {
			//86400000
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(currTime - (i * 86400000));
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			List<DownloadLog> list = dao.selectCountByUserIdAndTime(userId,calendar.getTimeInMillis(),calendar.getTimeInMillis() + 86400000);
			downloadData.put(calendar.get(Calendar.DAY_OF_MONTH), list);
		}
		return downloadData;
	}

	/**
	 * 	获取指定userId今天总记录数（UDID去重）
	 */
	@Override
	public long distinctToDayCountByUserId(long userId) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Long count = dao.selectCountByUserIdAndDistinctUdidAndTime(userId,calendar.getTimeInMillis());		
		if(count == null) {
			return 0;
		}
		return count;
	}

	@Override
	public long countByAppId(long appId) {
		Long count = dao.selectCountByAppId(appId);
		if(count == null) {
			return 0;
		}
		return count;
	}

	@Override
	public void changeLogs(String udid) {
		dao.changeLogs(udid,udid + "BLOCK" + Long.toHexString(System.currentTimeMillis()));
	}

	@Override
	public Map<String, Object> list(Integer flag, Integer num,long userId) {
		int start = 0;
		int end = 100;
		if(num != 0) {
			start = (flag - 1) * num;
			end = start + num;
		}
		List<DownloadLog> logList = dao.limitSelectByUserId(userId, start, end);
		long count = dao.selectCountByUserId(userId);
		
		Map<String, Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(DownloadLog log:logList) {
			OriginalApplication app = originalApplicationDao.selectById(log.getAppId());
			HashMap<String, Object> row = new HashMap<>();
			row.put("appName", app == null ? "(应用已删除)":app.getApplicationName());
			row.put("appPackage", app == null ? "(应用已删除)":app.getPackageName());
			row.put("udid", log.getUdid());
			row.put("model", "");
			row.put("osVersion", "");
			row.put("appVersion", app == null ? "(应用已删除)":app.getVersion());
			row.put("ip", "");
			row.put("downType", "IOS");
			list.add(row);
		}
		data.put("list", list);
		data.put("count", count);
		return data;
	}

	@Override
	public Map<String, Object> statlist(Integer f, Integer n, Long userId) {
		//dao.
		return null;
	}

	@Override
	public long distinctCountByAppId(Long appId) {
		Long count = dao.selectCountByAppIdAndDistinctUdid(appId);
		if(count == null) {
			return 0;
		}
		return count;
	}
	
	@Override
	public long realDistinctCountByAppId(Long appId,long startTime,long endTime) {
		Long count = dao.selectCountByAppIdAndDistinctUdidAndReal(appId,1,startTime,endTime);
		if(count == null) {
			return 0;
		}
		return count;
	}

	@Override
	public void changeUdidByUdid(String newUdid,String udid) {
	 	dao.changeLogs(newUdid, udid);
	}
	
	@Override
	public boolean isContainByUserIdAndUdid(Long userId, String udid) {
	 	List<DownloadLog> list = dao.selectByUserId(userId);
	 	for(DownloadLog item:list) {
	 		if(item.getUdid() != null && item.getUdid().equals(udid)) {
	 			return true;
	 		}
	 	}
		return false;
	}

	@Override
	public long fakeLastDownloadLogByAppId(long appId,long startTime,long endTime) {
		Long count = dao.selectCountByAppIdAndDistinctUdidAndReal(appId,0,startTime,endTime);
		if(count == null) {
			return 0;
		}
		return count;
	}
}
