package com.yzx.flow.modular.customer.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.dao.AreaCodeMapper;
import com.yzx.flow.common.persistence.model.AreaCode;
import com.yzx.flow.modular.customer.service.IAreaCodeService;

/**
 * 
 * <b>Title：</b>AreaCodeService.java<br/>
 * <b>Description：</b> <br/>
 * <b>@author： </b>v5480<br/>
 * <b>@date：</b>2015-09-02 10:17:41<br/>
 * <b>Copyright (c) 2015 szwisdom Tech.</b>
 * 
 */
@Service("areaCodeService")
public class AreaCodeServiceImpl implements IAreaCodeService {
	@Autowired
	private AreaCodeMapper areaCodeMapper;

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#pageQuery(com.yzx.flow.common.page.Page)
	 */
	@Override
	public Page<AreaCode> pageQuery(Page<AreaCode> page) {
		List<AreaCode> list = areaCodeMapper.pageQuery(page);
		page.setDatas(list);
		return page;
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#insert(com.yzx.flow.common.persistence.model.AreaCode)
	 */
	@Override
	public void insert(AreaCode data) {
		areaCodeMapper.insert(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#get(java.lang.String)
	 */
	@Override
	public AreaCode get(String areaCode) {
		return areaCodeMapper.selectByPrimaryKey(areaCode);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#saveAndUpdate(com.yzx.flow.common.persistence.model.AreaCode)
	 */
	@Override
	public void saveAndUpdate(AreaCode data) {
		if (StringUtils.isNotEmpty(data.getAreaCode())) {// 判断有没有传主键，如果传了为更新，否则为新增
			this.update(data);
		} else {
			this.insert(data);
		}
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#update(com.yzx.flow.common.persistence.model.AreaCode)
	 */
	@Override
	public void update(AreaCode data) {
		areaCodeMapper.updateByPrimaryKey(data);
	}

	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#delete(java.lang.String)
	 */
	@Override
	public int delete(String areaCode) {
		return areaCodeMapper.deleteByPrimaryKey(areaCode);
	}
	
	/* (non-Javadoc)
	 * @see com.yzx.flow.modular.customer.service.impl.IAreaCodeService#selectAll(java.lang.String, java.lang.Long, java.lang.Long, java.lang.String, java.lang.String)
	 */
	@Override
	public List<AreaCode> selectAll(String mobileOperator, Long partnerId, Long customerId, String inputStartTime, String inputEndTime) {
	    Map<String, Object> map = new HashMap<String, Object>();
        map.put("mobileOperator", mobileOperator);
        map.put("partnerId", partnerId);
        map.put("customerId", customerId);
        map.put("inputStartTime", inputStartTime);
        map.put("inputEndTime", inputEndTime);
        return areaCodeMapper.selectAll(map);
    }
}