package com.bootdo.signature.service.impl;
import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.utils.ExecTools;
import com.bootdo.common.utils.FileTools;
import com.bootdo.common.utils.FileUtil;
import com.bootdo.common.utils.OssTools;
import com.bootdo.common.utils.ExecTools.ExecResult;
import com.bootdo.signature.dao.AppleAccountDao;
import com.bootdo.signature.dao.SignaturePackageDao;
import com.bootdo.signature.definition.AppType;
import com.bootdo.signature.definition.TaskErrorCode;
import com.bootdo.signature.definition.TaskField;
import com.bootdo.signature.definition.TaskState;
import com.bootdo.signature.entity.po.AppQuota;
import com.bootdo.signature.entity.po.AppleAccount;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.entity.po.UniqueIdentificationLog;
import com.bootdo.signature.exception.APIException;
import com.bootdo.signature.service.SignatureService;
import com.bootdo.signature.service.AppQuotaService;
import com.bootdo.signature.service.AppleService;
import com.bootdo.signature.service.DownloadLogService;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.signature.service.RechargeService;
import com.bootdo.signature.service.UniqueIdentificationLogService;
import com.bootdo.signature.util.Authorize;
import com.bootdo.signature.util.ITSUtils;
import com.bootdo.signature.util.ThreadPoolUtil;

import cn.hutool.core.lang.Pair;

@Service("signatureService")
public class SignatureServiceImpl implements SignatureService {
	private static final Logger logger = LoggerFactory.getLogger(SignatureServiceImpl.class);
	
	@Autowired
	private OriginalApplicationService originalApplicationService;
	@Autowired
	private UniqueIdentificationLogService udidService;
	@Autowired
	private AppleAccountDao appleAccountDao;
	@Autowired
	private AppleService appleService;
	@Autowired
	private SignaturePackageDao signaturePackageDao;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private OssTools ossTools;

	@Autowired
	private DownloadLogService downloadLogService;
	
	@Autowired
	private RechargeService rechargeService;

	@Autowired
	private AppQuotaService appQuotaService;
	
	@Autowired
	private BootdoConfig bootdoConfig;

	@Value("${signature.scriptPath}")
	public String scriptPath = null;
	
	@Value("${signature.updateDescription}")
	public String updateDescription = null;
	
	@Value("${signature.signature}")
	public String signature = null;

