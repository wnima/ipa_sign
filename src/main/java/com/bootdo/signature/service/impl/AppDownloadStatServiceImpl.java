package com.bootdo.signature.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.signature.dao.AppDownloadStatDao;
import com.bootdo.signature.entity.po.AppDownloadStat;
import com.bootdo.signature.service.AppDownloadStatService;

@Service
public class AppDownloadStatServiceImpl implements AppDownloadStatService{

	@Autowired
	AppDownloadStatDao appDownloadStatDao;
	
	@Override
	public Map<String, Object> getAllDetails(int appId) {
		Map<String,Object> result = new HashMap<>();
		// 获取月统计
		List<AppDownloadStat> monthList = getMonthStat(appId, 0, 24);
		result.put("month", monthList);
		// 获取日统计
		List<AppDownloadStat> dayList = getDayStat(appId, 0, 24);
		result.put("day", dayList);
		// 获取小时统计
		List<AppDownloadStat> hourList = getDayStat(appId, 0, 24);
		result.put("hour", hourList);
		result.put("o", 0);
		return result;
	}
	
	public List<AppDownloadStat> getMonthStat(int appId,int offset,int item){
		long currTime = System.currentTimeMillis();
		long startTime = getMonthInitTime(currTime);
		List<AppDownloadStat> list = appDownloadStatDao.queryMonthRecord(appId, offset, item);
		int count = appDownloadStatDao.queryDownloadCountByAppId(appId, startTime, currTime);
		AppDownloadStat stat = new AppDownloadStat();
		stat.setNum(count);
		stat.setAppId(appId);
		stat.settTime(currTime);
		list.add(0,stat);
		return list;
	}
	
	public List<AppDownloadStat> getDayStat(int appId,int offset,int item){
		long currTime = System.currentTimeMillis();
		long startTime = getDayInitTime(currTime);
		List<AppDownloadStat> list = appDownloadStatDao.queryDayRecord(appId, offset, item);
		int count = appDownloadStatDao.queryDownloadCountByAppId(appId, startTime, currTime);
		AppDownloadStat stat = new AppDownloadStat();
		stat.setNum(count);
		stat.setAppId(appId);
		stat.settTime(currTime);
		list.add(0,stat);
		return list;
	}
	
	public List<AppDownloadStat> getHourStat(int appId,int offset,int item){
		long currTime = System.currentTimeMillis();
		long startTime = getHourInitTime(currTime);
		List<AppDownloadStat> list = appDownloadStatDao.queryHourRecord(appId, offset, item);
		int count = appDownloadStatDao.queryDownloadCountByAppId(appId, startTime, currTime);
		AppDownloadStat stat = new AppDownloadStat();
		stat.setNum(count);
		stat.setAppId(appId);
		stat.settTime(currTime);
		list.add(0,stat);
		return list;
	}

	String formatTime(long time){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
		return format.format(new Date(time));
	}
	
	long getDayInitTime(long time){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String s = format.format(new Date(time));
			Date d = format.parse(s);
			return d.getTime();
		}catch (Exception e) {
		}
		return 0;
	}
	
	long getHourInitTime(long time) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
			String s = format.format(new Date(time));
			Date d = format.parse(s);
			return d.getTime();
		}catch (Exception e) {
		}
		return 0;
	}
	
	long getMonthInitTime(long time) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTimeInMillis(time);
		c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
		c2.set(Calendar.MONTH, c1.get(Calendar.MONTH));
		c2.set(Calendar.DATE, 1);
		return c2.getTimeInMillis();
	}
	
	long getNextMonthTime(long time) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(time);
        c1.add(Calendar.MONTH, +1);
		return c1.getTimeInMillis();
	}
}
