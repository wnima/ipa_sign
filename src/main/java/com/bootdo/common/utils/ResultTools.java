package com.bootdo.common.utils;

import java.util.HashMap;
import java.util.Map;

public class ResultTools {
	public static final Map<String,Object> result_ok(){
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("code", 200);
		return result;
	}

	public static Map<String, Object> paramsError() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("code", 401);
		result.put("msg", "缺少参数");
		return result;
	}
}
