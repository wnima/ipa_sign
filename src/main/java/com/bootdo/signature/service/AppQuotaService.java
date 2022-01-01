package com.bootdo.signature.service;

import com.bootdo.signature.entity.po.AppQuota;

public interface AppQuotaService {
	// 查询
	public AppQuota findByAppId(long appId);
	// 更新配额限制limit
	public boolean updateLimitById(int id,int limit);
	// 更新配额数量num
	public boolean updateNumById(int id,int num);
	// 删除
	public boolean deleteById(int id);
	// 保存
	public void save(AppQuota appQuota);
}
