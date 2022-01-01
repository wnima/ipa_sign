package com.bootdo.signature.definition;

public enum TaskErrorCode {
	NO_ERROR(-1),							// 没错误
	NO_ACCOUNT(200),						// 无可用账户
	GET_PROFILE_ERROR(300),					// 获取描述文件失败
	APP_NOT_FIND(400), 						// 应用未找到
	SIGN_ERROR(800), 						// 签名错误
	APP_NO_ENABLE_OR_NO_PERSONAL(500), 		// 应用未启用或非超级签
	QUOTA_MAX(600), 						// 配额满
	BALANCE_NOT_ENOUGH(700),  				// 次数不足
	;
	private Integer code;
	private TaskErrorCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	
	public String getCodeToString() {
		return code.toString();
	}
	
	public boolean isEquelsCode(Object obj) {
		if(obj == null) {
			return false;
		}
		
		return code.toString().equals(obj.toString());
	}
}
