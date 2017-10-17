package com.yzx.flow.core.util.licence;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.xiaoleilu.hutool.crypto.asymmetric.KeyType;
import com.xiaoleilu.hutool.crypto.asymmetric.RSA;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.lang.Base64;
import com.xiaoleilu.hutool.util.CharsetUtil;
import com.xiaoleilu.hutool.util.NetUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import com.yzx.flow.core.util.RSAUtils;

/**
 * licence 检测
 * @author liuyufeng
 *
 */
public class LicenceUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(LicenceUtil.class);
	
	//仅打印一次
	private static int printCount = 0;
	//
	/**
	 * 验证licence
	 * @param publicKey
	 * @param licenceSecret
	 * @return
	 */
	public static LicenceVo verifyLicence(String publicKey,String licenceSecret){
		if(StringUtils.isBlank(publicKey)||StringUtils.isBlank(licenceSecret)){
			logger.info("licence error publicKey:"+publicKey+",licenceSecret:"+licenceSecret);
			return null;
		}
		try{
			String rasSecret = "";
			if(licenceSecret.indexOf("~")!=-1){
				rasSecret = licenceSecret.substring(0, licenceSecret.lastIndexOf("~"));
			}
			String base = licenceSecret.substring(licenceSecret.lastIndexOf("~")+1);
			base = base.split(",")[0];
			LicenceVo licence = JSONObject.parseObject(Base64.decode(base), LicenceVo.class);
			//公钥解密
			byte[] decrypt2 = RSAUtils.decryptByPublicKey(Base64.decode(rasSecret), publicKey);
			
			String result = StrUtil.str(decrypt2, CharsetUtil.CHARSET_UTF_8);
			String mac = result.split(",")[0];
			String endTime = result.split(",")[1];
			String password = result.split(",")[2];
			if(printCount == 0){
				licence.setMac(mac);
				licence.setEndTime(endTime);
				licence.setPassword(password);
				logger.info(licence.toString());
				printCount ++;
			}
			String thisMac = NetUtil.getLocalMacAddress();
			if(DateUtil.parse(endTime).after(new Date())){
				//检查数据是否一致
				if(StringUtils.isNotBlank(mac) && mac.toLowerCase().indexOf(thisMac.toLowerCase())!=-1){
					return licence;
				}else{
					logger.info("licence mac:"+result+",thisMac:"+thisMac);
					return null;
				}
			}else{
				logger.info("licence endTime:"+endTime+",thisMac:"+DateUtil.now());
				return null;
			}
		}catch(Exception e){
			logger.error("verifyLicence:",e);
		}
		return null;
	}
}
