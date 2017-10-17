package com.yzx.flow.modular.sms.service;

import com.yzx.flow.common.page.Page;
import com.yzx.flow.common.persistence.model.SmsChannelInfo;

public interface ISmsChannelInfoService {

	Page<SmsChannelInfo> pageQuery(Page<SmsChannelInfo> page);

	void insert(SmsChannelInfo data);

	SmsChannelInfo get(Long smsChannelId);

	void saveAndUpdate(SmsChannelInfo data);

	void update(SmsChannelInfo data);

	int delete(Long smsChannelId);

}