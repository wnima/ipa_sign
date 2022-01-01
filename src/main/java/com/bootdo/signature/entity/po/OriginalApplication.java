package com.bootdo.signature.entity.po;

public class OriginalApplication {
	private Long appId;
	private String packageName;
	private String organizationName;
	private String applicationName;
	private String filePath;
	private String iconUrl;
	private Long fileSize;
	private String version;
	private Boolean enable;
	private String type;
	private Integer mark;// 评分
	private String shop;
	private String introduce;// 介绍
	private Long userId;
	private Long createTime;
	private String minVersion;
	private String remarkName;
	private String showIconUrl;
	private String showAppName;
	private String shortDomain;
	private String screenshots;
	private String downloadId;
	private boolean push = true;
	private Integer appType;
	private String fileUrl;
	private String udidTitle;
	private String udidDescription;
	private int downloadNum;
	private int realDownNum;
	private String androidFileUrl;
	private int instartModel;
	private String instartPassword;
	private String area;
	private String downloadCode;
	
	public String getDownloadCode() {
		return downloadCode;
	}

	public void setDownloadCode(String downloadCode) {
		this.downloadCode = downloadCode;
	}

	public int getRealDownNum() {
		return realDownNum;
	}

	public void setRealDownNum(int realDownNum) {
		this.realDownNum = realDownNum;
	}

	public String getInstartPassword() {
		return instartPassword;
	}

	public void setInstartPassword(String instartPassword) {
		this.instartPassword = instartPassword;
	}

	public int getInstartModel() {
		return instartModel;
	}

	public void setInstartModel(int instartModel) {
		this.instartModel = instartModel;
	}

	public String getAndroidFileUrl() {
		return androidFileUrl;
	}

	public void setAndroidFileUrl(String androidFileUrl) {
		this.androidFileUrl = androidFileUrl;
	}

	public int getDownloadNum() {
		return downloadNum;
	}

	public void setDownloadNum(int downloadNum) {
		this.downloadNum = downloadNum;
	}

	public String getUdidTitle() {
		return udidTitle;
	}

	public void setUdidTitle(String udidTitle) {
		this.udidTitle = udidTitle;
	}

	public String getUdidDescription() {
		return udidDescription;
	}

	public void setUdidDescription(String udidDescription) {
		this.udidDescription = udidDescription;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public Integer getAppType() {
		return appType;
	}

	public void setAppType(Integer appType) {
		this.appType = appType;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getMark() {
		return mark;
	}

	public void setMark(Integer mark) {
		this.mark = mark;
	}

	public String getShop() {
		return shop;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public String getIntroduce() {
		return introduce;
	}

	public void setIntroduce(String introduce) {
		this.introduce = introduce;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}
	
	public String getMinVersion() {
		return minVersion;
	}

	public void setMinVersion(String minVersion) {
		this.minVersion = minVersion;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}

	public String getShowIconUrl() {
		return showIconUrl;
	}

	public void setShowIconUrl(String showIconUrl) {
		this.showIconUrl = showIconUrl;
	}

	public String getShowAppName() {
		return showAppName;
	}

	public void setShowAppName(String showAppName) {
		this.showAppName = showAppName;
	}

	public String getShortDomain() {
		return shortDomain;
	}

	public void setShortDomain(String shortDomain) {
		this.shortDomain = shortDomain;
	}

	public String getScreenshots() {
		return screenshots;
	}

	public void setScreenshots(String screenshots) {
		this.screenshots = screenshots;
	}

	public String getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}

	public int isPush() {
		return push?1:0;
	}

	public void setPush(boolean push) {
		this.push = push;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
}
