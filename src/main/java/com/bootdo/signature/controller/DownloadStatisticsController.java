package com.bootdo.signature.controller;

import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.R;
import com.bootdo.signature.service.DownloadLogService;

@RequestMapping("/system/downloadstat")
public class DownloadStatisticsController extends BaseController {
	@Autowired
	DownloadLogService downloadLogService;
	
	@RequiresPermissions("system:form_and_logs:download_statistics:list")
	@GetMapping
	String list() {
		return "/system/downloadlog/list.html";
	}
	
	@RequiresPermissions("system:form_and_logs:download_statistics:list")
	@ResponseBody
	@GetMapping("/list")
	R list(Integer f,Integer n) {
		if(f == null) {
			f = 0;
		}
		if(n == null) {
			n = 0;
		}
		Map<String,Object> data = downloadLogService.statlist(f,n,getUserId());
		return R.ok();
	}
}
