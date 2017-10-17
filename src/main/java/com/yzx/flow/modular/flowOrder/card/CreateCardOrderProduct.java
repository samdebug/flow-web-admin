package com.yzx.flow.modular.flowOrder.card;

import com.yzx.flow.common.persistence.model.FlowCardBatchInfo;
import com.yzx.flow.common.persistence.model.OrderInfo;
import com.yzx.flow.common.persistence.model.Staff;
import com.yzx.flow.modular.flowOrder.Service.IFlowCardInfoService;

public class CreateCardOrderProduct implements ICreateCard {
	public CreateCardOrderProduct(IFlowCardInfoService flowCardInfoService, int count, OrderInfo orderInfo,
			Long productId, FlowCardBatchInfo flowCardBatchInfo,Staff staff) {
		this.flowCardInfoService = flowCardInfoService;
		this.flowCardBatchInfo = flowCardBatchInfo;
		this.count = count;
		this.productId = productId;
		this.orderInfo = orderInfo;
		this.staff=staff;
	}

	private IFlowCardInfoService flowCardInfoService;
	private FlowCardBatchInfo flowCardBatchInfo;
	private int count;
	private OrderInfo orderInfo;
	private Long productId;
	private Staff staff;
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
		this.batchId=flowCardInfoService.createCard(count, orderInfo, productId, flowCardBatchInfo,staff);
		this.flag=true;
	}

}
