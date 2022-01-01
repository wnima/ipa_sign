package com.bootdo.signature.service.impl;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.signature.dao.VisitLogDao;
import com.bootdo.signature.entity.po.VisitLog;
import com.bootdo.signature.service.VisitLogService;

@Service
public class VisitLogServiceImpl implements VisitLogService {

	@Autowired
	private VisitLogDao dao;
	
	@Override
	public long getToDayPV(long appId) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Long count = dao.selectPVCountByAppId(appId,calendar.getTimeInMillis());
		return count == null ? 0 : count;
	}

	@Override
	public long getToDayUV(long appId) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Long count = dao.selectUVCountByAppId(appId,calendar.getTimeInMillis());
		return count == null ? 0 : count;
	}

	@Override
	public long getToDayIV(long appId) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Long count = dao.selectIPCountByAppId(appId,calendar.getTimeInMillis());
		return count == null ? 0 : count;
	}

	@Override
	public void save(VisitLog log) {
		dao.save(log);
	}

}
