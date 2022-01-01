package com.bootdo.signature.service;

import com.bootdo.signature.entity.po.VisitLog;

public interface VisitLogService {
	long getToDayPV(long appId);
	long getToDayUV(long appId);
	long getToDayIV(long appId);
	void save(VisitLog log);
}
