package com.bootdo.signature.controller;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.R;
import com.bootdo.signature.service.PurchaseHistoryService;


@Controller
@RequestMapping("/system/purchase_history")
public class PurchaseHistoryController extends BaseController{
	
	@Autowired
	PurchaseHistoryService purchaseHistoryService;
	
	@RequiresPermissions("system:form_and_logs:purchase_history:list")
	@GetMapping
	String list() {
		return "/system/purchase_history/list.html";
	}
	
	// 消费记录
	@RequiresPermissions("system:form_and_logs:purchase_history:list")
	@ResponseBody
	@PostMapping("/list")
	R list(Integer f,Integer n) {
		if(f == null) {
			f = 0;
		}
		if(n == null) {
			n = 0;
		}
		Map<String,Object> data = purchaseHistoryService.list(f, n, getUserId());
		return R.ok(data);
	}
}
