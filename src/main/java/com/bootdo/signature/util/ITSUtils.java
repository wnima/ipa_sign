package com.bootdo.signature.util;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.bootdo.common.utils.ExecTools;
import com.bootdo.common.utils.ExecTools.ExecResult;
import com.bootdo.signature.exception.APIException;
import com.bootdo.common.utils.Randomizer;

import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

public class ITSUtils {

	/** 账号最大可容纳设备量 */
	public final static int DEVICES_MAX = 100;

	/**
	 * create by: iizvv description: 获取帐号信息 create time: 2019-06-29 15:13
	 * @return number：可用数量， udids已有设备, cerId证书id
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	public static Map<String, Object> initAccount(Authorize authorize,String csrPath) throws IOException, InterruptedException, APIException {
		Map<String, String> header = getToken(authorize.getP8(), authorize.getIss(), authorize.getKid());
		Map<String, Object> res = new HashMap<String, Object>();
		String url = "https://api.appstoreconnect.apple.com/v1/devices";
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(header).execute().body();
		Map<String, Object> map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		JSONArray data = (JSONArray) map.get("data");
		List<Map<String, String>> devices = new LinkedList<Map<String, String>>();
		for (Object datum : data) {
			Map<String, String> device = new HashMap<String, String>();
			Map m = (Map) datum;
			String id = (String) m.get("id");
			Map attributes = (Map) m.get("attributes");
			String udid = (String) attributes.get("udid");
			device.put("deviceId", id);
			device.put("udid", udid);
			devices.add(device);
		}
		
		Map<String, List<Pair<String, String>>> cerMap = getCertificates(header);
		//Pair<String, String> bundlepair = getBundleIds(header);
		String certificateType = "IOS_DEVELOPMENT";
		if(!cerMap.containsKey("IOS_DISTRIBUTION")) {
			certificateType = "IOS_DISTRIBUTION";
		}
		
		File key = new File(csrPath,UUID.randomUUID().toString() + ".key");
		File csr = new File(csrPath,UUID.randomUUID().toString() + ".csr");
		long email = Randomizer.nextLong(111111111111L, 999999999999L);
		ExecResult exelog = ExecTools.exec("openssl genrsa -out " + key.getAbsolutePath() + " 2048");
		//System.out.println(exelog);
		exelog = ExecTools.exec("openssl req -new -key " + key.getAbsolutePath() + " -out " + csr.getAbsolutePath()
		+ " -subj /emailAddress=" + email + "@qq.com/CN=CommonName/C=CN");
		//System.out.println(exelog);
		Pair<String, String> cerpair = insertCertificates(header, FileUtils.readFileToString(csr, Charset.forName("UTF-8")),certificateType);
		
		
		Pair<String, String> bundlepair = insertBundleIds(header,"com.supersig." + Long.toHexString(System.currentTimeMillis()) + ".app");
		

		String publicKey = convertToPem("CERTIFICATE", cerpair.getValue());
		
		Map<String, Object> meta = (Map<String, Object>) map.get("meta");
		Map<String, Object> paging = (Map<String, Object>) meta.get("paging");
		int total = (int) paging.get("total");
		res.put("number", total);
		res.put("devices", devices);
		res.put("cerId", cerpair.getKey());
		res.put("publicKey", publicKey);
		res.put("privateKey",FileUtils.readFileToString(key,Charset.forName("UTF-8")));
		res.put("bundleIds", bundlepair.getKey());
		res.put("packageName", bundlepair.getValue());
		res.put("certificateType", certificateType);
		csr.delete();
		key.delete();
		
		return res;
	}

	/**
	 * create by: iizvv description: 添加设备 create time: 2019-06-29 15:14
	 *
	 * 
	 * @return String
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	public static String insertDevice(String udid, Map<String, String> header) throws APIException {
		Map body = new HashMap();
		body.put("type", "devices");
		Map attributes = new HashMap();
		attributes.put("name", udid);
		attributes.put("udid", udid);
		attributes.put("platform", "IOS");
		body.put("attributes", attributes);
		Map data = new HashMap();
		data.put("data", body);
		String url = "https://api.appstoreconnect.apple.com/v1/devices";
		String result = HttpRequest.post(url).setConnectionTimeout(10000)
				.addHeaders(header)
				.body(JSON.toJSONString(data)).execute().body();
		Map map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		Map data1 = (Map) map.get("data");
		String id = (String) data1.get("id");
		return id;
	}

	/**
	 * create by: iizvv description: 创建个人资料 create time: 2019-06-29 15:33
	 *
	 * 
	 * @return File
	 * @throws APIException 
	 * @throws AccountBlockException 
	 * @throws APIReqParamsErrorException 
	 */
	public static String insertProfile(Map header, String devId,String bundleIds,String cerId,String cerType) throws APIException {
		String profileType = "IOS_APP_DEVELOPMENT";
		if("IOS_DISTRIBUTION".equals(cerType)) {
			profileType ="IOS_APP_ADHOC";
		}

		//String bodyJson = "{\"data\":{\"relationships\":{\"certificates\":{\"data\":[{\"id\":\""+cerId+"\",\"type\":\"certificates\"}]},\"devices\":{\"data\":[{\"id\":\""+devId+"\",\"type\":\"devices\"}]},\"bundleId\":{\"data\":{\"id\":\""+bundleIds+"\",\"type\":\"bundleIds\"}}},\"attributes\":{\"profileType\":\""+profileType+"\",\"name\":\""+UUID.randomUUID().toString().replace("-", "")+"\"},\"type\":\"profiles\"}}";
		Map<String, Object> body = new HashMap();
		body.put("type", "profiles");
		String name = UUID.randomUUID().toString().replace("-", "");
		Map<String, Object> attributes = new HashMap();
		attributes.put("name", name);
		attributes.put("profileType", profileType);
		body.put("attributes", attributes);
		Map<String, Object> relationships = new HashMap();
		Map<String, Object> bundleId = new HashMap();
		Map<String, Object> data2 = new HashMap();
		data2.put("id", bundleIds);
		data2.put("type", "bundleIds");
		bundleId.put("data", data2);
		relationships.put("bundleId", bundleId);
		Map<String, Object> certificates = new HashMap();
		Map<String, Object> data3 = new HashMap();
		data3.put("id", cerId);
		data3.put("type", "certificates");
		List<Map<String, Object>> list = new LinkedList();
		list.add(data3);
		certificates.put("data", list);
		relationships.put("certificates", certificates);
		Map<String, Object> devices = new HashMap();
		Map<String, Object> data4 = new HashMap();
		data4.put("id", devId);
		data4.put("type", "devices");
		List<Map<String, Object>> list2 = new LinkedList();
		list2.add(data4);
		devices.put("data", list2);
		relationships.put("devices", devices);
		body.put("relationships", relationships);
		Map<String, Object> data = new HashMap();
		data.put("data", body);
		String url = "https://api.appstoreconnect.apple.com/v1/profiles";
		String result = HttpRequest.post(url).setConnectionTimeout(10000).addHeaders(header)
				.body(JSON.toJSONString(data)).execute().body();
		Map<String, Object> map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		Map<String, Object> o = (Map) map.get("data");
		Map<String, Object> o2 = (Map) o.get("attributes");
		String profileContent = (String) o2.get("profileContent");
		//File file = base64ToFile(profileContent, path + name + ".mobileprovision");
		return profileContent;
	}

