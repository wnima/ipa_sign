package com.bootdo.signature.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.bootdo.common.utils.IPUtils;
import com.bootdo.common.utils.OssTools;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.UUIDUtil;
import com.bootdo.signature.config.UDIDConfig;
import com.bootdo.signature.dao.AndroidDownloadDao;
import com.bootdo.signature.dao.OriginalApplicationDao;
import com.bootdo.signature.dao.SignaturePackageDao;
import com.bootdo.signature.definition.AppType;
import com.bootdo.signature.entity.po.AndroidDownload;
import com.bootdo.signature.entity.po.AppQuota;
import com.bootdo.signature.entity.po.EnterprisePackage;
import com.bootdo.signature.entity.po.HeavyVolume;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.entity.po.VisitLog;
import com.bootdo.signature.service.AppQuotaService;
import com.bootdo.signature.service.DownloadLogService;
import com.bootdo.signature.service.EnterprisePackageService;
import com.bootdo.signature.service.HeavyVolumeService;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.signature.service.RechargeLogService;
import com.bootdo.signature.service.RechargeService;
import com.bootdo.signature.service.SignatureService;
import com.bootdo.signature.service.VisitLogService;
import com.bootdo.signature.util.IpaFile;
import com.bootdo.signature.util.IpaFile.InfoPlist;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.UserService;
import com.dd.plist.PropertyListFormatException;

@Service("originalApplicationService")
public class OriginalApplicationServiceImpl implements OriginalApplicationService {
	@Autowired
	private OriginalApplicationDao originalApplicationDao;

	@Autowired
	private DownloadLogService downloadLogService;

	@Autowired
	private OssTools ossTools;

	@Autowired
	private UserService userService;

	@Autowired
	private AppQuotaService appQuotaService;

	@Autowired
	private RechargeService rechargeService;
	@Autowired
	private SignaturePackageDao signaturePackageDao;
	
	@Autowired
	private UDIDConfig udidConfig;

	@Autowired
	private EnterprisePackageService enterprisePackageService;
	@Autowired
	private VisitLogService visitLogService;

	@Autowired
	private HeavyVolumeService heavyVolumeService;
	
	@Autowired
	private AndroidDownloadDao androidDownloadDao;

	public OriginalApplication getOriginalApplicationById(Long id) {
		return originalApplicationDao.selectById(id);
	}
	
	public OriginalApplication getOriginalApplicationByDownloadCode(String code) {
		return originalApplicationDao.selectByDownloadCode(code);
	}

	public void save(OriginalApplication appinfo) {
		if (appinfo.getAppId() == null) {
			appinfo.setCreateTime(System.currentTimeMillis() / 1000);
			originalApplicationDao.save(appinfo);
		} else {
			originalApplicationDao.update(appinfo,0,0);
		}
	}

	public R getOriginalApplicationByOwnerId(Long userId, String username, Integer flag, Integer num, String search) {
		if(num == 0) {
			num = 20;
		}
		int start = flag * num;
		List<OriginalApplication> appList = originalApplicationDao.selectByOwnerId(userId,start, num,"%"+search+"%");
		long count = originalApplicationDao.selectCountByOwnerId(userId);
		long downloadSum = rechargeService.getUseCount(userId);
		long rechargeCount = rechargeService.getRechargeCount(userId);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (OriginalApplication app : appList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", app.getAppId());
			map.put("down_num", app.getDownloadNum());
			List<AndroidDownload> down = androidDownloadDao.select(app.getAppId());
			if(down.size() > 0) {
				map.put("android_down_num", down.get(0).getCount());
			}else {				
				map.put("android_down_num", 0);
			}
			if (!AppType.UNKNOWN.getValue().equals(app.getAppType())) {
				map.put("link", udidConfig.getHost() + "/signature/install/" + (StringUtils.isBlank(app.getDownloadCode()) ? app.getAppId() : app.getDownloadCode()) + "/appinfo");
				map.put("baklink", udidConfig.getBakhost() + "/signature/install/" + (StringUtils.isBlank(app.getDownloadCode()) ? app.getAppId() : app.getDownloadCode()) + "/appinfo");
			}
			
			if(AppType.ENTERPRISE.getValue().equals(app.getAppType())) {
				EnterprisePackage enter = enterprisePackageService.findByAppId(app.getAppId());
				map.put("status", enter.getStatus());
			}
			
			map.put("time", app.getCreateTime());
			map.put("title", app.getApplicationName());
			map.put("no", app.getVersion());
			map.put("size", app.getFileSize());
			map.put("img", app.getIconUrl());
			map.put("enable", app.getEnable());
			map.put("select", app.getType());
			map.put("num", app.getMark());
			map.put("shop", app.getOrganizationName());
			map.put("remark", app.getIntroduce());
			map.put("remarkname", StringUtils.isBlank(app.getRemarkName()) ? "" : app.getRemarkName());
			map.put("type", app.getAppType());
			map.put("showIconUrl", app.getShowIconUrl());
			map.put("appName", app.getShowAppName());
			map.put("showPicUrl", app.getScreenshots());
			map.put("udidTitle", app.getUdidTitle());
			map.put("udidDescription", app.getUdidDescription());
			map.put("appType", app.getAppType());
			map.put("installType", app.getInstartModel());
			map.put("password", app.getInstartPassword());
			map.put("androidUrl", app.getAndroidFileUrl());
			map.put("area", app.getArea());
			AppQuota appQuota = appQuotaService.findByAppId(app.getAppId());
			if (appQuota != null) {
				map.put("quota", appQuota.getMaxNum());
				map.put("quota_surplus", appQuota.getMaxNum() - appQuota.getUsedNum());
			}

			if (app.getShortDomain() != null && !"".equals(app.getShortDomain())) {
				map.put("domain", app.getShortDomain());
			}
			list.add(map);
		}

		long download = downloadLogService.distinctToDayCountByUserId(userId);

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("list", list);
		data.put("money", rechargeCount - downloadSum);
		data.put("download", download);
		data.put("user_name", username);
		data.put("count", count);
		return R.ok().put("data", data);
	}

