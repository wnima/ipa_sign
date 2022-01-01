package com.bootdo.system.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 验证拦截器
 * 
 * @author HUANG
 */
public class CCInterceptor implements HandlerInterceptor {
	private final static Logger logger = LoggerFactory.getLogger(CCInterceptor.class);
	private Map<String, Long> date = new HashMap<>();
	private Map<String, Integer> count = new HashMap<>();

	/***
	 * 校验IP是否加入黑名单
	 * 
	 * @param ip
	 * @return true 是在黑名单
	 * @throws IOException
	 */
	public boolean chickIpBreak(String url,String ip) throws IOException {
		Long lastTime = date.get(ip);
		if (lastTime == null) {
			lastTime = 0L;
		}
		Integer tcount = count.get(ip);
		if (tcount == null) {
			tcount = 0;
		}
		long currTime = System.currentTimeMillis() / 1000 / 60;
		if (currTime == lastTime) {
			tcount++;
			count.put(ip, tcount);
			if(tcount > 100) {
				logger.info("url:{} ip:{} curr:{} count:{}",url, ip, currTime,tcount);
				return true;
			}else {
				return false;				
			}
		} else {
			date.put(ip, currTime);
			count.put(ip, 1);
			return false;
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String ip = getIpAddress(request);
		Map<String, String> map = getParameterMap(request);// 获取url中的所有参数
		String servletUrl = request.getServletPath();// servlet地址
		String url = getRealUrl(servletUrl, map);
		if (chickIpBreak(url, ip)) {
			response.sendRedirect(
					"http://download.microsoft.com/download/B/8/F/B8F1470D-2396-4E7A-83F5-AC09154EB925/vs2015.ent_chs.iso");
			return false;
		}
		return true;
	}

	/**
	 * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public final static String getIpAddress(HttpServletRequest request) throws IOException {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} else if (ip.length() > 15) {
			String[] ips = ip.split(",");
			for (int index = 0; index < ips.length; index++) {
				String strIp = (String) ips[index];
				if (!("unknown".equalsIgnoreCase(strIp))) {
					ip = strIp;
					break;
				}
			}
		}
		return ip;
	}

	/**
	 * 根据request获取所有的参数集
	 * 
	 * @param request
	 * @return
	 */
	protected Map<String, String> getParameterMap(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		String name;
		Map<String, String> map = new HashMap<String, String>();
		while (names.hasMoreElements()) {
			name = names.nextElement();
			map.put(name, request.getParameter(name).trim().replaceAll("'", ""));
		}
		return map;
	}

	/**
	 * 获取url
	 * 
	 * @param uri
	 * @param params
	 * @return
	 */
	String getRealUrl(String uri, Map<String, String> params) {
		StringBuffer sb = new StringBuffer(uri);
		if (params != null) {
			int i = 0;
			for (String key : params.keySet()) {
				i++;
				if (i == 1) {
					sb.append("?" + key + "=" + params.get(key));
				} else {
					sb.append("&" + key + "=" + params.get(key));
				}
			}
		}
		return sb.toString();
	}
}