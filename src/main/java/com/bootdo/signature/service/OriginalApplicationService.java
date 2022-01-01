package com.bootdo.signature.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.bootdo.common.utils.R;
import com.bootdo.signature.entity.po.OriginalApplication;

public interface OriginalApplicationService {

	public OriginalApplication getOriginalApplicationById(Long id);
	
	public OriginalApplication getOriginalApplicationByDownloadCode(String code);

	public void save(OriginalApplication appinfo);
	
	public void update(OriginalApplication appinfo);

	public R getOriginalApplicationByOwnerId(Long userId,String username, Integer flag, Integer num, String s);

	public void delete(OriginalApplication app);

	public OriginalApplication getOriginalApplicationByShortDomain(String turl);

	public List<OriginalApplication> list();

	public R list(int flag, int num,String search);
	
	public long count();

	public R addApplication(File file,long userId);
	
	public R updateApplication(File file, long appId);

	public Map<String, Object> getAppInfoById(Long appId,String ip,String sessionId);
	
	public Map<String, Object> getAppInfoByCode(String code,String ip,String sessionId);

	public void updateDownloadNum(Long appId,int addNum,boolean real);

	public List<Map<String,Object>> getOriginalApplicationByUserId(Long userId);
}