	@Override
	public void update(OriginalApplication appinfo) {
		originalApplicationDao.update(appinfo,0,0);
	}

	@Override
	public void delete(OriginalApplication app) {
		int row = originalApplicationDao.delete(app.getAppId());
	}

	@Override
	public OriginalApplication getOriginalApplicationByShortDomain(String turl) {
		return originalApplicationDao.selectByShortDomain(turl);
	}

	@Override
	public List<OriginalApplication> list() {
		return null;
	}

	@Override
	public R list(int flag, int num,String search) {
		int start = flag * num;
		List<OriginalApplication> appList = originalApplicationDao.selectLimit(start, num,"%"+search+"%");
		HeavyVolume globalconfig = heavyVolumeService.getConfigAndInit(0);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (OriginalApplication app : appList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", app.getAppId());
			map.put("down_num", app.getDownloadNum());
			List<AndroidDownload> down = androidDownloadDao.select(app.getAppId());
			if(down.size() > 0) {
				map.put("android_down_num", down.get(0).getCount());
			}else {				
				map.put("android_down_num", 0);
			}
			map.put("realdownnum", app.getRealDownNum());
			if (!AppType.UNKNOWN.getValue().equals(app.getAppType())) {
				map.put("link", udidConfig.getHost() + "/signature/install/" + (StringUtils.isBlank(app.getDownloadCode()) ? app.getAppId() : app.getDownloadCode()) + "/appinfo");
				map.put("baklink", udidConfig.getBakhost() + "/signature/install/" + (StringUtils.isBlank(app.getDownloadCode()) ? app.getAppId() : app.getDownloadCode()) + "/appinfo");
			}
			map.put("time", app.getCreateTime());
			map.put("title", app.getApplicationName());
			map.put("no", app.getVersion());
			map.put("size", app.getFileSize());
			map.put("img", app.getIconUrl());
			map.put("enable", app.getEnable());
			map.put("select", app.getType());
			map.put("num", app.getMark());
			map.put("shop", app.getOrganizationName());
			map.put("remark", app.getIntroduce());
			map.put("remarkname", StringUtils.isBlank(app.getRemarkName()) ? "" : app.getRemarkName());
			map.put("showIconUrl", app.getShowIconUrl());
			map.put("appName", app.getShowAppName());
			map.put("showPicUrl", app.getScreenshots());
			map.put("type", app.getAppType());
			UserDO user = userService.get(app.getUserId());
			String userName = "用户已删除";
			if(user != null) {
				userName = user.getUsername();				
			}
			map.put("ownName", userName);
			map.put("ipaurl", app.getFileUrl());
			map.put("udidTitle", app.getUdidTitle());
			map.put("udidDescription", app.getUdidDescription());
			map.put("appType", app.getAppType());
			map.put("androidUrl", app.getAndroidFileUrl());
			map.put("installType", app.getInstartModel());
			map.put("password", app.getInstartPassword());
			map.put("area", app.getArea());
			// 查询放量配置
			HeavyVolume config = heavyVolumeService.getConfigAndInit(app.getAppId().intValue());
			if(globalconfig.getEnable() == 1){
				if(config != null) {
					if(config.getEnable() == 0) {
						map.put("switch", 0);
					}else if(config.getEnable() == 1) {
						map.put("switch", 1);
					}else if(config.getEnable() == 2) {
						map.put("switch", 3);
					}
				}else {					
					map.put("switch", 0);
				}
			}else {
				map.put("switch", 2);
			}
			AppQuota appQuota = appQuotaService.findByAppId(app.getAppId());
			if (appQuota != null) {
				map.put("quota", appQuota.getMaxNum());
				map.put("quota_surplus", appQuota.getMaxNum() - appQuota.getUsedNum());
			}
			
			if (app.getShortDomain() != null && !"".equals(app.getShortDomain())) {
				map.put("domain", app.getShortDomain());
			}
			if (AppType.ENTERPRISE.getValue().equals(app.getAppType())) {
				EnterprisePackage enPackage = enterprisePackageService.findByAppId(app.getAppId());
				map.put("status",enPackage.getStatus());
			}
			list.add(map);
		}

		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("list", list);
		data.put("count", count());
		return R.ok().put("data", data);
	}

