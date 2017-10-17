package com.yzx.flow.core.util;

import com.yzx.flow.common.persistence.model.MobileHomeInfo;
import com.yzx.flow.modular.system.dao.MobileHomeInfoDao;

/**
 * 
 * @author Liulei
 *
 */
public class MobileHomeInfoUtil {
	
	
	
	/**
	 * 获取号码归属信息。
	 * 
	 * @param mobileNo
	 * @return
	 */
	public static MobileHomeInfo getMobileHomeInfo(String mobileNo) {
		try {
			if (CheckPhone.isMobileNO(mobileNo)) {
				String operator = CheckPhone.getMobileOpr(mobileNo);
				String mobilePrefix = mobileNo.substring(0, 7);
				MobileHomeInfo info = SpringContextHolder.getBean(MobileHomeInfoDao.class).selectByPrimaryKey(mobilePrefix);
				if (null != info) {
					info.setOperator(operator);
				}
				return info;
			}
		} catch (Exception ee) {
			ee.printStackTrace();// ignore exception
		}
		return null;
	}
	

}
