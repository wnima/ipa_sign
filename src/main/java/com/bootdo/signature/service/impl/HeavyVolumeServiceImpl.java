package com.bootdo.signature.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.common.utils.R;
import com.bootdo.common.utils.UUIDUtil;
import com.bootdo.signature.dao.HeavyVolumeDao;
import com.bootdo.signature.entity.po.HeavyVolume;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.DownloadLogService;
import com.bootdo.signature.service.HeavyVolumeService;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.signature.service.RechargeService;

@Service
public class HeavyVolumeServiceImpl implements HeavyVolumeService {

	private static final Logger logger = LoggerFactory.getLogger(HeavyVolumeServiceImpl.class);
	@Autowired
	private RechargeService rechargeService;
	@Autowired
	private HeavyVolumeDao heavyVolumeDao;

	@Autowired
	private DownloadLogService downloadLogService;
	
	@Autowired
	private OriginalApplicationService originalApplicationService;
	
	@Override
	public void configuration(long appId, int enable, long startTime, long intervals, int downNum, int addNum) {
	 	HeavyVolume item = heavyVolumeDao.selectByAppId(appId);
	 	if(item == null) {
	 		item = new HeavyVolume();
	 	}
 		item.setAddNum(addNum);
 		item.setAppId(appId);
 		item.setDownNum(downNum);
 		item.setEnable(enable);
 		item.setIntervals(intervals);
 		item.setStartTime(startTime * 60 * 60 * 1000);
 		if(item.getId() == null) {
 			heavyVolumeDao.insert(item);
 		}else {
 			heavyVolumeDao.update(item);
 		}
	}

	private HeavyVolume getGlobalitem() {
		HeavyVolume globalitem = heavyVolumeDao.selectByAppId(0);
		if(globalitem == null) {
			globalitem = new HeavyVolume();
			globalitem.setAddNum(1);
			globalitem.setAppId(0L);
			globalitem.setDownNum(2);
			globalitem.setEnable(1);
			globalitem.setIntervals(60L);
			globalitem.setStartTime(System.currentTimeMillis());
	 		heavyVolumeDao.insert(globalitem);
		}
		return globalitem;
	}
	
	@Override
	public void addUseNumber(long appId,long userId) {
		logger.info("increment appId:{} userId:{}",appId,userId);
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(appId);
		if(app == null) {
			return;
		}

		// 获取全局配置
		HeavyVolume globalitem = getGlobalitem();
		
		// 全局禁用
		if(globalitem.getEnable() != 1) {
			return;
		}
		
		// 应用配置 如果没有或未开启就
		HeavyVolume item = heavyVolumeDao.selectByAppId(appId);
		if(item == null || item.getEnable() == 0) {
			item = globalitem;
		}
		
		// 单个禁用
		if(item.getEnable() == 2) {
			return;
		}
		
		// 增加数量不能小于等于0
		if(item.getAddNum() <= 0) {
			return;
		}
		
		long createTime = app.getCreateTime() * 1000;
		long time = System.currentTimeMillis();
		logger.info("increment startTime:{} {} item:{}",createTime + item.getStartTime(),time > createTime + item.getStartTime(),item);
		if(time > createTime + item.getStartTime()) {
			long fakecount = downloadLogService.fakeLastDownloadLogByAppId(appId,time - (item.getIntervals() * 60 * 1000),time);
			long realcount = downloadLogService.realDistinctCountByAppId(appId,time - (item.getIntervals() * 60 * 1000),time);
			logger.info("increment count:{} {}",realcount,realcount >= item.getDownNum());
			if(fakecount == 0 && realcount >= item.getDownNum()) {
				boolean isSuccess = rechargeService.consume(appId,userId,item.getAddNum(),false);
				logger.info("increment isSuccess:{}",isSuccess);
				if(isSuccess) {
					for(int i = 0; i < item.getAddNum(); i++) {						
						downloadLogService.save(appId, UUIDUtil.udid(), userId,false);
					}
				}	
			}
		}
	}

	@Override
	public R getConfig(int appId) {
		R r = R.ok();
		Map<String,Object> bean = new HashMap<>();
		HeavyVolume item = heavyVolumeDao.selectByAppId(appId);
		if(item == null) {
			configuration(appId, 0, 0, 60, 10, 1);
			item = heavyVolumeDao.selectByAppId(appId);
		}
		bean.put("starttime", item.getStartTime() / 60 / 60 / 1000);
		bean.put("intervals", item.getIntervals());
		bean.put("downnum", item.getDownNum());
		bean.put("addnum", item.getAddNum());
		bean.put("id", item.getAppId());
		bean.put("enable", item.getEnable());
		r.put("data", bean);
		return r;
	}

	@Override
	public HeavyVolume getConfigAndInit(int appId) {
		HeavyVolume item = heavyVolumeDao.selectByAppId(appId);
		if(item == null) {
			configuration(appId, 0, 0 , 60, 10, 1);
			item = heavyVolumeDao.selectByAppId(appId);
		}
		return item;
	}
}
