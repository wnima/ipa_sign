package com.bootdo.signature.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.IPUtils;
import com.bootdo.common.utils.ImageUtil;
import com.bootdo.common.utils.OssTools;
import com.bootdo.common.utils.PlistTools;
import com.bootdo.common.utils.R;
import com.bootdo.common.utils.ResultTools;
import com.bootdo.signature.dao.AndroidDownloadDao;
import com.bootdo.signature.dao.AndroidDownloadLogDao;
import com.bootdo.signature.dao.SignaturePackageDao;
import com.bootdo.signature.definition.AppType;
import com.bootdo.signature.definition.TaskErrorCode;
import com.bootdo.signature.definition.TaskField;
import com.bootdo.signature.definition.TaskState;
import com.bootdo.signature.entity.po.AndroidDownload;
import com.bootdo.signature.entity.po.EnterprisePackage;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.entity.po.SignaturePackage;
import com.bootdo.signature.service.SignatureService;
import com.bootdo.signature.util.STSTokenTools;
import com.bootdo.signature.service.DownloadLogService;
import com.bootdo.signature.service.EnterprisePackageService;
import com.bootdo.signature.service.HeavyVolumeService;
import com.bootdo.signature.service.OriginalApplicationService;

import cn.hutool.core.lang.UUID;

