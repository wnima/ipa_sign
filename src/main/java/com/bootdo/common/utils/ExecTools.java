package com.bootdo.common.utils;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bootdo.signature.service.SignatureService;

public class ExecTools{

	private static final Logger logger = LoggerFactory.getLogger(SignatureService.class);
	public static class ExecResult{
		private String info;
		private String error;
		private boolean isExecSuc;
		public String getInfo() {
			return info;
		}
		public void setInfo(String info) {
			this.info = info;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
		public boolean isExecSuc() {
			return isExecSuc;
		}
		public void setExecSuc(boolean isExecSuc) {
			this.isExecSuc = isExecSuc;
		}
		@Override
		public String toString() {
			return "ExecResult [info=" + info + ", error=" + error + ", isExecSuc=" + isExecSuc + "]";
		}
		
	}
	public static ExecResult exec(String commnd) throws IOException, InterruptedException {
		ExecResult result = new ExecResult();
		logger.info("exec {}",commnd);
		Process process = Runtime.getRuntime().exec(commnd);
		process.waitFor();
		// 读取错误消息
		InputStream es = process.getErrorStream();
		int len = 1024;
		byte[] b = new byte[len];
		StringBuilder builder = new StringBuilder();
		while((len = es.read(b)) != -1) {
			builder.append(new String(b, 0, len));
		}

		result.error = builder.toString();
		result.isExecSuc = builder.length() == 0;
		
		// 读取消息
		InputStream is = process.getInputStream();
		len = 1024;
		b = new byte[len];
		builder.delete(0, builder.length());
		while((len = is.read(b)) != -1) {
			builder.append(new String(b, 0, len));
		}
		result.info = builder.toString();
		return result;
	}
	
	public static Object createUdidMobileConfig(String appid) throws IOException, InterruptedException {
		return null;
		
	}
}