	@Override
	public long count() {
		return originalApplicationDao.count();
	}

	@Override
	public R addApplication(File file, long userId) {
		OriginalApplication appinfo = new OriginalApplication();
		IpaFile ipaFile = null;
		try {
			ipaFile = new IpaFile(file);
			InfoPlist info = ipaFile.getInfo();
			// 上传文件到OSS
			String fileUrl = ossTools.uploadIMGFile(file.getName(), file.getAbsolutePath());
			// 上传ICON到OSS
			String iconUrl = "";
			if(ipaFile.getIcon() != null) {
				iconUrl = ossTools.uploadIMGFile(UUIDUtil.uuid()+".png", ipaFile.getIcon().getAbsolutePath());
			}
			
			String applicationName = "";
			if(StringUtils.isBlank(info.getCFBundleDisplayName())) {
				applicationName = info.getCFBundleName();
			}else {
				applicationName = info.getCFBundleDisplayName();
			}
			
			appinfo.setFilePath(file.getAbsolutePath());
			appinfo.setApplicationName(applicationName);
			appinfo.setPackageName(info.getCFBundleIdentifier());
			appinfo.setIconUrl(iconUrl);
			appinfo.setVersion(info.getCFBundleVersion());
			appinfo.setMinVersion(info.getMinimumOSVersion());
			appinfo.setUserId(userId);
			appinfo.setEnable(true);
			appinfo.setFileSize(file.length());
			appinfo.setFileUrl(fileUrl);
			appinfo.setAppType(AppType.PERSONAL.getValue());
			appinfo.setInstartModel(1);
			appinfo.setArea("zh-CN");
			appinfo.setInstartPassword("");
			appinfo.setUdidTitle(appinfo.getApplicationName());
			appinfo.setUdidDescription("安装" + appinfo.getApplicationName());
			appinfo.setOrganizationName("");
			// 随机8位下载码
		 	String downloadCode = null;
			while(downloadCode == null) {
		 		String code = UUIDUtil.random8();
		 		OriginalApplication app = originalApplicationDao.selectByDownloadCode(code);
		 		if(app == null) {
		 			downloadCode = code;
		 			break;
		 		}
		 	}
			
			appinfo.setDownloadCode(downloadCode);
			
			// 保存数据库
			save(appinfo);
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("id", appinfo.getAppId());
			data.put("title", appinfo.getApplicationName());
			data.put("size", appinfo.getFileSize());
			data.put("udidTitle", appinfo.getUdidTitle());
			data.put("udidDescription", appinfo.getUdidDescription());
			data.put("installType", appinfo.getInstartModel());
			data.put("password", appinfo.getInstartPassword());
			data.put("img", appinfo.getIconUrl());
			return R.ok().put("data", data);
		} catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
				| SAXException e) {
			e.printStackTrace();
			return R.error("文件解析错误");
		} finally {
			if (ipaFile != null) {
				ipaFile.deleteTempDir();
			}
		}
	}
	
	
	
	@Override
	public R updateApplication(File file, long appId) {
		OriginalApplication oriApp = getOriginalApplicationById(appId);
		if(oriApp == null) {
			return R.error("没有此应用");
		}
		IpaFile ipaFile = null;
		try {
			ipaFile = new IpaFile(file);
			InfoPlist info = ipaFile.getInfo();
			// 上传文件到OSS
			String fileUrl = ossTools.uploadIMGFile(file.getName(), file.getAbsolutePath());
			// 上传ICON到OSS
			String iconUrl = "";
			if(ipaFile.getIcon() != null) {
				iconUrl = ossTools.uploadIMGFile(UUIDUtil.uuid()+".png", ipaFile.getIcon().getAbsolutePath());
			}
			
			String applicationName = "";
			if(StringUtils.isBlank(info.getCFBundleDisplayName())) {
				applicationName = info.getCFBundleName();
			}else {
				applicationName = info.getCFBundleDisplayName();
			}
			
			oriApp.setFilePath(file.getAbsolutePath());
			oriApp.setApplicationName(applicationName);
			oriApp.setPackageName(info.getCFBundleIdentifier());
			oriApp.setIconUrl(iconUrl);
			oriApp.setVersion(info.getCFBundleVersion());
			oriApp.setMinVersion(info.getMinimumOSVersion());
			oriApp.setFileSize(file.length());
			oriApp.setFileUrl(fileUrl);
			oriApp.setUdidTitle(oriApp.getApplicationName());
			oriApp.setUdidDescription("安装" + oriApp.getApplicationName());
			// 保存数据库
			update(oriApp);
			// 删除缓存
			signaturePackageDao.deleteByAppId(appId);
			
			HashMap<String, Object> data = new HashMap<String, Object>();
			data.put("id", oriApp.getAppId());
			data.put("title", oriApp.getApplicationName());
			data.put("size", oriApp.getFileSize());
			data.put("udidTitle", oriApp.getUdidTitle());
			data.put("udidDescription", oriApp.getUdidDescription());
			data.put("no", oriApp.getVersion());
			data.put("img", oriApp.getIconUrl());
			data.put("showIconUrl", oriApp.getShowIconUrl());
			data.put("showPicUrl", oriApp.getScreenshots());
			data.put("appName", oriApp.getApplicationName());
			data.put("shop", oriApp.getOrganizationName());
			data.put("remarkname", oriApp.getShowAppName());
			data.put("appType", oriApp.getAppType());
			data.put("select", oriApp.getType());
			data.put("num", oriApp.getMark());
			data.put("remark", oriApp.getIntroduce());
			data.put("installType", oriApp.getInstartModel());
			data.put("password", oriApp.getInstartPassword());
			return R.ok().put("data", data);
		} catch (IOException | PropertyListFormatException | ParseException | ParserConfigurationException
				| SAXException e) {
			e.printStackTrace();
			return R.error("文件解析错误");
		} finally {
			if (ipaFile != null) {
				ipaFile.deleteTempDir();
			}
		}
	}

	@Override
	public Map<String, Object> getAppInfoById(Long appId,String ip,String sessionId) {
		OriginalApplication app = originalApplicationDao.selectById(appId);
		if (app == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<>();
		result.put("app", app);
		Long downloadCount = rechargeService.getUseCount(app.getUserId());
		Long rechargeCount = rechargeService.getRechargeCount(app.getUserId());
		boolean state = app.getEnable();
		if (state && AppType.PERSONAL.getValue().equals(app.getAppType())) {
			state = downloadCount < rechargeCount;
			AppQuota appQuota = appQuotaService.findByAppId(app.getAppId());
			if (state && appQuota != null) {
				state = appQuota.getMaxNum() > appQuota.getUsedNum();
			}
		}
		VisitLog log = new VisitLog();
		log.setAppId(app.getAppId());
		log.setIp(ip);
		log.setSessionId(sessionId);
		log.setTime(System.currentTimeMillis());
		visitLogService.save(log);
		
		result.put("state", state);
		return result;
	}
	

	@Override
	public Map<String, Object> getAppInfoByCode(String code,String ip,String sessionId) {
		OriginalApplication app = originalApplicationDao.selectByDownloadCode(code);
		if (app == null) {
			return null;
		}
		Map<String, Object> result = new HashMap<>();
		result.put("app", app);
		Long downloadCount = rechargeService.getUseCount(app.getUserId());
		Long rechargeCount = rechargeService.getRechargeCount(app.getUserId());
		boolean state = app.getEnable();
		if (state && AppType.PERSONAL.getValue().equals(app.getAppType())) {
			state = downloadCount < rechargeCount;
			AppQuota appQuota = appQuotaService.findByAppId(app.getAppId());
			if (state && appQuota != null) {
				state = appQuota.getMaxNum() > appQuota.getUsedNum();
			}
		}
		
		VisitLog log = new VisitLog();
		log.setAppId(app.getAppId());
		log.setIp(ip);
		log.setSessionId(sessionId);
		log.setTime(System.currentTimeMillis());
		visitLogService.save(log);
		
		result.put("state", state);
		return result;
	}

	@Override
	@Transactional
	public void updateDownloadNum(Long appId,int addNum,boolean real) {
		OriginalApplication app = originalApplicationDao.selectById(appId);
		originalApplicationDao.update(app,addNum,real ? addNum : 0);
	}

	@Override
	public List<Map<String, Object>> getOriginalApplicationByUserId(Long userId) {
		List<OriginalApplication> appList = originalApplicationDao.selectByOwnerId(userId,0, 1000,"%%");
		List<Map<String, Object>> list = new ArrayList<>();
		for(OriginalApplication app:appList) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", app.getAppId());
			map.put("icon", StringUtils.isBlank(app.getShowIconUrl()) ? app.getIconUrl() : app.getShowIconUrl());
			map.put("name", app.getApplicationName());
			list.add(map);
		}
		return list;
	}
}
