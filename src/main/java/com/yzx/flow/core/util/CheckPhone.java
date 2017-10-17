package com.yzx.flow.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("yzx_CheckPhone") // 翻遍自动注入静态资源
public class CheckPhone {
	
	
	private static String PHONE_REG_ALL;
	private static String PHONE_REG_YD;
	private static String PHONE_REG_LT;
	private static String PHONE_REG_DX;
	
	
	public static boolean isMobileNO(String mobiles) {
		if(!isNotNull(mobiles)){
			return false;
		}
		//^((13[0-9])|(14[7])|(15[^4,\\D])|(17[6,7])|(18[0,1,2,3,4,5-9]))\\d{8}$
		Pattern p = Pattern.compile(PHONE_REG_ALL);
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	public final static boolean isNotNull(String str) {
		if(str!=null && !"".equals(str)){
			return true; 
		}else {
			return false; 
		}
		
	}
	public static String getMobileOpr(String mobiles) {
		if (mobiles == null || mobiles.length() < 11) {
			return "NA";
		}
		int pos = mobiles.length() - 11;

		mobiles = mobiles.substring(pos);
		//^((13[3])|(15[3])|(17[7])|(18[1,9,0]))\\d{8}$
		Pattern p = Pattern.compile(PHONE_REG_DX);
		Matcher m = p.matcher(mobiles);

		if (m.matches()) {
			return "DX";
		}
		//^((13[4,5,6,7,8,9])|(14[7])|(15[0,1,2,7,8,9])|(18[2,3,4,7,8]))\\d{8}$
		p = Pattern.compile(PHONE_REG_YD);
		m = p.matcher(mobiles);
		if (m.matches()) {
			return "YD";
		}
		//^((13[0,1,2])|(15[5,6])|(18[5,6]))\\d{8}$
		p = Pattern.compile(PHONE_REG_LT);
		m = p.matcher(mobiles);
		if (m.matches()) {
			return "LT";
		}
		return "NA";
	}
	
	
	
	@Value("${phone.reg.all}")
	public void setPhoneRegALL(String phoneRegALL) {
		PHONE_REG_ALL = phoneRegALL;
	}
	
	@Value("${phone.reg.yd}")
	public void setPhoneRegYD(String phoneRegYD) {
		PHONE_REG_YD = phoneRegYD;
	}
	
	@Value("${phone.reg.lt}")
	public void setPhoneRegLT(String phoneRegLT) {
		PHONE_REG_LT = phoneRegLT;
	}
	
	@Value("${phone.reg.dx}")
	public void setPhoneRegDX(String phoneRegDX) {
		PHONE_REG_DX = phoneRegDX;
	}
	
}
