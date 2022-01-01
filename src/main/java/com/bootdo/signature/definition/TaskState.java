package com.bootdo.signature.definition;

public enum TaskState {
	INIT(0),				// 初始化
	CHECK(20),				// 检查完成
	GET_CONFIGURATION(40),	// 获取描述文件完成 
	SIGN_START(41),			// 开始签名 
	SIGN_END(60), 			// 包签名完成
	UPLOAD_PACKAGE(80), 	// 上传包
	COMPLETE(100), 			// 完成
	FAIL(-1),;				// 失败
	private Integer code;

	private TaskState(int key) {
		this.code = key;
	}

	public int getKey() {
		return code;
	}
	
	public String getKeyToString() {
		return code.toString();
	}
	
	public boolean isEquelsCode(Object obj) {
		if(obj == null) {
			return false;
		}
		
		return code.toString().equals(obj.toString());
	}
}