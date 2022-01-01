package com.bootdo.signature.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.common.utils.R;
import com.bootdo.signature.dao.RechargeLogDao;
import com.bootdo.signature.entity.po.RechargeLog;
import com.bootdo.signature.service.RechargeLogService;

@Service
public class RechargeLogServiceImpl implements RechargeLogService {

	@Autowired
	private RechargeLogDao rechargeLogDao;
	
	@Override
	public long selectCountByUserId(long userId) {
		Long count = rechargeLogDao.selectCountByUserId(userId);
		if(count == null) {
			count = 0L;
		}
		return count;
	}

	@Override
	public void save(long userId,String userName,long number,long createrId,String createrName) {
		RechargeLog log = new RechargeLog();
		log.setCreaterId(createrId);
		log.setCreateTime(System.currentTimeMillis());
		log.setNumber(number);
		log.setUserId(userId);
		log.setUserName(userName);
		log.setCreaterName(createrName);
		rechargeLogDao.save(log);
	}

	@Override
	public R selectListByUserId(int p, int i,Long userId) {
		int start = p * i;
		long count = rechargeLogDao.selectCount();
		List<RechargeLog> loglist = rechargeLogDao.selectListLimitByUserId(start, i, userId);
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		for(RechargeLog log:loglist) {
			HashMap<String, Object> item = new HashMap<String,Object>();
			item.put("username", log.getUserName());
			item.put("creatername", log.getCreaterName());
			item.put("num", log.getNumber());
			item.put("time", log.getCreateTime() / 1000);
			item.put("type", 1);
			data.add(item);
		}
		return R.ok().put("data", data).put("count", count).put("p", p).put("i", i);
	}

	@Override
	public R selectListLimit(int p, int i) {
		int start = p * i;
		long count = rechargeLogDao.selectCount();
		List<RechargeLog> loglist = rechargeLogDao.selectListLimit(start, i);
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		for(RechargeLog log:loglist) {
			HashMap<String, Object> item = new HashMap<String,Object>();
			item.put("username", log.getUserName());
			item.put("creatername", log.getCreaterName());
			item.put("num", log.getNumber());
			item.put("time", log.getCreateTime() / 1000);
			item.put("type", 1);
			data.add(item);
		}
		return R.ok().put("data", data).put("count", count).put("p", p).put("i", i);
	}
}
