package com.bootdo.signature.util;

import javax.servlet.http.HttpServletRequest;

public class URLUtil {

	public static String toDomain(HttpServletRequest req) {
		String requestURL = req.getRequestURL().toString();
		String tempstr = requestURL.replaceAll("https://", "").replaceAll("http://", "");
		tempstr = tempstr.split("\\?")[0];
		tempstr = tempstr.split("/")[0];
		if(requestURL.indexOf("https://") != -1) {
			tempstr = "https://" + tempstr;
		}else {
			tempstr = "https://" + tempstr;
		}
		return tempstr;
	}

}
