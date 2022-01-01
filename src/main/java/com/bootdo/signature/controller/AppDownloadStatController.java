package com.bootdo.signature.controller;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bootdo.common.controller.BaseController;
import com.bootdo.common.utils.R;
import com.bootdo.signature.entity.po.AppDownloadStat;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.AppDownloadStatService;
import com.bootdo.signature.service.OriginalApplicationService;

@Controller
@RequestMapping("/signature/appstat")
public class AppDownloadStatController extends BaseController {
	private final static String[] WEEK = {"周一","周二","周三","周四","周五","周六","周日"};
	
	@Autowired
	OriginalApplicationService originalApplicationService;
	@Autowired
	AppDownloadStatService appDownloadStatService;
	
	@GetMapping()
	String index() {
		return "/signature/appstat/details.html";
	}
	
	@GetMapping("/applist")
	@ResponseBody
	R applist() {
		Long userId = getUserId();
		List<Map<String, Object>> list = originalApplicationService.getOriginalApplicationByUserId(userId);
		return R.ok().put("data", list);
	}
	
	
	@PostMapping("/details")
	@ResponseBody
	R details(Integer appId,Integer type) {
		if(appId == null) {
			return R.error();
		}
		OriginalApplication app = originalApplicationService.getOriginalApplicationById(appId.longValue());
		if(app != null) {
			if(type == null) {
				Map<String, Object> data = appDownloadStatService.getAllDetails(appId);
				return R.ok().put("data", data);
			}else {
				List<AppDownloadStat> list = null;
				switch (type) {
				case 1:
					list = appDownloadStatService.getHourStat(appId, 0, 24);
					break;
				case 2:
					list = appDownloadStatService.getDayStat(appId, 0, 7);
					break;
				case 3:
					list = appDownloadStatService.getMonthStat(appId, 0, 30);
					break;
				default:
					break;
				}
				Map<String,Object> data = new HashMap<>();
 				if(list != null) {
 					List<String> title = new ArrayList<>();
 					List<Integer> numList = new ArrayList<>();
					for(AppDownloadStat i:list) {
						Instant instant = Instant.ofEpochMilli(i.gettTime());
						ZoneId zone = ZoneId.of("Asia/Shanghai");
						LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
						numList.add(i.getNum());
						switch (type) {
						case 1:							
							title.add(localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
							break;
						case 2:							
							title.add(WEEK[localDateTime.getDayOfWeek().getValue() - 1]);
							break;
						case 3:							
							title.add(localDateTime.format(DateTimeFormatter.ofPattern("yyyyMM")));
							break;
						default:
							break;
						}
					}
					data.put("echartsX", title);
					data.put("echartsData", numList);
 				}
				return R.ok().put("data", data);
			}
		}else {
			return R.error();
		}
	}
}
