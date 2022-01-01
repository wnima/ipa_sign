package com.bootdo.signature.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.bootdo.signature.service.RechargeService;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.UserService;

@Controller
@RequestMapping("/signature/statistics")
public class StatisticsController {
	@Autowired
	UserService userService;	
	@Autowired
	private RechargeService rechargeService;
	
	@RequiresPermissions("sig:stat:all:surplus")
	@GetMapping("/allusers/surplus")
	String surplusAllUsers(Model model) {
		HashMap<String, Object> where = new HashMap<>();
		where.put("deptId", "");
		List<UserDO> list = userService.list(where);
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		long sumdownloadCount = 0;
		long sumrechargeCount = 0;
		long sumsurplus = 0;
		long userCount = 0;
		for(UserDO item:list) {
			Long downloadCount = rechargeService.getUseCount(item.getUserId());
			Long rechargeCount = rechargeService.getRechargeCount(item.getUserId());
			Long surplus = rechargeCount - downloadCount;
			HashMap<String, Object> ih = new HashMap<>();
			ih.put("userId", item.getUserId());
			ih.put("userName", item.getUsername());
			ih.put("downloadCount", downloadCount);
			ih.put("rechargeCount", rechargeCount);
			ih.put("surplus", surplus);
			data.add(ih);
			sumdownloadCount += downloadCount;
			sumrechargeCount += rechargeCount;
			sumsurplus += surplus;
			userCount ++;
		}
		model.addAttribute("data", data);
		model.addAttribute("downloadCount", sumdownloadCount);
		model.addAttribute("rechargeCount", sumrechargeCount);
		model.addAttribute("surplus", sumsurplus);
		model.addAttribute("userCount", userCount);
		return "/signature/statistics/details.html";
	}
}
