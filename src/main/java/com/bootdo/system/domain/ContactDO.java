package com.bootdo.system.domain;

import java.io.Serializable;
import java.util.Date;



/**
 * 
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2020-04-27 14:40:25
 */
public class ContactDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Integer id;
	//微信1|QQ2
	private String type;
	//号码
	private String value;
	//联系客服(是:1 否:0)
	private Integer main;
	//显示位置(1:右边2:底部)
	private Integer pos;
	//图片URL
	private String img;

	/**
	 * 设置：
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * 获取：
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * 设置：微信1|QQ2
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取：微信1|QQ2
	 */
	public String getType() {
		return type;
	}
	/**
	 * 设置：号码
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * 获取：号码
	 */
	public String getValue() {
		return value;
	}
	/**
	 * 设置：联系客服(是:1 否:0)
	 */
	public void setMain(Integer main) {
		this.main = main;
	}
	/**
	 * 获取：联系客服(是:1 否:0)
	 */
	public Integer getMain() {
		return main;
	}
	/**
	 * 设置：显示位置(1:右边2:底部)
	 */
	public void setPos(Integer pos) {
		this.pos = pos;
	}
	/**
	 * 获取：显示位置(1:右边2:底部)
	 */
	public Integer getPos() {
		return pos;
	}
	/**
	 * 设置：图片URL
	 */
	public void setImg(String img) {
		this.img = img;
	}
	/**
	 * 获取：图片URL
	 */
	public String getImg() {
		return img;
	}
}
