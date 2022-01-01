package com.bootdo.signature.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolUtil {
	
	private static ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
	private static ExecutorService fixedExecutorService = Executors.newFixedThreadPool(5);
	
	public static ExecutorService getSingleExecutorService() {
		return singleExecutorService;
	}
	
	public static ExecutorService getFixedExecutorService() {
		return fixedExecutorService;
	}
}
