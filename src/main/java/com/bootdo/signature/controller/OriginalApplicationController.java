package com.bootdo.signature.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.OssTools;
import com.bootdo.common.utils.R;
import com.bootdo.signature.definition.AppType;
import com.bootdo.signature.entity.po.AppQuota;
import com.bootdo.signature.entity.po.DownloadLog;
import com.bootdo.signature.entity.po.EnterprisePackage;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.AppQuotaService;
import com.bootdo.signature.service.DownloadLogService;
import com.bootdo.signature.service.EnterprisePackageService;
import com.bootdo.signature.service.HeavyVolumeService;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.signature.util.FileUtil;

/**
 * 应用控制器，包含iOS与Android应用
 * @author abc
 */
@Controller
@RequestMapping("/signature/app")
public class OriginalApplicationController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(OriginalApplicationController.class);

	@Autowired
	private OriginalApplicationService originalApplicationService;

	@Autowired
	private DownloadLogService downloadLogService;

	@Autowired
	private BootdoConfig bootdoConfig;

	@Autowired
	private AppQuotaService appQuotaService;

	@Autowired
	private EnterprisePackageService enterprisePackageService;
	
	@Autowired
	private HeavyVolumeService heavyVolumeService;
	

	@Autowired
	private OssTools ossTools;

	@ResponseBody
	@PostMapping("/uploadipa")
	R uploadIpa(@RequestParam("file") MultipartFile file) {
		R result = null;
		String fileName = UUID.randomUUID().toString() + ".ipa";
		File ipafile = new File(bootdoConfig.getUploadPath(), fileName);
		try {
			FileUtil.saveToFile(file, ipafile);
			result = originalApplicationService.addApplication(ipafile,getUserId());
		} catch (IOException e) {
			e.printStackTrace();
			result = R.error("文件上传失败");
		}
		return result;
	}
	
	@GetMapping("/upload")
	String upload() {
		return "/signature/app/upload.html";
	}

	@GetMapping("/all/list")
	@RequiresPermissions("signature:app:listall")
	String allAppListv() {
		return "/signature/app/alllist.html";
	}
	
	@PostMapping("/all/list")
	@ResponseBody
	@RequiresPermissions("signature:app:listall")
	R allAppList(@RequestBody Map<String,String> param) {
	 	Integer flag = Integer.parseInt(param.get("flag"));
	  	Integer num = Integer.parseInt(param.get("num"));
	  	String s = param.get("s");
	  	if(s == null) {
	  		s = "";
	  	}
	  	logger.info("originalApplicationService:{} flag:{} num:{} s:{}",originalApplicationService,flag, num,s);
	  	R result = originalApplicationService.list(flag, num,s);
		return result;
	}
	
	@PostMapping("/quota")
	@ResponseBody
	R updateQuota(Long id,@RequestParam("num")Integer limit) {
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(id);
		if(app == null) {
			return R.error();
		}
		
		if(!(getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L))) {
			return R.error();
		}
		
		AppQuota appQuota = appQuotaService.findByAppId(app.getAppId());
		if(appQuota == null) {
			if(limit < 0) {
				return R.error("操作失败");
			}
			appQuota = new AppQuota();
			appQuota.setAppId(app.getAppId());
			appQuota.setMaxNum(limit);
			appQuota.setUsedNum(0);
			appQuotaService.save(appQuota);
			return R.ok();
		}else {
			if(limit > -1) {
				appQuotaService.updateLimitById(appQuota.getId(),limit);
			}else {
				appQuotaService.deleteById(appQuota.getId());
			}
		}
		return R.ok();
	}
	
	@PostMapping("/list")
	@ResponseBody
	R userApplist(@RequestBody Map<String,String> param) {
	 	Integer flag = Integer.parseInt(param.get("flag"));
	  	Integer num = Integer.parseInt(param.get("num"));
	  	String s = param.get("s");
	  	if(s == null) {
	  		s = "";
	  	}
		Long userId = getUserId();
		String username = getUsername();
		R result = originalApplicationService.getOriginalApplicationByOwnerId(userId,username,flag, num,s);
		return result;
	}

	@PostMapping("/{appid}/upappinfo")
	@ResponseBody
	Map<String, Object> upAppInfo(@PathVariable("appid") Long appId, String shop, String select, String remark,
			Integer num, String remarkname, String showIconUrl, String appName, String domain, String showPicUrl,Integer appType,String udidTitle,String udidDescription,
			Integer installType,
			String password,
			String area) {
		appType = AppType.PERSONAL.getValue();
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(appId);
		if (num != null &&
				num > 0 &&
				num <= 5 &&
				app != null &&
				(getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L))
				) {
			app.setType(select);
			app.setMark(num);
			app.setIntroduce((StringUtils.isBlank(remark) || "null".equals(remark)) ? "" : remark);
			app.setRemarkName((StringUtils.isBlank(remarkname) || "null".equals(remarkname)) ? "" : remarkname);
			app.setShowIconUrl((StringUtils.isBlank(showIconUrl) || "null".equals(showIconUrl)) ? "" : showIconUrl);
			app.setShowAppName((StringUtils.isBlank(appName) || "null".equals(appName)) ? "" : appName);
			app.setScreenshots((StringUtils.isBlank(showPicUrl) || "null".equals(showPicUrl)) ? "" : showPicUrl);
			app.setAppType(appType);
			app.setInstartModel(installType);
			app.setInstartPassword((StringUtils.isBlank(password) || "null".equals(password)) ? "" : password);
			app.setArea((StringUtils.isBlank(area) || "null".equals(area)) ? "" : area);
			app.setOrganizationName((StringUtils.isBlank(shop) || "null".equals(shop)) ? "" : shop);
			
			if(AppType.ENTERPRISE.equals(AppType.getByValue(appType))) {
				EnterprisePackage enter = enterprisePackageService.findByAppId(app.getAppId());
			 	if(enter == null) {
			 		enter = new EnterprisePackage();
			 	}
			 	enter.setAppId(app.getAppId().intValue());
			 	enter.setStatus(1);
			 	enter.setUrl("");
				enterprisePackageService.save(enter);
			}
			
			app.setUdidTitle((StringUtils.isBlank(udidTitle) || "null".equals(udidTitle)) ? "" : udidTitle );
			app.setUdidDescription((StringUtils.isBlank(udidDescription) || "null".equals(udidDescription)) ? "" : udidDescription);
			
			if (!StringUtils.isBlank(domain)) {
				domain = domain.replaceAll(" ", "").toLowerCase();
				if (domain.length() < 10 && domain.matches("^[a-z0-9A-Z]+$")) {
					OriginalApplication ori = originalApplicationService.getOriginalApplicationByShortDomain(domain);
					if (ori == null || (ori != null && app.getAppId().equals(ori.getAppId()))) {
						app.setShortDomain(domain);
					} else {
						return R.error("独立域名已被使用");
					}
				} else {
					return R.error("只能由字母数字组合,长度小于等于10");
				}
			} else {
				app.setShortDomain("");
			}
			originalApplicationService.update(app);
			return R.ok();
		}
		return null;
	}

	@PostMapping("/{appid}/upandroidurl")
	@ResponseBody
	Map<String, Object> upAndroidUrl(@PathVariable("appid") Long appId, String url) {
		if(StringUtils.isBlank(url)) {
			return R.error("地址不能为空");
		}
		if(!(url.startsWith("https") || url.startsWith("http"))) {
			return R.error("请填写包含http或https开头的完整地址");
		}
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(appId);
		if(app != null && (getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L))) {
			app.setAndroidFileUrl(url);
			originalApplicationService.update(app);
		}
		return R.ok();
	}
	
	@PostMapping("/checkdomain")
	@ResponseBody
	Map<String, Object> checkdomain(String domain) {
		if (!StringUtils.isBlank(domain)) {
			domain = domain.replaceAll(" ", "").toLowerCase();
			if (domain.length() < 10 && domain.matches("^[a-z0-9A-Z]+$")) {
				OriginalApplication ori = originalApplicationService.getOriginalApplicationByShortDomain(domain);
				if (ori == null) {
					return R.ok();
				} else {
					return R.error("独立域名已被使用");
				}
			}
			return R.error("只能由字母数字组合,长度小于等于10");
		}
		return R.ok();
	}

	@PostMapping("/enable")
	@ResponseBody
	Map<String, Object> enable(@RequestParam("id") Long appId, @RequestParam("enable") boolean enable) {
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(appId);
		if (app != null && (getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L))) {
			app.setEnable(enable);
			originalApplicationService.update(app);
			return R.ok();
		}
		return null;
	}

	@PostMapping("/delete")
	@ResponseBody
	Map<String, Object> delete(@RequestParam("id") Long appId) {
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(appId);
		if (app != null) {
			if(getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L)) {
				originalApplicationService.delete(app);
				return R.ok();
			}
		}
		return null;
	}

	@PostMapping("/updateicon")
	@ResponseBody
	Map<String, Object> updateicon(@RequestParam("file") MultipartFile file) {
		try {
			byte[] fileData = file.getBytes();
			BufferedImage io = ImageIO.read(new ByteArrayInputStream(fileData));
			if (io != null) {
				String iconUrl = ossTools.uploadIMGFile(FileUtil.renameToUUID(file.getOriginalFilename()),
						new ByteArrayInputStream(fileData));
				return R.ok().put("url", iconUrl);
			}
		} catch (IOException e) {
			logger.error("read image error", e);
		}
		return R.error().put("msg", "图片无法识别");
	}

	@PostMapping("/upappscreen")
	@ResponseBody
	Map<String, Object> upApplicationScreenshots(@RequestParam("file") MultipartFile file) {
		try {
			byte[] fileData = file.getBytes();
			BufferedImage io = ImageIO.read(new ByteArrayInputStream(fileData));
			if (io != null) {
				String imgUrl = ossTools.uploadIMGFile(FileUtil.renameToUUID(file.getOriginalFilename()),
						new ByteArrayInputStream(fileData));
				return R.ok().put("url", imgUrl);
			}
		} catch (IOException e) {
			logger.error("read image error", e);
		}
		return R.error().put("msg", "图片无法识别");
	}
	
	@PostMapping("/updateipa")
	@ResponseBody
	Map<String, Object> upIpa(@RequestParam("file") MultipartFile file,long appid) {
		R result = null;
		String fileName = UUID.randomUUID().toString() + ".ipa";
		File ipafile = new File(bootdoConfig.getUploadPath(), fileName);
		try {
			OriginalApplication app = originalApplicationService.getOriginalApplicationById(appid);
			if(getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L)) {
				FileUtil.saveToFile(file, ipafile);
				result = originalApplicationService.updateApplication(ipafile, appid);
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = R.error("文件上传失败");
		}
		return result;
	}
	
	@PostMapping("/updateapk")
	@ResponseBody
	Map<String, Object> upApk(@RequestParam("file") MultipartFile file,long appid) {
		R result = null;
		String fileName = UUID.randomUUID().toString() + ".apk";
		File ipafile = new File(bootdoConfig.getUploadPath(), fileName);
		try {
			OriginalApplication app = originalApplicationService.getOriginalApplicationById(appid);
			if(getUserId().equals(app.getUserId()) || getUser().getRoleIds().contains(60L) || getUser().getRoleIds().contains(61L) || getUser().getRoleIds().contains(1L)) {
				FileUtil.saveToFile(file, ipafile);
				String fileUrl = ossTools.uploadIMGFile(ipafile.getName(), ipafile.getAbsolutePath());
				app.setAndroidFileUrl(fileUrl);
				originalApplicationService.update(app);
				result = R.ok();	
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = R.error("文件上传失败");
		}
		return result;
	}
	
	@PostMapping("/entupload")
	@RequiresPermissions("signature:app:listall:uploadipa")
	@ResponseBody
	Map<String, Object> upEntApplication(@RequestParam("file") MultipartFile file,int appid) {
		R result = null;
		String fileName = UUID.randomUUID().toString() + ".ipa";
		File ipafile = new File(bootdoConfig.getUploadPath(), fileName);
		try {
			FileUtil.saveToFile(file, ipafile);
			String ipaUrl = ossTools.uploadIPAFile(fileName,ipafile.getAbsolutePath());
			EnterprisePackage entPackage = enterprisePackageService.findByAppId(appid);
			if(entPackage != null) {
				entPackage.setUrl(ipaUrl);
				entPackage.setStatus(2);
				enterprisePackageService.update(entPackage);
				result = R.ok();
			}else {
				result = R.error();
			}
		} catch (IOException e) {
			e.printStackTrace();
			result = R.error("文件上传失败");
		}
		return result;
	}
	
	@GetMapping("/{appid}/downloadinfo")
	String downloadinfo(@PathVariable("appid") Long appId, Model model) {
		Long userId = getUserId();
		Map<Integer, List<DownloadLog>> data = downloadLogService.getWeekByUserId(userId);
		model.addAttribute("downloadData", JSONObject.toJSON(data).toString());
		return "/signature/app/downloadinfo.html";
	}
	
	@PostMapping("/getheavyvolume")
	@RequiresPermissions("signature:app:heavyvolume:get")
	@ResponseBody
	R getHeavyVolume(Integer id) {
		if(id == null) {
			id = 0;
		}
		return heavyVolumeService.getConfig(id);
	}
	
	@PostMapping("/setheavyvolume")
	@RequiresPermissions("signature:app:heavyvolume:set")
	@ResponseBody
	R setHeavyVolume(Long id,Integer enable ,Long starttime ,Integer intervals ,Integer downnum ,Integer addnum ) {
		logger.info("setHeavyVolume {} {} {} {} {} {}",id, enable , starttime , intervals , downnum , addnum);
		if(enable == null || starttime == null || intervals == null || downnum == null | addnum == null) {
			return R.error();
		}
		if(id == null) {
			id = 0L;
		}
		heavyVolumeService.configuration(id, enable, starttime, intervals, downnum, addnum);
		return R.ok();
	}
}
