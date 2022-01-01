package com.bootdo.signature.definition;

/**
 * 应用类型
 * @author abc
 */
public enum AppType {
	/**
	 * 超级签
	 */
	PERSONAL("超级签",1),
	/**
	 * 企业签
	 */
	ENTERPRISE("企业签",2),
	/**
	 * 安卓
	 */
	ANDROID("安卓",3),
	/**
	 * 未知
	 */
	UNKNOWN("未知",-1),
	;

	private String name;
	private Integer value;
	
	AppType(String name,Integer value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Integer getValue() {
		return value;
	}

	public static AppType getByValue(Integer value) {
		for(AppType val:values()) {
			if(val.value == value) {
				return val;
			}
		}
		return AppType.UNKNOWN;
	}
}
