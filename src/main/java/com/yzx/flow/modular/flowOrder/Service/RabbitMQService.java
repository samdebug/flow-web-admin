package com.yzx.flow.modular.flowOrder.Service;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yzx.flow.common.persistence.model.CPCallBackBean;

@Service("rabbitMQService")
public class RabbitMQService {
	private static Logger logger = LoggerFactory.getLogger(RabbitMQService.class);
	@Autowired
	private RabbitTemplate queueTemplate;

	public void sentToCTGW(CPCallBackBean callBackBean) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(callBackBean);
		queueTemplate.convertAndSend(json);
	}

	public void sentToCTGW(String flowAppId, String flag, String taskId, String mobile, String status,
			String reportTime, String reportCode, String outTradeNo) throws JsonProcessingException {

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new TreeMap<>();
		map.put("FlowAppId", flowAppId);
		map.put("Flag", flag);
		map.put("TaskID", taskId);
		map.put("Mobile", mobile);
		map.put("Status", status);
		map.put("ReportTime", reportTime);
		map.put("ReportCode", reportCode);
		map.put("OutTradeNo", outTradeNo);
		map.put("Count", 0);
		map.put("Time", System.currentTimeMillis());

		String jsonmap = mapper.writeValueAsString(map);
		logger.debug("发送消息{}至gtgw", jsonmap);
		queueTemplate.convertAndSend(jsonmap);
	}

}
