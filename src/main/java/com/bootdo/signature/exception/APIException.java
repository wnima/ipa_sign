package com.bootdo.signature.exception;

public class APIException extends Exception  {
	private static final long serialVersionUID = -6159078372416928883L;
	
	/**
	 * 	请求参数错误
	 */
	public static final String REQ_PARAMS_ERROR = "ENTITY_ERROR.ATTRIBUTE.INVALID";

	/**
	 * 	意外错误
	 */
	public static final String UNEXPECTED_ERROR = "UNEXPECTED_ERROR";
	/**
	 * 	可能被封
	 */
	public static final String NOT_AUTHORIZED = "NOT_AUTHORIZED";
	
	private String code;
	public APIException(String code,String arg0) {
		super(arg0);
		this.code = code;
	}
	
	public String getStatus() {
		return code;
	}
}
