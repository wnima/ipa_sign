package com.bootdo.signature.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.R;
import com.bootdo.signature.service.RechargeLogService;
import com.bootdo.signature.service.RechargeService;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.UserService;

@Controller
@RequestMapping("/signature/recharge")
public class RechargeController extends BaseController{
	@Autowired
	RechargeLogService rechargeLogService;
	@Autowired
	RechargeService rechargeService;
	
	@Autowired
	UserService userService;
	
	@RequiresPermissions("sig:recharge:add")
	@PostMapping("/add")
	@ResponseBody
	R add(String phone,int num){
	 	UserDO user = userService.getByUsername(phone);
	 	if(user == null) {
	 		return R.error("没有该用户");
	 	}
		rechargeService.addRechargeNum(user.getUserId(), user.getName(), num, getUserId(),getUsername());
		return R.ok();
	}
	
	@RequiresPermissions("sig:recharge:add")
	@GetMapping("/add")
	String show(Model model) {
		return "/signature/recharge/add.html";
	}
	
	@GetMapping("/personallist")
	String personalShow(Model model) {
		return "/signature/recharge/personallist.html";
	}
	
	@PostMapping("/personallist")
	@ResponseBody
	R personallist(Integer p,Integer i) {
		if(p == null) {
			p = 0;
		}
		if(i == null) {
			i = 10;
		}
		return rechargeLogService.selectListByUserId(p, i, getUserId());
	}
	
	@RequiresPermissions("sig:recharge:add")
	@GetMapping("/list")
	String list(Model model) {
		return "/signature/recharge/list.html";
	}
	
	@RequiresPermissions("sig:recharge:add")
	@PostMapping("/loglist")
	@ResponseBody
	R loglist(Integer p,Integer i) {
		if(p == null) {
			p = 0;
		}
		if(i == null) {
			i = 10;
		}
		return rechargeLogService.selectListLimit(p,i);
	}
}
