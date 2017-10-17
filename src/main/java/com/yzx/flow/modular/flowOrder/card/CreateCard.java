package com.yzx.flow.modular.flowOrder.card;

import java.util.Map;

import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.modular.flowOrder.Service.IFlowCardInfoService;

public class CreateCard implements ICreateCard {
	public CreateCard(IFlowCardInfoService flowCardInfoService, Map<String, Integer> countMap, Long customId,Long orderId,FlowCardBatchInfo flowCardBatchInfo,Staff staff) {
		this.flowCardInfoService = flowCardInfoService;
		this.staff = staff;
		this.customId = customId;
		this.countMap=countMap;
		this.orderId=orderId;
		this.flowCardBatchInfo=flowCardBatchInfo;
	}

	private IFlowCardInfoService flowCardInfoService;
	private Map<String,Integer> countMap;
	private Staff staff;
	private Long customId;
	private Long orderId;
	private FlowCardBatchInfo flowCardBatchInfo;
	private Long batchId;
	private boolean flag=false;
	

	public Long getBatchId() {
		return batchId;
	}



	public void setBatchId(Long batchId) {
		this.batchId = batchId;
	}
	
	



	public boolean getFlag() {
		return flag;
	}



	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	@Override
	public void run() {
		this.batchId=flowCardInfoService.createCard(countMap, customId,orderId,flowCardBatchInfo, staff);
		this.flag=true;
	}

}