@Controller
@RequestMapping("/signature/install")
public class InstallController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(InstallController.class);
	
	@Autowired
	private SignatureService signatureService;
	@Autowired
	private OriginalApplicationService originalApplicationService;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SignaturePackageDao signaturePackageDao;
	@Autowired
	private DownloadLogService downloadLogService;
	@Autowired
	private EnterprisePackageService enterprisePackageService;
	@Autowired
	private HeavyVolumeService heavyVolumeService;
	@Autowired
	private AndroidDownloadDao androidDownloadDao;
	@Autowired
	private AndroidDownloadLogDao androidDownloadLogDao;
	@Autowired
	private OssTools ossTools;

	/**
	 * 获取应用信息
	 * @param appId
	 * @param udid
	 * @param model
	 * @param req
	 * @return
	 */
	@GetMapping("/{code}/appinfo")
	public String appinfo(@PathVariable("code") String code, String udid, Model model, HttpServletRequest req) {
		String sessionId = req.getSession().getId();
		String ip = IPUtils.getIpAddr(req);
		
		Map<String,Object> result = null;
		if(code.length() >= 8) {
			result = originalApplicationService.getAppInfoByCode(code,ip,sessionId);
		}else {
			result = originalApplicationService.getAppInfoById(Long.parseLong(code),ip,sessionId);			
		}
		
		if (result == null) {
			return "redirect:index";
		}
		model.addAttribute("result", result);
		return "/signature/install/info.html";
	}
	
	@PostMapping("/{code}/{udid}/exec")
	public @ResponseBody R startTask(@PathVariable("code") String code,
			@PathVariable("udid") String udid) {
		if(code == null || udid == null || udid.length() < 25) {
			return R.error();
		}
		OriginalApplication application = null;
		if(code.length() >= 8) {
			application = originalApplicationService.getOriginalApplicationByDownloadCode(code);	
		}else {
			application = originalApplicationService.getOriginalApplicationById(Long.parseLong(code));			
		}
		if (application == null) {
			return R.error();
		}
		String key = TaskField.getTaskKey(udid, application.getAppId());
		signatureService.addTask(key);
		signatureService.addDevice(key, application.getAppId(), udid);
		return R.ok().put("code", 200);
	}

	@GetMapping("/{code}/{udid}/state")
	public @ResponseBody Map<String, Object> getState(@PathVariable("code") String code,
			@PathVariable("udid") String udid) throws IOException {
		if(code == null || udid == null || udid.length() < 25) {
			return R.error();
		}
		OriginalApplication application = null;
		if(code.length() >= 8) {
			application = originalApplicationService.getOriginalApplicationByDownloadCode(code);	
		}else {
			application = originalApplicationService.getOriginalApplicationById(Long.parseLong(code));			
		}
		if (application == null) {
			return R.error();
		}
		String key = TaskField.getTaskKey(udid, application.getAppId());
		if (!application.getEnable() || !AppType.PERSONAL.equals(AppType.getByValue(application.getAppType()))) {
			Map<String, Object> result = ResultTools.paramsError();
			result.put("msg", "已下架");
			stringRedisTemplate.delete(key);
			return result;
		} else {
			Object val = stringRedisTemplate.opsForHash().get(key, TaskField.PROGRESS);
			Map<String, Object> resultMap = ResultTools.result_ok();
			if (val == null) {
				resultMap.put("state", 0);
				resultMap.put("code", 501);
				stringRedisTemplate.delete(key);
			} else {
				resultMap.put("state", val);
				Object taskException = stringRedisTemplate.opsForHash().get(key, TaskField.EXCEPTION);
				logger.info("taskException:{}", taskException);
				if (taskException != null && !TaskErrorCode.NO_ERROR.isEquelsCode(taskException)) {
					logger.error("taskException progress:{} exception：{}",val,taskException);
					stringRedisTemplate.delete(key);
					resultMap.put("code", 503);
				}else {
					resultMap.put("code", 200);
				}
				if(TaskState.GET_CONFIGURATION.isEquelsCode(val)) {
					stringRedisTemplate.opsForHash().put(key, TaskField.PROGRESS, TaskState.SIGN_START.getKeyToString());
					signatureService.exec(key, application.getAppId(), udid);
				}
				if(TaskState.COMPLETE.isEquelsCode(val)) {
					stringRedisTemplate.delete(key);
				}
			}
			return resultMap;
		}
	}

	@GetMapping({"/{code}/{udid}/manifest.plist"})
	public @ResponseBody Map<String, Object> getInstallPlist(@PathVariable("code") String code,
			@PathVariable(name="udid",required=false) String udid, HttpServletResponse resp) throws IOException {
		if(code == null) {
			return R.error();
		}
		OriginalApplication application = null;
		if(code.length() >= 8) {
			application = originalApplicationService.getOriginalApplicationByDownloadCode(code);	
		}else {
			application = originalApplicationService.getOriginalApplicationById(Long.parseLong(code));			
		}
		if (application == null) {
			Map<String, Object> result = ResultTools.paramsError();
			result.put("msg", "没有udid关联包信息");
			return result;
		}
		
		if(!application.getEnable()) {
			Map<String, Object> result = ResultTools.paramsError();
			result.put("msg", "已下架");
			return result;
		}
		
		if(AppType.PERSONAL.getValue().equals(application.getAppType())) {
			if (udid == null || udid.length() == 0) {
				return ResultTools.paramsError();
			}
			List<SignaturePackage> list = signaturePackageDao.selectSignaturePackageByPackageNameAndUdid(udid,
					application.getAppId());
			logger.info("getInstallPlist {} {} {}", list, application.getPackageName(),
					application.getApplicationName());
			if(list.isEmpty()) {
				return ResultTools.paramsError();
			}
			Optional<SignaturePackage> op = list.stream().findFirst();
			String plist = PlistTools.createDownloadPlist(ossTools.createIPAUrl(op.get().getFileUrl()), application.getPackageName(),
					application.getApplicationName());
			resp.setHeader("Content-Type", "application/octet-stream");
			resp.setHeader("Content-Disposition", "attachment;filename=" + "manifest.plist");
			ServletOutputStream os = resp.getOutputStream();
			os.write(plist.getBytes());
			os.flush();
			String key = ("TASK:" + application.getAppId() + ":" + udid).toUpperCase();
			stringRedisTemplate.delete(key);
			heavyVolumeService.addUseNumber(application.getAppId(), application.getUserId());
		}else {
			Map<String, Object> result = ResultTools.paramsError();
			result.put("msg", "已下架");
			return result;
		}
		return null;
	}
	
	@GetMapping("/getImageCode")
    @ResponseBody
    public Map<String,Object> getImageVerifyCode(HttpSession session) {
        Map<String, Object> resultMap = new HashMap<>();
        //读取本地路径下的图片,随机选一条
        File file = new File("verify_img");
        File[] files = file.listFiles();
        int n = new Random().nextInt(files.length);
        File imageUrl = files[n];
        ImageUtil.createImage(imageUrl, resultMap);
 
        //读取网络图片
        //ImageUtil.createImage("http://hbimg.b0.upaiyun.com/7986d66f29bfeb6015aaaec33d33fcd1d875ca16316f-2bMHNG_fw658",resultMap);
        session.setAttribute("xWidth", resultMap.get("xWidth"));
        resultMap.remove("xWidth");
        resultMap.put("errcode", 0);
        resultMap.put("errmsg", "success");
        return resultMap;
    }
 
    @ResponseBody
    @PostMapping("/verifyPassword")
    public Map<String,Object> verifyPassword(String code,String password,HttpSession session) {
    	logger.info("appId:{} password:{}",code,password);
        Map<String, Object> resultMap = new HashMap<>();
		OriginalApplication application = null;
		if(code != null && code.length() >= 8) {
			application = originalApplicationService.getOriginalApplicationByDownloadCode(code);	
		}else {
			application = originalApplicationService.getOriginalApplicationById(Long.parseLong(code));			
		}
    	if(application != null && password != null && password.equals(application.getInstartPassword())) {
    		String uuid = UUID.randomUUID().toString();
    		resultMap.putAll(R.ok());
    		resultMap.put("token", uuid);
    		session.setAttribute("token", uuid);
    	}else {
    		resultMap.putAll(R.error("密码错误"));
    	}
        return resultMap;
    }
	
    /**
     * 校验滑块拼图验证码
     *
     * @param moveLength 移动距离
     * @return BaseRestResult 返回类型
     * @Description: 生成图形验证码
     */
    @ResponseBody
    @PostMapping("/verifyImageCode")
    public Map<String,Object> verifyImageCode(@RequestParam(value = "Y") String height,@RequestParam(value = "X") String moveLength,HttpSession session) {
        Double dMoveLength = Double.valueOf(moveLength);
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Integer xWidth = (Integer) session.getAttribute("xWidth");
            if (xWidth == null) {
                resultMap.put("errcode", 1);
                resultMap.put("errmsg", "验证过期，请重试");
                return resultMap;
            }
            if (Math.abs(xWidth - dMoveLength) > 10) {
                resultMap.put("errcode", 1);
                resultMap.put("errmsg", "验证不通过");
            } else {
            	String token = UUID.randomUUID().toString();
            	session.setAttribute("token", token);
                resultMap.put("errcode", 0);
                resultMap.put("token", token);
                resultMap.put("errmsg", "验证通过");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            session.removeAttribute("xWidth");
        }
        return resultMap;
    }
   
    @PostMapping("/{code}/androidurl")
    @ResponseBody
    public Map<String,Object> getAndroidUrl(@PathVariable("code") String code) {
		if(code == null) {
			return R.error();
		}
		OriginalApplication application = null;
		if(code.length() >= 8) {
			application = originalApplicationService.getOriginalApplicationByDownloadCode(code);	
		}else {
			application = originalApplicationService.getOriginalApplicationById(Long.parseLong(code));			
		}
		if (application == null || StringUtils.isBlank(application.getAndroidFileUrl())) {
			return R.error();
		}
	 	List<AndroidDownload> down = androidDownloadDao.select(application.getAppId());
	 	if(down.size() > 0) {
	 		androidDownloadDao.countIncrease(down.get(0).getId());
	 	}else {
	 		AndroidDownload d = new AndroidDownload();
	 		d.setId(application.getAppId().intValue());
	 		d.setCount(1);
	 		androidDownloadDao.insert(d);
	 	}
	 	String url = application.getAndroidFileUrl();
	 	if(!StringUtils.isBlank(url)) {
	 		if(url.startsWith("http")) {
	 			
	 		}else {
	 			url = ossTools.createIPAUrl(url);
	 		}
	 	}
    	return R.ok().put("url", url);
    }
}
