package com.bootdo.signature.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.aspectj.util.FileUtil;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.bootdo.common.utils.ExecTools;
import com.bootdo.common.utils.PlistTools;
import com.bootdo.signature.config.UDIDConfig;
import com.bootdo.signature.entity.po.OriginalApplication;
import com.bootdo.signature.service.OriginalApplicationService;
import com.bootdo.signature.util.URLUtil;
import com.dd.plist.PropertyListFormatException;

@RestController
@RequestMapping("/signature/udid/")
public class UDIDController {
	private static final Logger logger = LoggerFactory.getLogger(UDIDController.class);

	@Autowired
	private UDIDConfig udidConfig;
	
	@Autowired
	private OriginalApplicationService originalApplicationService;

	@PostMapping("/{code}/post")
	public @ResponseBody ResponseEntity<?> postUdid(@PathVariable("code") String code, HttpServletRequest req)
			throws DocumentException, ParserConfigurationException, ParseException, SAXException,
			PropertyListFormatException, IOException {
		String udid = readUDID(req.getInputStream());
		String domain = URLUtil.toDomain(req);
		logger.info("{},{}", code, udid);
		HttpHeaders headers = new HttpHeaders();
		if (code == null || udid == null) {
			headers.add(HttpHeaders.LOCATION, domain + "/404");
		} else {
			headers.add(HttpHeaders.LOCATION, domain + "/signature/install/" + code + "/appinfo?udid=" + udid);
		}
		return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).build();
	}

	@GetMapping("/{sudom}/get_new")
	public ResponseEntity<byte[]> getNewUdidDescriptionFile(@PathVariable("sudom") String sudom,HttpServletRequest req,
			HttpServletResponse resp) throws IOException, InterruptedException {
		if (sudom == null) {
			return null;
		}
		// 查找包信息
		OriginalApplication original = originalApplicationService.getOriginalApplicationByShortDomain(sudom);
		if (original == null) {
			return null;
		}
		
		String domain = URLUtil.toDomain(req);
		
		logger.info("request domain:{}",domain);
		Long appid = original.getAppId();
		File udidmobileConfigDir = new File("./udid");
		if (!udidmobileConfigDir.exists()) {
			udidmobileConfigDir.mkdir();
		}
		File tempFile = new File("./temp/" + UUID.randomUUID().toString());
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}
		// 创建未签名描述文件
		File file = ResourceUtils.getFile(udidConfig.getUdidTemplates());
		String fileContent = new String(FileUtil.readAsByteArray(file));
		fileContent = fileContent.replaceAll("#appid#", "" + appid).replaceAll("#requestURL#", "" + domain);
		String unsigudidPath = tempFile.getAbsolutePath() + "/" + appid + "unsigudid.mobileconfig";
		FileUtil.writeAsString(new File(unsigudidPath), fileContent);
		// 签名
		File sigudidFile = new File(tempFile.getAbsolutePath() + "/" + appid + "sigudid.mobileconfig");

		ExecTools.exec("openssl smime " + "-sign " + "-in " + unsigudidPath + " " + "-out "
				+ sigudidFile.getAbsolutePath() + " " + "-signer "
				+ ResourceUtils.getFile(udidConfig.getPublicCertificate()).getAbsolutePath() + " " + "-inkey "
				+ ResourceUtils.getFile(udidConfig.getPrivateKeyCertificate()).getAbsolutePath() + " " + "-certfile "
				+ ResourceUtils.getFile(udidConfig.getCACertificate()).getAbsolutePath() + " " + "-outform der " + "-nodetach");

		byte[] sigudidContent = FileUtil.readAsByteArray(sigudidFile);
		tempFile.delete();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("fileName", "udid.mobileconfig");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(sigudidContent, headers, HttpStatus.OK);
		return result;
	}

	@GetMapping("/{code}/get")
	public ResponseEntity<byte[]> getUdidDescriptionFile(@PathVariable("code") String code,String token,HttpServletRequest req, HttpServletResponse resp)
			throws IOException, InterruptedException {
		if (code == null) {
			return null;
		}
		OriginalApplication application = null;
		if(code.length() >= 8) {
			application = originalApplicationService.getOriginalApplicationByDownloadCode(code);	
		}else {
			application = originalApplicationService.getOriginalApplicationById(Long.parseLong(code));			
		}
		String verToken = (String) req.getSession().getAttribute("token");		

		if(application.getInstartModel() > 1 && verToken == null) {
			return null;
		}
		
		String domain = URLUtil.toDomain(req);
		if(verToken != null) {
			if(token == null || token != null && !token.equals(req.getSession().getAttribute("token"))) {
				return new ResponseEntity<byte[]>(HttpStatus.NOT_FOUND);
			}
		}
		
		req.getSession().removeAttribute("token");
		
		File udidmobileConfigDir = new File("./udid");
		if (!udidmobileConfigDir.exists()) {
			udidmobileConfigDir.mkdir();
		}

		File tempFile = new File("./temp/" + UUID.randomUUID().toString());
		if (!tempFile.exists()) {
			tempFile.mkdirs();
		}

		
		// 创建未签名描述文件
		File file = ResourceUtils.getFile(udidConfig.getUdidTemplates());
		String fileContent = new String(FileUtil.readAsByteArray(file));
		fileContent = fileContent.replaceAll("#appid#", code).replaceAll("#requestURL#", "" + domain).replaceAll("#appName#",application.getApplicationName());
		String unsigudidPath = tempFile.getAbsolutePath() + "/" + code + "unsigudid.mobileconfig";
		FileUtil.writeAsString(new File(unsigudidPath), fileContent);

		// 签名
		File sigudidFile = new File(tempFile.getAbsolutePath() + "/" + code + "sigudid.mobileconfig");

		ExecTools.exec("openssl smime " + "-sign " + "-in " + unsigudidPath + " " + "-out "
				+ sigudidFile.getAbsolutePath() + " " + "-signer "
				+ ResourceUtils.getFile(udidConfig.getPublicCertificate()).getAbsolutePath() + " " + "-inkey "
				+ ResourceUtils.getFile(udidConfig.getPrivateKeyCertificate()).getAbsolutePath() + " " + "-certfile "
				+ ResourceUtils.getFile(udidConfig.getCACertificate()).getAbsolutePath() + " " + "-outform der " + "-nodetach");

		byte[] sigudidContent = FileUtil.readAsByteArray(sigudidFile);
		sigudidFile.delete();
		tempFile.delete();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("fileName", "udid.mobileconfig");
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		ResponseEntity<byte[]> result = new ResponseEntity<byte[]>(sigudidContent, headers, HttpStatus.OK);
		return result;
	}

	public String readUDID(InputStream in) throws DocumentException, ParserConfigurationException, ParseException,
			SAXException, PropertyListFormatException, IOException {
		// 已HTTP请求输入流建立一个BufferedReader对象
		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		StringBuilder sb = new StringBuilder();

		// 读取HTTP请求内容
		String buffer = null;
		while ((buffer = br.readLine()) != null) {
			sb.append(buffer);
		}
		String content = sb.toString().substring(sb.toString().indexOf("<?xml"), sb.toString().indexOf("</plist>") + 8);
		return PlistTools.analysisUDID(content.getBytes());
	}
}
