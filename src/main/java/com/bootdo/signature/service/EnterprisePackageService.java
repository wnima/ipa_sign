package com.bootdo.signature.service;

import com.bootdo.common.utils.R;
import com.bootdo.signature.entity.po.EnterprisePackage;

public interface EnterprisePackageService {
	R list(int flag,int num);
	boolean save(EnterprisePackage enPackage);
	boolean update(EnterprisePackage enPackage);
	void delete(int appId);
	EnterprisePackage findByAppId(long appId);
}
