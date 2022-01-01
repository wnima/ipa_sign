package com.bootdo.signature.service;

import java.util.Map;

public interface PurchaseHistoryService {
	Map<String, Object> list(Integer flag,Integer num,Long userId);
}
