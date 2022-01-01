package com.bootdo.signature.controller;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.utils.R;
import com.bootdo.signature.entity.po.AppleAccount;
import com.bootdo.signature.entity.vo.AccountVO;
import com.bootdo.signature.service.AppleService;
import com.bootdo.signature.util.FileUtil;

@Controller
@RequestMapping("/signature/account")
public class AppleAccountController {
	@Autowired
	private AppleService appleService;
	@Autowired
	private BootdoConfig bootdoConfig;
	
	@RequiresPermissions("sig:account:list")
	@PostMapping("/list")
	@ResponseBody
	R list() {
		return R.ok().put("data", appleService.list());
	}
	
	@RequiresPermissions("sig:account:index")
	@GetMapping("/index")
	String index() {
		return "/signature/account/list.html";
	}
	
	@RequiresPermissions("sig:account:add")
	@PostMapping("/add")
	@ResponseBody
	R add(AccountVO vo) {
		AppleAccount account = new AppleAccount();
		account.setBlockade(false);
		account.setEnable(false);
		account.setPassword(vo.getPassword());
		account.setUsername(vo.getUsername());
		account.setP8(vo.getP8());
		account.setIss(vo.getIss());
		account.setKid(vo.getKid());
		appleService.add(account);
		return R.ok();
	}
	
	@RequiresPermissions("sig:account:add")
	@PostMapping("/batchadd")
	@ResponseBody
	R batchAdd(@RequestParam("file") MultipartFile file) {
		String fileName = UUID.randomUUID().toString() + ".xlsx";
		File xlsxfile = new File(bootdoConfig.getUploadPath(), fileName);
		R result = R.error();
		try {
			FileUtil.saveToFile(file, xlsxfile);
			result = appleService.batchAdd(xlsxfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@RequiresPermissions("sig:account:enable")
	@PostMapping("/enable")
	@ResponseBody
	R enable(AccountVO vo) {
		boolean result = appleService.enable(vo.getId(), vo.isEnable());
		return result? R.ok() : R.error();
	}
	
	@RequiresPermissions("sig:account:login")
	@PostMapping("/login")
	@ResponseBody
	R login(AccountVO vo) {
		boolean result = appleService.login(vo.getId());
		return result? R.ok() : R.error();
	}
	
	@RequiresPermissions("sig:account:smscode")
	@PostMapping("/smscode")
	@ResponseBody
	R smscode(AccountVO vo) {
		boolean result = appleService.verification(vo.getId(), vo.getCode());
		return result? R.ok() : R.error();
	}
}
