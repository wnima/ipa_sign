package com.bootdo.signature.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UDIDConfig {
	@Value("${signature.udid.templates}")
	private String udidTemplates;
	
	@Value("${signature.host}")
	private String host;
	
	@Value("${signature.bakhost}")
	private String bakhost;
	
	@Value("${signature.udid.publicCertificate}")
	private String publicCertificate;

	@Value("${signature.udid.CACertificate}")
	private String CACertificate;

	@Value("${signature.udid.privateKeyCertificate}")
	private String privateKeyCertificate;

	public String getBakhost() {
		return bakhost;
	}

	public void setBakhost(String bakhost) {
		this.bakhost = bakhost;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUdidTemplates() {
		return udidTemplates;
	}

	public void setUdidTemplates(String udidTemplates) {
		this.udidTemplates = udidTemplates;
	}

	public String getPublicCertificate() {
		return publicCertificate;
	}

	public void setPublicCertificate(String publicCertificate) {
		this.publicCertificate = publicCertificate;
	}

	public String getCACertificate() {
		return CACertificate;
	}

	public void setCACertificate(String cACertificate) {
		CACertificate = cACertificate;
	}

	public String getPrivateKeyCertificate() {
		return privateKeyCertificate;
	}

	public void setPrivateKeyCertificate(String privateKeyCertificate) {
		this.privateKeyCertificate = privateKeyCertificate;
	}
}
