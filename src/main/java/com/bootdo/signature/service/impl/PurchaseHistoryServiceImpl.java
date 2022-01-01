package com.bootdo.signature.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.signature.dao.DownloadLogDao;
import com.bootdo.signature.dao.OriginalApplicationDao;
import com.bootdo.signature.entity.po.DownloadLog;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.PurchaseHistoryService;

@Service
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {

	@Autowired
	DownloadLogDao downloadLogDao;
	
	@Autowired
	OriginalApplicationDao originalApplicationDao;
	
	// 消费记录
	@Override
	public Map<String, Object> list(Integer flag, Integer num,Long userId) {
		int start = 0;
		if(num == 0) {
			num = 10;
		}
		List<DownloadLog> logList = downloadLogDao.limitSelectByUserIdAndDistinct(userId, start, num);
		long count = downloadLogDao.selectCountByUserIdAndDistinctUdid(userId);
		Map<String, Object> data = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(DownloadLog log:logList) {
			OriginalApplication app = originalApplicationDao.selectById(log.getAppId());
			HashMap<String, Object> row = new HashMap<>();
			row.put("appName", app == null ? "(应用已删除)":app.getApplicationName());
			row.put("package", app == null ? "(应用已删除)":app.getPackageName());
			row.put("udid", log.getUdid());
			row.put("details", "设备签名");
			row.put("type", app == null ? "(应用已删除)":app.getAppType());
			row.put("time", new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(log.getDownloadTime())));
			list.add(row);
		}
		data.put("list", list);
		data.put("count", count);
		return data;
	}

}
