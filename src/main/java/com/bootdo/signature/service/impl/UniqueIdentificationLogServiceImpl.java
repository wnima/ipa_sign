package com.bootdo.signature.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bootdo.signature.dao.UniqueIdentificationLogDao;
import com.bootdo.signature.entity.po.UniqueIdentificationLog;
import com.bootdo.signature.service.UniqueIdentificationLogService;

@Service("udidService")
public class UniqueIdentificationLogServiceImpl implements UniqueIdentificationLogService {
	@Autowired
	private UniqueIdentificationLogDao dao;
	
	public UniqueIdentificationLog getUDIDById(String id) {
		Optional<UniqueIdentificationLog> op = dao.selectByUdid(id).stream().findFirst();
		return op.isPresent() ? op.get() : null;
	} 

	@Override
	public void delete(String udid) {
		dao.delete(udid);
	}

	@Override
	public void save(UniqueIdentificationLog udidEntrty) {
		dao.insert(udidEntrty);
	}

	@Override
	public void update(UniqueIdentificationLog udidEntrty) {
		dao.update(udidEntrty);
	}
}
