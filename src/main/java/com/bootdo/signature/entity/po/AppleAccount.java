package com.bootdo.signature.entity.po;

public class AppleAccount {
	private long id;
	private String username;
	private String password;
	private String phoneNumber;
	private String certificate;
	private String keyOfCertificate;
	private String descriptionFile;
	private long registerNumber;
	private String packageName;
	private boolean enable;
	private boolean blockade;
	private String loginstate;
	private String p8;
	private String iss;
	private String kid;
	private String bundleId;
	private String cerId;
	private String cerType;
	private String csr;
	private boolean use;
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public boolean isUse() {
		return use;
	}
	public void setUse(boolean use) {
		this.use = use;
	}
	public String getCerType() {
		return cerType;
	}
	public void setCerType(String cerType) {
		this.cerType = cerType;
	}
	public String getCsr() {
		return csr;
	}
	public void setCsr(String csr) {
		this.csr = csr;
	}
	public String getBundleId() {
		return bundleId;
	}
	public void setBundleId(String bundleId) {
		this.bundleId = bundleId;
	}
	public String getCerId() {
		return cerId;
	}
	public void setCerId(String cerId) {
		this.cerId = cerId;
	}
	public String getP8() {
		return p8;
	}
	public void setP8(String p8) {
		this.p8 = p8;
	}
	public String getIss() {
		return iss;
	}
	public void setIss(String iss) {
		this.iss = iss;
	}
	public String getKid() {
		return kid;
	}
	public void setKid(String kid) {
		this.kid = kid;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public long getRegisterNumber() {
		return registerNumber;
	}
	public void setRegisterNumber(long registerNumber) {
		this.registerNumber = registerNumber;
	}

	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getKeyOfCertificate() {
		return keyOfCertificate;
	}
	public void setKeyOfCertificate(String keyOfCertificate) {
		this.keyOfCertificate = keyOfCertificate;
	}
	public String getDescriptionFile() {
		return descriptionFile;
	}
	public void setDescriptionFile(String descriptionFile) {
		this.descriptionFile = descriptionFile;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	public boolean isBlockade() {
		return blockade;
	}
	public void setBlockade(boolean blockade) {
		this.blockade = blockade;
	}
	public String getLoginstate() {
		return loginstate;
	}
	public void setLoginstate(String loginstate) {
		this.loginstate = loginstate;
	}
}
