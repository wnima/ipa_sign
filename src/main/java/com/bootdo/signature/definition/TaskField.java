package com.bootdo.signature.definition;

public class TaskField {
	/**
	 * 任务异常
	 */
	public static final String EXCEPTION = "exception"; 
	/**
	 * 任务进度
	 */
	public static final String PROGRESS = "progress"; 
	/**
	 * OSS访问Token
	 */
	public static final String OSS_TOKEN = "oss_token"; 
	
	public static String getTaskKey(Object udid,Object appId) {
		return ("TASK:" + appId + ":" + udid).toUpperCase();
	}
	
}