	@Async
	public void exec(String key, Long appId, String udid) {
		logger.info("exec {}", key);
		try {
			OriginalApplication originalApplication = originalApplicationService.getOriginalApplicationById(appId);
			UniqueIdentificationLog udidEntrty = udidService.getUDIDById(udid);
			
			File mobileprovisionFile = new File(bootdoConfig.getMobPath(),FileUtil.renameToUUID("mobileprovision"));
			FileUtils.writeByteArrayToFile(mobileprovisionFile, Base64.getDecoder().decode(udidEntrty.getMobileprovision()));
			
			AppleAccount appleAccount = appleAccountDao.selectAccountById(udidEntrty.getAccountId());
			
			String targetDirPath = originalApplication.getFilePath() + ".d/";
			String targetFilePath = targetDirPath + UUID.randomUUID().toString() + ".ipa";
			FileTools.checkDirectoryPath(targetDirPath);
			String newSignature = signature.replace("&&certificate", appleAccount.getCertificate())
					.replace("&&key", appleAccount.getKeyOfCertificate())
					.replace("&&mobileprovision", mobileprovisionFile.getAbsolutePath())
					.replace("&&resigned",targetFilePath)
					.replace("&&original", originalApplication.getFilePath());
			ExecResult commndResult = ExecTools.exec(newSignature);
			logger.info("{}",commndResult);
			
			logger.info("开始上传文件:{}",targetFilePath);
			String fileName = "temp_"+UUID.randomUUID().toString()+".ipa";
			ossTools.uploadIPAFile(fileName,targetFilePath);

			logger.info("上传文件成功:{}",targetFilePath);
			signaturePackageDao.insertSignaturePackage(udid, appId, fileName);
			stringRedisTemplate.opsForHash().put(key, TaskField.PROGRESS, TaskState.COMPLETE.getKeyToString());
			Files.delete(new File(targetFilePath).toPath());
		} catch (Exception e) {
			e.printStackTrace();
			stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION, TaskErrorCode.SIGN_ERROR.getCodeToString());
		}
	}

	/**
	 * 添加任务，主要生成任务KEY
	 */
	public void addTask(String taskKey) {
		Object progress = stringRedisTemplate.opsForHash().get(taskKey, TaskField.PROGRESS);
		// 避免重复请求，覆盖
		if(progress == null) {
			stringRedisTemplate.opsForHash().put(taskKey, TaskField.PROGRESS,TaskState.INIT.getKeyToString());
			stringRedisTemplate.opsForHash().put(taskKey, TaskField.EXCEPTION,TaskErrorCode.NO_ERROR.getCodeToString());
		}
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void insertDeviceProfile(String key,String udid,OriginalApplication application) {
		// 没有账户则获取可用的账户获取描述文件
		AppleAccount account = null;
		try {
			account = appleService.getEnableAccountAndLock();
			if(account == null) {
				logger.error("udidEntrty is null not Apple Account");
				stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.NO_ACCOUNT.getCodeToString());
				return;
			}
			UniqueIdentificationLog newudidEntrty = new UniqueIdentificationLog();
			newudidEntrty.setAccountId(account.getId());
			newudidEntrty.setUdid(udid);
			
			boolean contain = downloadLogService.isContainByUserIdAndUdid(application.getUserId(),udid);
			if(contain) {
				downloadLogService.changeUdidByUdid(udid,udid+"_"+System.currentTimeMillis()+"_black");
			}
			
			// 消耗
			boolean isSuccess = rechargeService.consume(application.getAppId(),application.getUserId(),1,true);
			logger.info("insertDeviceProfile consume:{} key:{} udid:{} appId:{}",isSuccess,key,udid,application.getAppId());
			if(!isSuccess) {
				stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.BALANCE_NOT_ENOUGH.getCodeToString());
				logger.error("udidEntrty is null not Apple Account");
				return; 
			}
			
			logger.info("insertDeviceProfile request start key:{} udid:{} appId:{}",key,udid,application.getAppId());
			Authorize authorize = new Authorize(account.getP8(),account.getIss(),account.getKid(),account.getBundleId(),account.getCerId(),account.getCerType());
			Pair<Integer, String> mobPair = ITSUtils.addDeviceAndInsertProfile(udid, authorize);
			if(mobPair == null) {
				stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.GET_PROFILE_ERROR.getCodeToString());
				return;
			}
			
			account.setRegisterNumber(mobPair.getKey());
			newudidEntrty.setMobileprovision(mobPair.getValue());
			UniqueIdentificationLog oldudidEntrty = udidService.getUDIDById(udid);
			if(oldudidEntrty == null) {				
				udidService.save(newudidEntrty);
			}else {
				udidService.update(newudidEntrty);
			}
			stringRedisTemplate.opsForHash().put(key, TaskField.PROGRESS,TaskState.GET_CONFIGURATION.getKeyToString());
			logger.info("insertDeviceProfile end key:{} udid:{} appId:{}",key,udid,application.getAppId());
		}catch (APIException e) {
			switch (e.getStatus()) {
			case APIException.REQ_PARAMS_ERROR:
				break;
			case APIException.UNEXPECTED_ERROR:
				break;
			case APIException.NOT_AUTHORIZED:				
				account.setBlockade(true);
				break;
			default:
				break;
			}
			logger.error("insertDeviceProfile exception:{}",e.toString());
		}finally {
			if(account != null) {
				appleService.accountUnlock(account);
			}
		}
	}
	
	/**
	 * 添加设备，@Async使当前进行异步处理，处理过程放入SingleExecutorService线程队列中处理，避免相同账号同时请求
	 */
	@Override
	@Async
	public void addDevice(String key,Long appId, String udid) {
		OriginalApplication application = originalApplicationService.getOriginalApplicationById(appId);
		if (application == null) {
			stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.APP_NOT_FIND.getCodeToString());
			return;
		}
		
		// 是否下架  非个签
		if((application.getEnable() != null && !application.getEnable()) || !AppType.PERSONAL.equals(AppType.getByValue(application.getAppType()))) {
			stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.APP_NO_ENABLE_OR_NO_PERSONAL.getCodeToString());
			return;
		}
		
		// 删除安装记录
		signaturePackageDao.deleteByUdid(udid);

		// 配额是否充足
		// 当前应用是否设置配额
		AppQuota appQuota = appQuotaService.findByAppId(application.getAppId());
		// 判断配额是否满了
		if(appQuota != null) {
			int limit = appQuota.getMaxNum();
			int num = appQuota.getUsedNum();
			if(num >= limit) {
				stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.QUOTA_MAX.getCodeToString());
				return;
			}
		}

		// 判断UDID是否有对应的账户
		UniqueIdentificationLog udidEntrty = udidService.getUDIDById(udid);
		logger.info("addDevice udidEntrty:{} key:{} udid:{} appId:{}",udidEntrty!=null,key,udid,appId);
		AppleAccount account = null;
		if(udidEntrty != null) {			
			account = appleService.findAccount(udidEntrty.getAccountId());
		}
		
		// 有关联账户
		if (udidEntrty != null && account != null) {
			Authorize authorize = new Authorize(account.getP8(),account.getIss(),account.getKid(),account.getBundleId(),account.getCerId(),account.getCerType());
			boolean blockade = ITSUtils.isBlockade(authorize);
			// 关联账户被封
			if(blockade) {
				ThreadPoolUtil.getSingleExecutorService().execute(()->{
					udidService.delete(udid);
					downloadLogService.changeUdidByUdid(udid,udid+"_"+System.currentTimeMillis()+"_black");
					insertDeviceProfile(key, udid, application);
					// 配额加1
					if(appQuota != null) {
					 	Integer num = appQuota.getUsedNum();
					 	appQuotaService.updateNumById(appQuota.getId(), ++num);
					}
					// 记录
					downloadLogService.save(application.getAppId(), udid, application.getUserId(),true);
				});
				return;
			}
			// 没封
			try {
				Pair<Integer, String> mobPair = ITSUtils.addDeviceAndInsertProfile(udid, authorize);
				udidEntrty.setMobileprovision(mobPair.getValue());
				udidService.update(udidEntrty);
				boolean contain = downloadLogService.isContainByUserIdAndUdid(application.getUserId(),udid);
				if(!contain) {
					boolean isSuccess = rechargeService.consume(application.getAppId(),application.getUserId(),1,true);
					if(!isSuccess) {
						stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.BALANCE_NOT_ENOUGH.getCodeToString());
						return; 
					}
				}
				// 记录
				downloadLogService.save(application.getAppId(), udid, application.getUserId(),true);
				stringRedisTemplate.opsForHash().put(key, TaskField.PROGRESS,TaskState.GET_CONFIGURATION.getKeyToString());
			}catch (APIException e) {
				switch (e.getStatus()) {
				case APIException.REQ_PARAMS_ERROR:
					
					break;
				case APIException.UNEXPECTED_ERROR:
					
					break;
				case APIException.NOT_AUTHORIZED:				
					account.setBlockade(true);
					break;
				default:
					break;
				}
				appleService.accountUnlock(account);
				stringRedisTemplate.opsForHash().put(key, TaskField.EXCEPTION,TaskErrorCode.GET_PROFILE_ERROR.getCodeToString());
			}
		} else {
			// 没有关联账户,添加新的关联账户
			ThreadPoolUtil.getSingleExecutorService().execute(()->{
				if(udidEntrty != null) {
					downloadLogService.changeUdidByUdid(udid,udid+"_"+System.currentTimeMillis()+"_black");
				}
				insertDeviceProfile(key, udid, application);
				// 配额加1
				if(appQuota != null) {
				 	Integer num = appQuota.getUsedNum();
				 	appQuotaService.updateNumById(appQuota.getId(), ++num);
				}
				// 记录
				downloadLogService.save(application.getAppId(), udid, application.getUserId(),true);
			});
		}
		
	}
}
