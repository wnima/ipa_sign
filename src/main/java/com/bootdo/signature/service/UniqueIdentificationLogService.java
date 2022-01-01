package com.bootdo.signature.service;

import com.bootdo.signature.entity.po.UniqueIdentificationLog;

public interface UniqueIdentificationLogService {
	public UniqueIdentificationLog getUDIDById(String id);

	public void delete(String udid);

	public void save(UniqueIdentificationLog udidEntrty);
	
	public void update(UniqueIdentificationLog udidEntrty);
}
