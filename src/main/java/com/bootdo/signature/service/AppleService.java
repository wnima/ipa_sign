package com.bootdo.signature.service;

import java.io.File;
import java.util.List;

import com.bootdo.common.utils.R;
import com.bootdo.signature.entity.po.AppleAccount;

public interface AppleService {
	/**
	 * 	所有账号
	 * @return
	 */
	List<AppleAccount> list();
	
	/**
	 * 	添加
	 * @param account
	 */
	void add(AppleAccount account);
	

	/**
	 * 	添加
	 * @param account
	 */
	void update(AppleAccount account);

	/**
	 * 	添加
	 * @param account
	 */
	String statue(long id);
	
	/**
	 * 	登录
	 * @param id
	 */
	boolean login(long id);
	
	/**
	 *	验证
	 * @param code
	 * @return
	 */
	boolean verification(long id,String code);
	
	/**
	 * 	启用账号
	 * @param id
	 */
	boolean enable(long id,boolean state);

	AppleAccount getEnableAccountAndLock();

	AppleAccount findAccount(long accountId);

	void accountUnlock(AppleAccount account);

	R batchAdd(File xlsxfile);
}
