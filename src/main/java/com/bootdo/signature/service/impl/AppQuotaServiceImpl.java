package com.bootdo.signature.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.signature.dao.AppQuotaDao;
import com.bootdo.signature.entity.po.AppQuota;
import com.bootdo.signature.service.AppQuotaService;

@Service
public class AppQuotaServiceImpl implements AppQuotaService {

	@Autowired
	AppQuotaDao appQuotaDao;
	
	@Override
	public AppQuota findByAppId(long appId) {
		return appQuotaDao.selectByAppId(appId);
	}

	@Override
	public boolean updateLimitById(int id,int limit) {
	 	int row = appQuotaDao.updateLimitById(limit, id);
		return row == 1;
	}

	@Override
	public boolean deleteById(int id) {
		int row = appQuotaDao.deleteById(id);
		return row == 1;
	}

	@Override
	public void save(AppQuota appQuota) {
		appQuotaDao.insert(appQuota);
	}

	@Override
	public boolean updateNumById(int id, int num) {
	 	int row = appQuotaDao.updateNumById(num, id);
		return row == 1;
	}
}
