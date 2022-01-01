package com.bootdo.system.controller;

import com.bootdo.common.annotation.Log;
import com.bootdo.common.config.BootdoConfig;
import com.bootdo.common.controller.BaseController;
import com.bootdo.common.domain.FileDO;
import com.bootdo.common.domain.Tree;
import com.bootdo.common.service.FileService;
import com.bootdo.common.sms.RandomSmsValidateCode;
import com.bootdo.common.utils.*;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.system.domain.ContactDO;
import com.bootdo.system.domain.MenuDO;
import com.bootdo.system.domain.UserDO;
import com.bootdo.system.service.MenuService;
import com.bootdo.system.service.UserService;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class LoginController extends BaseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	MenuService menuService;
	@Autowired
	FileService fileService;
	@Autowired
	BootdoConfig bootdoConfig;
	@Autowired
	UserService userService;
	@Autowired
	OriginalApplicationService originalApplicationService;
	
	@Autowired
	RandomSmsValidateCode randomSmsValidateCode;
	@Value("${subdomain.match.domains}")
	String[] domains;

	@GetMapping({ "/", "" })
	@PostMapping({ "/", "" })
	String welcome(Model model,HttpServletRequest req) {
		StringBuffer url = req.getRequestURL();
		for(String item:domains) {
			int sline = url.indexOf(item);
			if(sline != -1) {
				String substr = url.substring(0, sline);
				if(substr.length() > 0) {
					int subline = substr.indexOf("https://");
					if(subline != -1) {
						substr = substr.replaceAll("https://", "");
					}else {
						substr = substr.replaceAll("http://", "");
					}
					logger.info("二级域名：{}",substr);
					if(!"WWW".equalsIgnoreCase(substr) && !"".equals(substr)) {
						String turl = substr.replaceAll("\\.", "");
						OriginalApplication org = originalApplicationService.getOriginalApplicationByShortDomain(turl);
						if(org != null) {
							logger.info("二级域名：{}",turl);
							return "forward:/signature/install/info";
						}
					}
				}
				return "forward:/404";
			}
		}
		logger.info("req client addr:{}",req.getRemoteAddr());
		return "index.html";
	}

	@Log("请求访问主页")
	@GetMapping({ "/index" })
	String index(Model model) {
		List<Tree<MenuDO>> menus = menuService.listMenuTree(getUserId());
		model.addAttribute("menus", menus);
		model.addAttribute("name", getUser().getName());
		FileDO fileDO = fileService.get(getUser().getPicId());
		if (fileDO != null && fileDO.getUrl() != null) {
			if (fileService.isExist(fileDO.getUrl())) {
				model.addAttribute("picUrl", fileDO.getUrl());
			} else {
				model.addAttribute("picUrl", "/img/head_icon.png");
			}
		} else {
			model.addAttribute("picUrl", "/img/head_icon.png");
		}
		model.addAttribute("username", getUser().getUsername());
		return "index_v1";
	}

	@GetMapping("/login")
	String login(Model model) {
		// model.addAttribute("username", bootdoConfig.getUsername());
		// model.addAttribute("password", bootdoConfig.getPassword());
		return "login";
	}

	@Log("登录")
	@PostMapping("/login")
	@ResponseBody
	R ajaxLogin(String username, String password, String verify, HttpServletRequest request) {

		try {
			// 从session中获取随机数
			String random = (String) request.getSession().getAttribute(RandomValidateCodeUtil.RANDOMCODEKEY);
			if (StringUtils.isBlank(verify)) {
				return R.error("请输入验证码");
			}
			if (random != null && random.equals(verify)) {
			} else {
				return R.error("请输入正确的验证码");
			}
		} catch (Exception e) {
			logger.error("验证码校验失败", e);
			return R.error("验证码校验失败");
		}
		password = MD5Utils.encrypt(username, password);
		UsernamePasswordToken token = new UsernamePasswordToken(username, password);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(token);
			return R.ok();
		} catch (AuthenticationException e) {
			return R.error("用户或密码错误");
		}
	}

	@Log("修改密码")
	@PostMapping("/backpass")
	@ResponseBody
	R ajaxBackPass(String username, String password, String verify, HttpServletRequest request) {
		try {
			// 从session中获取随机数
			String random = (String) request.getSession().getAttribute(RandomSmsValidateCode.BACKRANDOMCODEKEY);
			if (StringUtils.isBlank(verify)) {
				return R.error("请输入验证码");
			}
			if (random.equals(verify)) {
			} else {
				return R.error("请输入正确的验证码");
			}
		} catch (Exception e) {
			logger.error("验证码校验失败", e);
			return R.error("验证码校验失败");
		}
		password = MD5Utils.encrypt(username, password);
		UserDO user = userService.getByUsername(username);
		if (user != null) {
			user.setPassword(password);
			userService.update(user);
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			Subject subject = SecurityUtils.getSubject();
			try {
				subject.login(token);
				return R.ok();
			} catch (AuthenticationException e) {
				return R.error("用户或密码错误");
			}
		}else {
			return R.error().put("msg", "手机号未注册");
		}
	}

	@Log("注册")
	@PostMapping("/register")
	@ResponseBody
	R ajaxRegister(String username, String password, String verify, HttpServletRequest request) {
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(verify)) {
			return R.error().put("msg", "手机号/密码/验证码不能为空");
		}
		try {
			// 从session中获取随机数
			String random = (String) request.getSession().getAttribute(RandomValidateCodeUtil.RANDOMCODEKEY);
			if (StringUtils.isBlank(verify)) {
				return R.error("请输入验证码");
			}
			if (random.equals(verify)) {
			} else {
				return R.error("请输入正确的验证码");
			}
		} catch (Exception e) {
			logger.error("验证码校验失败", e);
			return R.error("验证码校验失败");
		}
		UserDO usernames = userService.getByUsername(username);
		if (usernames == null) {
			password = MD5Utils.encrypt(username, password);
			UserDO user = new UserDO();
			user.setPassword(password);
			user.setUsername(username);
			user.setDeptId(13L);
			user.setName(username);
			user.setStatus(1);
			user.setSex(96L);
			List<Long> roles = new ArrayList<Long>();
			roles.add(59L);
			user.setRoleIds(roles);
			userService.save(user);

			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			Subject subject = SecurityUtils.getSubject();
			subject.login(token);
			return R.ok().put("msg", "注册完成");
		} else {
			return R.error().put("msg", "注册失败,用户名已注册");
		}
	}

	@GetMapping("/logout")
	String logout() {
		ShiroUtils.logout();
		return "redirect:/login";
	}

	@GetMapping("/main")
	String main(Model model) {
		model.addAttribute("username", getUsername());
		return "main";
	}

	/**
	 * 生成验证码
	 */
	@GetMapping(value = "/getVerify")
	public void getVerify(HttpServletRequest request, HttpServletResponse response) {
		try {
			response.setContentType("image/jpeg");// 设置相应类型,告诉浏览器输出的内容为图片
			response.setHeader("Pragma", "No-cache");// 设置响应头信息，告诉浏览器不要缓存此内容
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expire", 0);
			RandomValidateCodeUtil randomValidateCode = new RandomValidateCodeUtil();
			randomValidateCode.getRandcode(request, response);// 输出验证码图片方法
		} catch (Exception e) {
			logger.error("获取验证码失败>>>> ", e);
		}
	}

	/**
	 * 生成短信验证码
	 * 
	 * @return
	 */
	@GetMapping(value = "/getSmsVerify")
	@ResponseBody
	public R getSmsVerify(String phone, HttpServletRequest request) {
		logger.info("phone:{}", phone);
		randomSmsValidateCode.createRandcode(phone, request);
		return R.ok();
	}

	/**
	 * 生成短信验证码
	 * 
	 * @return
	 */
	@GetMapping(value = "/getBackSmsVerify")
	@ResponseBody
	public R getBackSmsVerify(String phone, HttpServletRequest request) {
		logger.info("phone:{}", phone);
		randomSmsValidateCode.createBackRandcode(phone, request);
		return R.ok();
	}
}
