package com.bootdo.signature.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.utils.ExcelUtil;
import com.bootdo.common.utils.R;
import com.bootdo.signature.dao.AppleAccountDao;
import com.bootdo.signature.entity.po.AppleAccount;
import com.bootdo.signature.service.AppleService;
import com.bootdo.signature.util.Authorize;
import com.bootdo.signature.util.FileUtil;
import com.bootdo.signature.util.ITSUtils;
import com.bootdo.signature.util.ThreadPoolUtil;

@Service
public class AppleServiceImpl implements AppleService {

	@Autowired
	private AppleAccountDao appleAccountDao;

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private BootdoConfig bootdoConfig;

	private static final Logger logger = LoggerFactory.getLogger(AppleServiceImpl.class);

	@Override
	public List<AppleAccount> list() {
		return appleAccountDao.selectAccountList();
	}

	@Override
	public void add(AppleAccount account) {
		appleAccountDao.insert(account);
	}


	@Override
	public void update(AppleAccount account) {
		appleAccountDao.updateAccount(account);
	}

	
	@Override
	public boolean login(long id) {
		AppleAccount account = appleAccountDao.selectAccountById(id);
		if (account != null && !"3".equals(account.getLoginstate())) {
			String accountkey = account.getUsername();
			HashMap<String, String> taskInfo = new HashMap<>();
			taskInfo.put("username", account.getUsername());
			taskInfo.put("password", account.getPassword());
			stringRedisTemplate.opsForHash().putAll(accountkey, taskInfo);
			stringRedisTemplate.opsForList().leftPush(("accounttaskqueue").toUpperCase(), accountkey);
			return true;
		}
		return false;
	}


	private static final String VERIFY_CODE = "VERIFY_CODE:";
	
	@Override
	public boolean verification(long id, String code) {
		AppleAccount account = appleAccountDao.selectAccountById(id);
		if (account != null) {
			stringRedisTemplate.opsForList().rightPush(VERIFY_CODE+account.getPhoneNumber(), code);
			restTemplate.postForEntity(bootdoConfig.getLoginApiHost()+"/"+account.getUsername()+"/"+code+"/putVerifyCode","",Map.class);
			return true;
		}
		return false;
	}

	@Override
	public boolean enable(long id, boolean state) {
		AppleAccount account = appleAccountDao.selectAccountById(id);
		if (account != null) {
			try {
				if (!"4".equals(account.getLoginstate())) {
					Authorize authorize = new Authorize(account.getP8(), account.getIss(), account.getKid(),
							account.getCsr());
					logger.info("enable {}",authorize);
					Map<String, Object> accountInfo = ITSUtils.initAccount(authorize, bootdoConfig.getCerPath());
					account.setBundleId(accountInfo.get("bundleIds").toString());
					account.setPackageName(accountInfo.get("packageName").toString());
					account.setRegisterNumber(Integer.parseInt(accountInfo.get("number").toString()));
					account.setCerId(accountInfo.get("cerId").toString());
					account.setCerType(accountInfo.get("certificateType").toString());
					String publicKey = accountInfo.get("publicKey").toString();
					String privateKey = accountInfo.get("privateKey").toString();
					File publicKeyFile = new File(bootdoConfig.getCerPath(), FileUtil.renameToUUID("cer"));
					File privateKeyFile = new File(bootdoConfig.getCerPath(), FileUtil.renameToUUID("key"));
					FileUtils.writeStringToFile(publicKeyFile, publicKey, StandardCharsets.UTF_8);
					FileUtils.writeStringToFile(privateKeyFile, privateKey, StandardCharsets.UTF_8);
					account.setLoginstate("4");
					account.setCertificate(publicKeyFile.getAbsolutePath());
					account.setKeyOfCertificate(privateKeyFile.getAbsolutePath());
				}
				account.setEnable(state);
				appleAccountDao.updateAccount(account);
				return true;
			} catch (Exception e) {
				logger.error("enable id:{} state:{} exception:{}", id, state, e);
				account.setLoginstate("2");
			}
			appleAccountDao.updateAccount(account);
		}
		return false;
	}

	@Override
	public String statue(long id) {
		AppleAccount account = appleAccountDao.selectAccountById(id);
		if (account != null) {
			String accountkey = ("account:" + account.getId()).toUpperCase();
			return (String) stringRedisTemplate.opsForHash().get(accountkey, "loginstate");
		}
		return null;
	}

	@Override
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public AppleAccount getEnableAccountAndLock() {
		List<AppleAccount> accountList = appleAccountDao.selectAccountList();
		for (AppleAccount account : accountList) {
			if (account.getRegisterNumber() < 100 
					&& account.isEnable() 
					&& "4".equals(account.getLoginstate())
					&& !account.isBlockade() 
					&& !account.isUse()) {
				account.setUse(true);
				appleAccountDao.updateAccount(account);
				return account;
			}
		}
		return null;
	}

	@Override
	public AppleAccount findAccount(long accountId) {
		return appleAccountDao.selectAccountById(accountId);
	}

	@Override
	public void accountUnlock(AppleAccount account) {
		account.setUse(false);
		appleAccountDao.updateAccount(account);
	}

	@Override
	public R batchAdd(File xlsxfile) {
		R result = R.ok();
		try {
			List<List<String>> data = ExcelUtil.readExcel(new FileInputStream(xlsxfile));
			List<AppleAccount> nList = new ArrayList<>();
			List<String> failList = new ArrayList<>();
			data.forEach(item->{
				AppleAccount account = appleAccountDao.selectAccountByUsername(item.get(0));
				if(account == null) {
					AppleAccount naccount = new AppleAccount();
					naccount.setLoginstate("1");
					naccount.setUsername(item.get(0));
					naccount.setPassword(item.get(1));
					naccount.setPhoneNumber(item.get(2));
					appleAccountDao.insert(naccount);
					nList.add(naccount);
				}else {
					failList.add(account.getUsername());
				}
			});
			
			nList.forEach(i->restTemplate.postForEntity(bootdoConfig.getLoginApiHost()+"/putUnLoginAccount", i,Map.class));
			
			result.put("count", data.size());
			result.put("faillist", failList);
			result.put("failcount", failList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public R batchAddBak(File xlsxfile) {
		R result = R.ok();
		try {
			List<List<String>> data = ExcelUtil.readExcel(new FileInputStream(xlsxfile));
			List<AppleAccount> nList = new ArrayList<>();
			List<String> failList = new ArrayList<>();
			data.forEach(item->{
				AppleAccount account = appleAccountDao.selectAccountByUsername(item.get(0));
				if(account == null) {
					AppleAccount naccount = new AppleAccount();
					naccount.setLoginstate("1");
					naccount.setUsername(item.get(0));
					naccount.setIss(item.get(1));
					naccount.setKid(item.get(2));
					naccount.setP8(item.get(3).replaceFirst("-----BEGIN PRIVATE KEY-----\n", "").replaceFirst("\n-----END PRIVATE KEY-----", ""));
					appleAccountDao.insert(naccount);
					nList.add(naccount);
				}else {
					failList.add(account.getUsername());
				}
			});
			
			nList.forEach(i->ThreadPoolUtil.getSingleExecutorService().execute(()->enable((int)i.getId(), true)));
			
			result.put("count", data.size());
			result.put("faillist", failList);
			result.put("failcount", failList.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
