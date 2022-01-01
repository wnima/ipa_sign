package com.bootdo.system.domain;

import java.io.Serializable;
import java.math.BigDecimal;



/**
 * 充值订单表
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2020-04-22 14:36:08
 */
public class OrderDO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//流水号
	private Integer rid;
	//订单号
	private String orderid;
	//充值用户ID
	private Integer uid;
	//用户名
	private String uname;
	//-1非法，0默认，1为创建订单成功，2为创建订单成功但第三方创建失败，3为支付完成
	private Integer status;
	//第三方订单号
	private String otherid;
	//价格
	private BigDecimal price;
	//数量
	private Integer num;
	//备注
	private String remark;
	//创建订单时间
	private Integer createTime;
	//订单状态更新时间
	private Integer mtime;

	public String getShowTime() {
		long showTime = (long)this.getMtime() * 1000;
		return new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(showTime));
	}
	
	public String getShowStatus() {
		String showStatus = "未支付";
		if(this.status == 3) {
			showStatus = "支付完成";
		}
		return showStatus;
	}
	
	/**
	 * 设置：流水号
	 */
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	/**
	 * 获取：流水号
	 */
	public Integer getRid() {
		return rid;
	}
	/**
	 * 设置：订单号
	 */
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	/**
	 * 获取：订单号
	 */
	public String getOrderid() {
		return orderid;
	}
	/**
	 * 设置：充值用户ID
	 */
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	/**
	 * 获取：充值用户ID
	 */
	public Integer getUid() {
		return uid;
	}
	/**
	 * 设置：用户名
	 */
	public void setUname(String uname) {
		this.uname = uname;
	}
	/**
	 * 获取：用户名
	 */
	public String getUname() {
		return uname;
	}
	/**
	 * 设置：-1非法，0默认，1为创建订单成功，2为创建订单成功但第三方创建失败，3为支付完成
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}
	/**
	 * 获取：-1非法，0默认，1为创建订单成功，2为创建订单成功但第三方创建失败，3为支付完成
	 */
	public Integer getStatus() {
		return status;
	}
	/**
	 * 设置：第三方订单号
	 */
	public void setOtherid(String otherid) {
		this.otherid = otherid;
	}
	/**
	 * 获取：第三方订单号
	 */
	public String getOtherid() {
		return otherid;
	}
	/**
	 * 设置：价格
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	/**
	 * 获取：价格
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * 设置：数量
	 */
	public void setNum(Integer num) {
		this.num = num;
	}
	/**
	 * 获取：数量
	 */
	public Integer getNum() {
		return num;
	}
	/**
	 * 设置：备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 获取：备注
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 设置：创建订单时间
	 */
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	/**
	 * 获取：创建订单时间
	 */
	public Integer getCreateTime() {
		return createTime;
	}
	/**
	 * 设置：订单状态更新时间
	 */
	public void setMtime(Integer mtime) {
		this.mtime = mtime;
	}
	/**
	 * 获取：订单状态更新时间
	 */
	public Integer getMtime() {
		return mtime;
	}
}
