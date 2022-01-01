package com.bootdo.common.sms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.bootdo.common.utils.Randomizer;

import net.sf.json.JSONObject;

@Component
public class RandomSmsValidateCode {
	private static Logger logger = LoggerFactory.getLogger(RandomSmsValidateCode.class);
	public static final String RANDOMCODEKEY = "RANDOMSMSVALIDATECODEKEY";//放到session中的key
	public static final String BACKRANDOMCODEKEY = "BACKRANDOMSMSVALIDATECODEKEY";//放到session中的key
	public static enum TemplateCode {
		REGISTER("SMS_188555697"),
		;

		private String val;

		private TemplateCode(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}
	}
	
	// 地域ID
	@Value("${sms.regionid}")
	private String regionId;
	
	@Value("${sms.accesskeyid}")
	private String accessKeyId;
	
	@Value("${sms.secret}")
	private String secret;
	
	@Value("${sms.signname}")
	private String signName;
	
	// 默认
	private String domain = "dysmsapi.aliyuncs.com";
	private String version = "2017-05-25";
	private MethodType method = MethodType.POST;
	private String action = "SendSms";

	public boolean send(String code, String phoneNumber, TemplateCode templateCode) {
		logger.info("send sms regionId:{} accessKeyId:{} secret:{} signName:{} code:{} phone:{} templateCode:{}",regionId,accessKeyId,secret,signName,code,phoneNumber,templateCode);
		DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, secret);
		IAcsClient client = new DefaultAcsClient(profile);
		CommonRequest request = new CommonRequest();
		request.setMethod(method);
		request.setDomain(domain);
		request.setVersion(version);
		request.setAction(action);
		request.putQueryParameter("RegionId", regionId);
		request.putQueryParameter("PhoneNumbers", phoneNumber);
		request.putQueryParameter("SignName", signName);
		request.putQueryParameter("TemplateCode", templateCode.getVal());
		request.putQueryParameter("TemplateParam", "{\"code\":" + code + "}");
		try {
			CommonResponse response = client.getCommonResponse(request);
			logger.info("response status:{} message:{}", response.getHttpStatus(), response.getData());
			if (response.getHttpStatus() == 200) {
				JSONObject data = JSONObject.fromObject(response.getData());
				return "OK".equals(data.get("Code"));
			}
			return false;
		} catch (ClientException e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public void createRandcode(String phomeNumber,HttpServletRequest request) {
        HttpSession session = request.getSession();
        String validateCode = Randomizer.nextInt(100000, 999999) + "";
        send(validateCode, phomeNumber, TemplateCode.REGISTER);
        session.setAttribute(RANDOMCODEKEY, validateCode);
    }
	
	public void createBackRandcode(String phomeNumber,HttpServletRequest request) {
        HttpSession session = request.getSession();
        String validateCode = Randomizer.nextInt(100000, 999999) + "";
        send(validateCode, phomeNumber, TemplateCode.REGISTER);
        session.setAttribute(BACKRANDOMCODEKEY, validateCode);
    }
	
	public String getRandcode(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(RANDOMCODEKEY);
    }
	
	public String getBackRandcode(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (String) session.getAttribute(BACKRANDOMCODEKEY);
    }
	
	public static void main(String[] args) {
		RandomSmsValidateCode ran = new RandomSmsValidateCode();
		ran.accessKeyId="LTAIxX2NgQpjcw4P";
		ran.regionId="cn-hangzhou";
		ran.secret="Y9rk0rV9jYVWNA8I7RIFvWqbzyagy2";
		ran.signName="一单元电子";
		ran.send("111111", "13612900684",TemplateCode.REGISTER);
	}
}