	/**
	 * create by: iizvv description: 删除当前帐号下的所有证书 create time: 2019-06-29 16:00
	 *
	 * 
	 * @return void
	 */
	static void removeCertificates(String id,Map<String, String> header) {
		String url = "https://api.appstoreconnect.apple.com/v1/certificates";
		String result2 = HttpRequest.delete(url + "/" + id).setConnectionTimeout(10000).addHeaders(header).execute().body();
	}

	/**
	 * create by: iizvv description: 删除所有绑定的ids create time: 2019-07-01 14:41
	 *
	 * @return void
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	static void removeBundleIds(Map header) throws APIException {
		String url = "https://api.appstoreconnect.apple.com/v1/bundleIds";
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(header).execute().body();
		System.out.println(result);
		Map map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		JSONArray data = (JSONArray) map.get("data");
		for (Object datum : data) {
			Map m = (Map) datum;
			String id = (String) m.get("id");
			String result2 = HttpRequest.delete(url + "/" + id).setConnectionTimeout(10000).addHeaders(header).execute().body();
		}
	}

	/**
	 * create by: iizvv description: base64转文件 create time: 2019-07-04 17:12
	 * 
	 * @return File
	 */
	static File base64ToFile(String base64, String fileName) {
		File file = null;
		BufferedOutputStream bos = null;
		java.io.FileOutputStream fos = null;
		try {
			byte[] bytes = Base64.decodeBase64(base64);
			file = new File(fileName);
			fos = new java.io.FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * create by: iizvv description: 创建证书 create time: 2019-06-29 16:34
	 *
	 * 
	 * @return 创建的证书id
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	static Pair<String, String> insertCertificates(Map header, String csr,String certificateType) throws APIException {
		String url = "https://api.appstoreconnect.apple.com/v1/certificates";
		Map body = new HashMap();
		body.put("type", "certificates");
		Map attributes = new HashMap();
		attributes.put("csrContent", csr);
		attributes.put("certificateType", certificateType);
		body.put("attributes", attributes);
		Map data = new HashMap();	
		data.put("data", body);
		String result = HttpRequest.post(url).setConnectionTimeout(10000).addHeaders(header).body(JSON.toJSONString(data)).execute().body();
		Map map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		Map data1 = (Map) map.get("data");
		String id = (String) data1.get("id");
		Map attr = (Map) data1.get("attributes");
		String content = (String) attr.get("certificateContent");
		return new Pair<String, String>(id, content);
	}

	public static String getProfiles(Authorize authorize) throws APIException{
		String url = "https://api.appstoreconnect.apple.com/v1/profiles";
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(getToken(authorize.getP8(), authorize.getIss(), authorize.getKid())).execute().body();
		Map map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		List data1 = (List) map.get("data");
		if(data1.size() == 0) {
			System.out.println(data1.size());
		}
		return null;
	}
	
	public static List<Map<String,Object>> getDevices(Map header) throws APIException {
		String url = "https://api.appstoreconnect.apple.com/v1/devices?limit=200";
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(header).execute().body();
		Map<String,Object> map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		List<Map<String,Object>> data1 = (List<Map<String,Object>>) map.get("data");
		List<Map<String,Object>> devices = new ArrayList<>();
		for(Map<String,Object> item : data1) {
			devices.add(new HashMap<String,Object>(item));
		}
		return devices;
	}
	
	/**
	 * description: 下载证书 create time: 2019-06-29 16:34
	 * @return 下载证书
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	public static String downloadCertificates(Authorize authorize, String cerId) throws APIException{
		String url = "https://api.appstoreconnect.apple.com/v1/certificates/"+cerId;
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(getToken(authorize.getP8(), authorize.getIss(), authorize.getKid())).execute().body();
		Map map = JSON.parseObject(result, Map.class);
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		Map data1 = (Map) map.get("data");
		Map attributes = (Map) data1.get("attributes");
		String content = (String) attributes.get("certificateContent");
		return content;
	}
	
	public static Map<String,List<Pair<String, String>>> getCertificates(Map<String, String> header) throws APIException{
		String url = "https://api.appstoreconnect.apple.com/v1/certificates";
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(header).execute().body();
		Map<String,List<Pair<String, String>>> cerMap = new HashMap<>();
		Map<String,List<Map<String,Object>>> map = JSON.parseObject(result, Map.class);
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		List<Map<String,Object>> data1 = map.get("data");
		if(data1.size() > 0) {
			for(Map<String, Object> cer:data1) {
				Map<String,Object> attributes = (Map<String, Object>) cer.get("attributes");
				if("IOS_DEVELOPMENT".equals(attributes.get("certificateType"))) {
					List<Pair<String, String>> deveList = cerMap.get("IOS_DEVELOPMENT");
					if(deveList == null) {
						deveList = new ArrayList<>();
						cerMap.put("IOS_DEVELOPMENT",deveList);
					}
					deveList.add(new Pair<String, String>(cer.get("id").toString(), attributes.get("certificateContent").toString()));
				}
				if("IOS_DISTRIBUTION".equals(attributes.get("certificateType"))) {
					List<Pair<String, String>> deveList = cerMap.get("IOS_DISTRIBUTION");
					if(deveList == null) {
						deveList = new ArrayList<>();
						cerMap.put("IOS_DISTRIBUTION",deveList);
					}
					deveList.add(new Pair<String, String>(cer.get("id").toString(), attributes.get("certificateContent").toString()));
				}
			}
		}
		return cerMap;
	}
	
	static void enableCapability(Map<String,String> header,String bundleIds,String capabilityType) throws APIException {
		String url = "https://api.appstoreconnect.apple.com/v1/bundleIdCapabilities";
		Map<String,Object> data = new HashMap<>();
		data.put("type", "bundleIdCapabilities");
		Map<String,Object> attributes = new HashMap<>();
		attributes.put("capabilityType",capabilityType);
		data.put("attributes", attributes);
		Map<String,Object> relationships = new HashMap<>();
		Map<String,Object> bundleId = new HashMap<>();
		Map<String,Object> bundleIdData = new HashMap<>();
		bundleIdData.put("id", bundleIds);
		bundleIdData.put("type", "bundleIds");
		bundleId.put("data", bundleIdData);
		relationships.put("bundleId", bundleId);
		data.put("relationships", relationships);
		Map<String,Object> body = new HashMap<>();
		body.put("data", data);
		String jsonbody = JSON.toJSONString(body);
		String result = HttpRequest.post(url).setConnectionTimeout(10000).addHeaders(header).body(jsonbody).execute().body();
		Map<String, Object> map = JSON.parseObject(result, Map.class);
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
	}
	
	/**
	 * create by: iizvv description: 创建BundleIds create time: 2019-07-01 15:00
	 *
	 * @return id
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	static Pair<String, String> insertBundleIds(Map<String,String> header,String packageName) throws APIException{
		String url = "https://api.appstoreconnect.apple.com/v1/bundleIds";
		Map body = new HashMap();
		body.put("type", "bundleIds");
		
		Map attributes = new HashMap();
		attributes.put("identifier", packageName);
		attributes.put("name", "AppBundleId");
		attributes.put("platform", "IOS");
		body.put("attributes", attributes);
		Map data = new HashMap();
		data.put("data", body);
		String jsonbody = JSON.toJSONString(data);
		String result = HttpRequest.post(url).setConnectionTimeout(10000).addHeaders(header).body(jsonbody).execute().body();
		Map map = JSON.parseObject(result, Map.class);
		
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		
		Map data1 = (Map) map.get("data");
		String id = (String) data1.get("id");
		Map<String,Object> attr = (Map<String,Object>) data1.get("attributes");
		String identifier = attr.get("identifier").toString();
		//enableCapability(header, id, "ICLOUD");
		//enableCapability(header, id, "IN_APP_PURCHASE");
		//enableCapability(header, id, "GAME_CENTER");
		enableCapability(header, id, "PUSH_NOTIFICATIONS");
		//enableCapability(header, id, "WALLET");
		//enableCapability(header, id, "INTER_APP_AUDIO");
		//enableCapability(header, id, "MAPS");
		//enableCapability(header, id, "ASSOCIATED_DOMAINS");
		enableCapability(header, id, "PERSONAL_VPN");
		enableCapability(header, id, "APP_GROUPS");
		//enableCapability(header, id, "HEALTHKIT");
		//enableCapability(header, id, "HOMEKIT");
		//enableCapability(header, id, "WIRELESS_ACCESSORY_CONFIGURATION");
		//enableCapability(header, id, "APPLE_PAY");
		//enableCapability(header, id, "DATA_PROTECTION");
		//enableCapability(header, id, "SIRIKIT");
		enableCapability(header, id, "NETWORK_EXTENSIONS");
		//enableCapability(header, id, "MULTIPATH");
		//enableCapability(header, id, "HOT_SPOT");
		//enableCapability(header, id, "NFC_TAG_READING");
		//enableCapability(header, id, "CLASSKIT");
		//enableCapability(header, id, "AUTOFILL_CREDENTIAL_PROVIDER");
		//enableCapability(header, id, "ACCESS_WIFI_INFORMATION");
		return new Pair<String, String>(id, identifier);
	}
	
	public static Pair<String, String> getBundleIds(Map<String,String> header) throws APIException{
		String url = "https://api.appstoreconnect.apple.com/v1/bundleIds";
		String result = HttpRequest.get(url).setConnectionTimeout(10000).addHeaders(header).execute().body();
		Map map = JSON.parseObject(result, Map.class);
		if(map.containsKey("errors")) {
			List errorsProp = (List) map.get("errors");
			Map error = (Map) errorsProp.get(0);
			throw new APIException(error.get("code").toString(), map.toString());
		}
		List<Map<String,Object>> data1 = (List) map.get("data");
		if(data1.size() > 0) {
			for(Map<String,Object> item:data1) {
				String id = item.get("id").toString();
				Map<String,Object> attributes = (Map<String,Object>) item.get("attributes");
				String identifier = attributes.get("identifier").toString();
				return new Pair<String, String>(id, identifier);
			}
		}
		return null;
	}

	static String convertToPem(String sufix,String derCert) {
		 Base64 encoder = new Base64(64);
		 String cert_begin = "-----BEGIN "+sufix+"-----\n";
		 String end_cert = "-----END "+sufix+"-----";
		 String pemCertPre = new String(encoder.encode(Base64.decodeBase64(derCert)));
		 String pemCert = cert_begin + pemCertPre + end_cert;
		 return pemCert;
		}
	
	/**
	 * @return 请求头
	 */
	public static Map getToken(String p8, String iss, String kid) {
		String s = p8.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
		byte[] encodeKey = Base64.decodeBase64(s);
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encodeKey);
		KeyFactory keyFactory = null;
		try {
			keyFactory = KeyFactory.getInstance("EC");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		PrivateKey privateKey = null;
		try {
			privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		String token = null;
		token = Jwts.builder().setHeaderParam(JwsHeader.ALGORITHM, "ES256").setHeaderParam(JwsHeader.KEY_ID, kid)
				.setHeaderParam(JwsHeader.TYPE, "JWT").

				setIssuer(iss).claim("exp", System.currentTimeMillis() / 1000 + 60 * 10)
				.setAudience("appstoreconnect-v1")
				.signWith(SignatureAlgorithm.ES256, privateKey).compact();
		Map map = new HashMap();
		map.put("Content-Type", "application/json");
		map.put("Authorization", "Bearer " + token);
		return map;
	}
	
	/**
	 * 添加设备并返回对应的描述文件
	 * @param udid
	 * @param authorize
	 * @return 描述文件
	 * @throws APIException 
	 * @throws AccountBlockException 
	 * @throws APIReqParamsErrorException 
	 */
	public static Pair<Integer, String> addDeviceAndInsertProfile(String udid,Authorize authorize) throws APIException{
		int regNum = 0;
		Map header = getToken(authorize.getP8(), authorize.getIss(), authorize.getKid());
		// 获取所有设备数，是否包含udid
		List<Map<String, Object>> devices = getDevices(header);
		
		String devId = null;
		for(Map<String, Object> device:devices) {
			Map<String,Object> att = (Map<String,Object>) device.get("attributes");
			String tudid = att.get("udid").toString();
			if(tudid.equalsIgnoreCase(udid)) {
				devId = device.get("id").toString();
				break;
			}
		}
		regNum = devices.size();
		if(devId == null) {
			if(devices.size() < DEVICES_MAX) {
				devId = insertDevice(udid, header);
				regNum++;
			}else {
				return null;
			}
		}
		String mob = insertProfile(header, devId,authorize.getBundleIds(),authorize.getCerId(),authorize.getCerType());
		return new Pair<Integer, String>(regNum, mob);
	}
	
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
xpxb973@163.com
hubeiHB12345
MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgn7farcuTwvOfK1kr
Cmg6YZfa79bldeKBL5zFQ8EKSEegCgYIKoZIzj0DAQehRANCAASQbrg3shuqJRak
Lfct+vSB8mCGIJGnPLDoLc+D0kJPOz25Aoh9bO0fgEOpV6vyIrjXbSlcSXghdYIe
9UWTxDUn
df27c094-6313-4cfb-93e4-8538b97a66e0
Z92PA2A2ND
	 * 
	 * 
	 * 
	 * 
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws APIException 
	 * @throws AccountBlockException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, APIException {
		Authorize authorize = new Authorize("MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgEvYLJRSOj0+IJV+7 UzGrjwG7ST8z+Uh5aPZ72bLlCeOgCgYIKoZIzj0DAQehRANCAASXe7lNJ1QQ3Ed/ CT0pjtvMogiwDYxgjJbLM8scXSjk1yzf9TJqDc1C95azTxOfk3KFqO1ImE5TxOO4 P6df9RFD"
				+ "", "d387d72f-74ca-4df4-b444-8059c2d3831e", "27554SKDN6","D7Y72K74U7","PBAN453CK6","IOS_DISTRIBUTION");
		Map<String,String> headMap = getToken("MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQg+hpWU+1EOJIrP5zi\r\n" + 
				"oBXA8W5eDCUOXXGZgMlq9Z/wVFGgCgYIKoZIzj0DAQehRANCAAS3jT6d32LKIu4E\r\n" + 
				"Tr5YcTH7LdRENWT/2o0Qu6FCayuoV4OUSitA/+XIuD3iXItq+J6nrrhpYuj3vVlA\r\n" + 
				"2OFVdsy0","507eeebd-0203-49ec-b02a-e0233fbd275c", "8JR4PAD8FF");
		System.out.println(headMap);
		Map<String, List<Pair<String, String>>> result = getCertificates(headMap);
		System.out.println(result);
	}

	public static boolean isBlockade(Authorize authorize) {
		try {
			getCertificates(getToken(authorize.getP8(), authorize.getIss(), authorize.getKid()));
			return false;
		} catch (APIException e) {
			switch (e.getStatus()) {
			case APIException.REQ_PARAMS_ERROR:
				
				break;
			case APIException.UNEXPECTED_ERROR:
				
				break;
			case APIException.NOT_AUTHORIZED:				
				return true;
			default:
				break;
			}
		}
		return false;
	}

}
