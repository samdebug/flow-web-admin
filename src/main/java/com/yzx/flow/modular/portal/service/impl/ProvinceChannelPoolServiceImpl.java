package com.yzx.flow.modular.portal.service.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.persistence.model.ProvinceChannelPool;
import com.yzx.flow.common.util.DateUtil;
import com.yzx.flow.modular.portal.service.IProvinceChannelPoolService;
import com.yzx.flow.modular.system.dao.ProvinceChannelPoolDao;

@Service
public class ProvinceChannelPoolServiceImpl implements IProvinceChannelPoolService {
	
	@Autowired
	private ProvinceChannelPoolDao provinceChannelPoolDao;
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IProvinceChannelPoolService#selectProvinceChannelPoolInfo(java.util.Map)
	 */
	public boolean selectProvinceChannelPoolInfo(Map<String, Object> map){
		//处于月末两天
		if(checkTime()){
			ProvinceChannelPool provinceChannelPool = provinceChannelPoolDao.getProvinceChannelPoolInfo(map);
			if(null == provinceChannelPool){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	};
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.portal.service.impl.IProvinceChannelPoolService#checkTime()
	 */
	public boolean checkTime(){
		Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, c.get(Calendar.YEAR));
        c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date day1 = c.getTime();
        c.add(Calendar.DAY_OF_MONTH, -1);
        Date day2 = c.getTime();
        Date date = new Date();
        if(isSameDay(day1, date) || isSameDay(day2, date)){
        	return true;
        }else{
        	return false;
        }
	}
	
	public static boolean isSameDay(Date day1, Date day2) {
	    SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD_EN);
	    String ds1 = sdf.format(day1);
	    String ds2 = sdf.format(day2);
	    if (ds1.equals(ds2)) {
	        return true;
	    } else {
	        return false;
	    }
	}
}
