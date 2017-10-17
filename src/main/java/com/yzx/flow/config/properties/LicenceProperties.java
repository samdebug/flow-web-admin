package com.yzx.flow.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * licence配置
 *
 * @author liuyufeng
 * @date 2017-05-24 20:37
 */
@Component
@ConfigurationProperties(prefix = LicenceProperties.PREFIX)
public class LicenceProperties {

    public static final String PREFIX = "licence";
	//公钥
    private String publickey;
    //通行密文
    private String licencesecret;
    
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	public String getLicencesecret() {
		
		return licencesecret;
	}
	public void setLicencesecret(String licencesecret) {
		this.licencesecret = licencesecret;
	}

}
