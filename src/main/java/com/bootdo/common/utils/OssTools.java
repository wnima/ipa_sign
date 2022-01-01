package com.bootdo.common.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.oss.OSSClient;

@Component
public class OssTools {

	@Value("${oss.accessKeyId}")
	private String accessKeyId = "LTAIxX2NgQpjcw4P";
	
	@Value("${oss.accessKeySecret}")
	private String accessKeySecret = "Y9rk0rV9jYVWNA8I7RIFvWqbzyagy2";
	
	@Value("${oss.imgendpoint}")
	private String imgEndpoint;

	@Value("${oss.imgbucketName}")
	private String imgBucketName;

	@Value("${oss.imgurlPrefix}")
	public String imgUrlPrefix;
	
	@Value("${oss.ipaendpoint}")
	private String ipaEndpoint = "oss-cn-hangzhou.aliyuncs.com";
	
	@Value("${oss.ipabucketName}")
	private String ipaBucketName = "sg-lin-new";

	@Value("${oss.ipaurlPrefix}")
	public String ipaUrlPrefix = "https://e3wl.com/";
	
	@Value("${oss.effectiveTime}")
	public int effectiveTime = 2;
	
	public String createIPAUrl(String key){
		OSSClient ossClient = new OSSClient(ipaEndpoint, "LTAI4Fzo6HFfXxjJdJW1uRzk", "Ws0GE6cdbwMZyBy3d9WhhRWC4f7kZB");
		URL url = ossClient.generatePresignedUrl(ipaBucketName, key, new Date(System.currentTimeMillis() + (effectiveTime * 60 * 1000)));
		int index = url.toString().indexOf("aliyuncs.com/");
		return ipaUrlPrefix.concat(url.toString().substring(index).replaceFirst("aliyuncs.com/", ""));
	}
	
	public String createIPAUrlNoAuth(String key){
		return ipaUrlPrefix.concat(key);
	}
	
	public String uploadIPAFile(String fileName, String filePath) throws IOException {
		return uploadIPAFile(fileName, new BufferedInputStream(new FileInputStream(filePath)));
	}

	public String uploadIPAFile(String fileName, InputStream in) throws IOException {
		OSSClient ossClient = new OSSClient(ipaEndpoint, accessKeyId, accessKeySecret);
		ossClient.putObject(ipaBucketName, fileName, in);
		ossClient.shutdown();
		return ipaUrlPrefix + fileName;
	}
	
	public String uploadIMGFile(String fileName, String filePath) throws IOException {
		return uploadIMGFile(fileName, new BufferedInputStream(new FileInputStream(filePath)));
	}
	
	public String uploadIMGFile(String fileName, InputStream in) throws IOException {
		OSSClient ossClient = new OSSClient(imgEndpoint, accessKeyId, accessKeySecret);
		ossClient.putObject(imgBucketName, fileName, in);
		ossClient.shutdown();
		return imgUrlPrefix + fileName;
	}

	
	public static void main(String[] args) throws IOException {
		String url = new OssTools().createIPAUrl("temp_0844f97c-df97-4bd0-adee-e74c820470ff.ipa");
		System.out.println(url);
	}
}
