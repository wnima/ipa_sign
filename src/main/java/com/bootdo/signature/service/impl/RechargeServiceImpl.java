package com.bootdo.signature.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bootdo.signature.dao.RechargeDao;
import com.bootdo.signature.entity.po.Recharge;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.signature.service.RechargeLogService;
import com.bootdo.signature.service.RechargeService;

@Service
public class RechargeServiceImpl implements RechargeService {

	@Autowired
	RechargeDao rechargeDao;
	
	@Autowired
	RechargeLogService rechargeLogService;
	
	@Autowired
	OriginalApplicationService originalApplicationService;
	
	@Override
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public boolean consume(Long appId, Long userId,int num,boolean real) {
	 	Recharge recharge = rechargeDao.selectByUserId(userId);
	 	if(recharge != null) {
	 	 	int rechargeNum = recharge.getRechargeNum();
	 	 	int useNum = recharge.getUseNum() + num;
	 	 	if(useNum <= rechargeNum) {
	 	 		recharge.setUseNum(useNum);
		 	 	rechargeDao.update(recharge);
		 	 	originalApplicationService.updateDownloadNum(appId,num,real);
		 	 	return true;
	 	 	}
	 	}
		return false;
	}

	@Override
	public long getRechargeCount(Long userId) {
		Recharge recharge = rechargeDao.selectByUserId(userId);
		return recharge != null ? recharge.getRechargeNum() : 0;
	}
	
	@Override
	public long getUseCount(Long userId) {
		Recharge recharge = rechargeDao.selectByUserId(userId);
		return recharge != null ? recharge.getUseNum() : 0;
	}

	@Override
	@Transactional
	public void addRechargeNum(Long userId, String userName, int num, Long createId, String createrName) {
		Recharge recharge = rechargeDao.selectByUserId(userId);
		if(recharge == null) {
		 	recharge = new Recharge();
		 	recharge.setRechargeNum(num);
		 	recharge.setUseNum(0);
		 	recharge.setUserId(userId.intValue());
		 	rechargeDao.insert(recharge);
		}else {
			int rechargeNum = recharge.getRechargeNum() + num;
			recharge.setRechargeNum(rechargeNum);
			rechargeDao.update(recharge);
		}
		rechargeLogService.save(userId, userName, num, createId, createrName);
	}
}
