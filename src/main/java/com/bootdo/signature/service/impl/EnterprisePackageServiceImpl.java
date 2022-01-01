package com.bootdo.signature.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.common.utils.R;
import com.bootdo.signature.dao.EnterprisePackageDao;
import com.bootdo.signature.entity.po.EnterprisePackage;
import com.bootdo.signature.service.EnterprisePackageService;

@Service
public class EnterprisePackageServiceImpl implements EnterprisePackageService{

	@Autowired
	EnterprisePackageDao enterprisePackageDao;
	
	@Override
	public R list(int flag,int num) {
		if(flag > 0) {
			flag -= 1;
		}
		int offset = flag * num;
		List<EnterprisePackage> list = enterprisePackageDao.selectLimit(offset, num);
		int count = enterprisePackageDao.selectCount();
		
		R result = R.ok();
		result.put("data", list);
		result.put("count", count);
		return result;
	}

	@Override
	public boolean save(EnterprisePackage enPackage) {
		int row = 0;
		if(enPackage.getId() == null) {
			row = enterprisePackageDao.insert(enPackage);
		}else {
			row = enterprisePackageDao.update(enPackage);
		}
		return row == 1;
	}

	@Override
	public boolean update(EnterprisePackage enPackage) {
	 	int row = enterprisePackageDao.update(enPackage);
	 	return row == 1;
	}

	@Override
	public void delete(int appId) {
		EnterprisePackage enter = enterprisePackageDao.selectByAppId(appId);
		if(enter != null) {
			enterprisePackageDao.deleteByid(enter.getId());
		}
	}

	@Override
	public EnterprisePackage findByAppId(long appId) {
		EnterprisePackage enter = enterprisePackageDao.selectByAppId(appId);
		return enter;
	}

}
