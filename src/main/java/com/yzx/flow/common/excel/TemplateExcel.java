package com.yzx.flow.common.excel;

import java.util.Date;

import com.yzx.flow.core.util.DateUtil;

public enum TemplateExcel implements ITemplateExcel {
	
	
	CUSTOMER_ORDER_DEAL_RECORD(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "客户订单记录.xls";
		}}, "CustomerOrderDealRecord-template.xls"),
	
	
	PARTNER_ORDER_DEAL_RECORD(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "合作伙伴产品协议订单记录.xls";
		}}, "PartnerOrderDealRecord-template.xls"),
	
	
	PARTNER_BALANCE_DAY(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "合作伙伴流量费用账单.xls";
		}}, "PartnerBalanceDay-template.xls"),
	
	
	CUSTOMER_BALANCE_DAY(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "客户流量费用账单.xls";
		}}, "CustomerBalanceDay-template.xls"),
	
	
	/**
	 * 批量充值结果.xls
	 */
	RECHARGE_BATCH_DETAIL(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "批量充值结果.xls";
		}}, "BatchRecharge-template.xls"),
	
	
	/**
	 * app接入信息
	 */
	FLOW_APP_INFO_LIST(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "appInfo.xls";
		}}, "appList-template.xls"),

	
	/**
	 * 订单记录.xls（指定订单）
	 */
	ORDER_INFO(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "订单记录.xls";
		}}, "order-template.xls"),
	
	
	/**
	 * 订单记录.xls （全部）
	 */
	ORDER_LIST(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "order.xls";
		}}, "order-template.xls"),
	
	
	/**
	 * 客户账户明细
	 */
	CUSTOMER_RECHARGE_DETAIL(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "客户账户明细" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
		}}, "CustomerRecharge-template.xls"),
	
	
	/**
	 * 通道供应商充值记录
	 */
	CHANNEL_SUPPLIER_RECHARGE(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "供应商充值记录" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
		}}, "SupplierRecharge-template.xls"),
	
	
	/**
	 * 上游供应商充值记录
	 */
	CHANNEL_COMPANY_RECHARGE(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "上游供应商充值记录" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
		}}, "CompanyRecharge-template.xls"),
	
	
	/**
	 * 上游供应商记录
	 */
	CHANNEL_COMPANY(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "上游供应商列表" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xls";
		}}, "channelCompany-template.xls"),
	
	
	/**
	 * 客户流量结算单.xls
	 */
	CUSTOMER_BALANCE_MONTH(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "客户流量结算单.xls";
		}}, "CustomerBalanceMonth-template.xls"),
	
	/**
	 * 资金流水明细.xls
	 */
	ACCOUNT_DETAIL(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "资金流水明细表" + DateUtil.format(new Date(), "yyyyMMdd") + ".xls";
		}}, "AccountDetail-template.xls"),
	
	/**
	 * 合作伙伴流量结算单.xls
	 */
	PARTNER_BALANCE_MONTH(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "合作伙伴流量结算单.xls";
		}}, "PartnerBalanceMonth-template.xls"),
	
		/**
		 * 合作伙伴流量结算单.xls
		 */
		AccessChannel_Info(new ITemplateExcel.INameBuilder() {
			@Override
			public String getName() {
				return "接入通道信息.xls";
			}}, "AccessChannelInfo-template.xls"),
		
		
		
	/**
	 * 批量充值手机号码模版.xls
	 */
	RECHARGE_MOBILE_TEMPLATE(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "recharge-download-template.xls";
		}}, "recharge-download-template.xls"),
	/**
	 * 流量订单导出模板
	 */
	FLOWORDERINFO_TEMPLATE(
			new ITemplateExcel.INameBuilder() {
				@Override
				public String getName() {
					return "订单记录.xls";
				}}, "FlowOrderInfo-template.xls"),
	FLOWORDERINFOEx_TEMPLATE(
			new ITemplateExcel.INameBuilder() {
				@Override
				public String getName() {
				return "异常订单记录.xls";
		}}, "FlowOrderInfo-template.xls"),
	
	/**
	 * 客户、合作伙伴充值记录模板
	 */
	PARTNER_CUSTOMER_RECHARGE_TEMPLATE(new ITemplateExcel.INameBuilder() {
		@Override
		public String getName() {
			return "充值记录.xls";
		}}, "p-c-recharge-template.xls"),
;	
	
	
	private INameBuilder nameBuilder;
	
	private String templateName;
	
	
	TemplateExcel(INameBuilder builder, String templateName) {
		this.nameBuilder = builder;
		this.templateName = templateName;
	}
	
	@Override
	public String getDisplayName() {
		return nameBuilder.getName();
	}

	@Override
	public String getTemplateName() {
		return templateName;
	}
	
	

}
