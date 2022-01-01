package com.bootdo.signature.util;

public class Authorize {

    private String csr;

    private String p8;

    private String iss;

    private String kid;
    
    private String bundleIds;

    private String cerId;
    
    private String cerType;
    
    private String packageName;

    public Authorize(String p8, String iss, String kid, String csr) {
        this.p8 = p8;
        this.iss = iss;
        this.kid = kid;
        this.csr = csr;
    }

    public Authorize(String p8, String iss, String kid,String bundleIds,String cerId,String cerType) {
        this.p8 = p8;
        this.iss = iss;
        this.kid = kid;
        this.bundleIds = bundleIds;
        this.cerId = cerId;
        this.cerType = cerType;
    }
    
    public String getCerType() {
		return cerType;
	}

	public void setCerType(String cerType) {
		this.cerType = cerType;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
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

    public String getCsr() {
        return csr;
    }

    public void setCsr(String csr) {
        this.csr = csr;
    }

    @Override
    public String toString() {
        return "Authorize{" +
                "csr='" + csr + '\'' +
                ", p8='" + p8 + '\'' +
                ", iss='" + iss + '\'' +
                ", kid='" + kid + '\'' +
                '}';
    }

	public String getBundleIds() {
		return bundleIds;
	}

	public void setBundleIds(String bundleIds) {
		this.bundleIds = bundleIds;
	}

	public String getCerId() {
		return cerId;
	}

	public void setCerId(String cerId) {
		this.cerId = cerId;
	}
}
